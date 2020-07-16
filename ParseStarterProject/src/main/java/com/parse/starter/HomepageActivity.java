package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    String currentUser;
    UserAdapter adapter;
    ArrayList<UserObject> users;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Get current username
        currentUser = ParseUser.getCurrentUser().getUsername();

        TextView userDetails = (TextView) findViewById(R.id.userDetails);
        userDetails.setText("Logged in as: " + currentUser);

        users = new ArrayList<UserObject>();
        adapter = new UserAdapter(HomepageActivity.this, users);
        listView = (ListView) findViewById(R.id.list);

        ParseQuery<ParseUser> query = ParseUser.getQuery(); //INSTEAD OF <ParseObject> & ParseQuery.getQuery("ExampleObject"), ITS <ParseUser> & ParseUser.getQuery() respectively!!!!!!!

        query.whereNotEqualTo("username", currentUser); //DON'T INCLUDE CURRENT USER
        query.addAscendingOrder("username"); //SORT BY ORDER

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        String username = user.getUsername();
                        users.add(new UserObject(username));
                    }
                    listView.setAdapter(adapter);
                }
                else {
                    e.printStackTrace();
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        Button logout = (Button) findViewById(R.id.logOutButton);
        logout.setOnClickListener(new View.OnClickListener() { //TO LOG OUT
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logged out " + currentUser, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photo:

                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WASN'T GRANTED,...
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1); //...THEN ASK FOR PERMISSION ( IN onRequestPermissionsResult() )
                } else { //IF PERMISSION IS GRANTED,...
                    selectPhoto(); //...THEN ALLOW USERS TO SELECT PHOTOS
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** here we act on the result we got from the intent code in selectPhoto() **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); //COMPRESSES IMG AND FORMATS IT

                byte[] byteArray = stream.toByteArray();
                ParseFile parseFile = new ParseFile("image.png", byteArray);

                ParseObject object = new ParseObject("Image"); // PASS INTO PARSE
                object.put("image", parseFile); //PASS IN THE IMG
                object.put("username", ParseUser.getCurrentUser().getUsername()); //PASS IN THE USER WHO UPLOADED THE IMG
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Image uploaded to Parse!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Couldn't upload image to Parse - check and try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** asks for user permission to access photos **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WAS GRANTED
                selectPhoto();
            }
        }
    }

    /** this intent code will open up the "Select photo" pop up **/
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

}