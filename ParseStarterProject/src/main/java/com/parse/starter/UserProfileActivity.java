package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    String userOfProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        userOfProfile = intent.getStringExtra("user");

        setTitle(userOfProfile); //CHANGES TEXT ON TAB

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText("Profile: " + userOfProfile);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");

        query.whereEqualTo("username", userOfProfile); //TARGETS THE IMAGES OF userOfProfile ONLY!
        query.orderByDescending("createdAt"); // ORDERS BY TIME OF UPLOAD!

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) { //LOOPS THROUGH EACH IMAGE OF userOfProfile, CREATES AN IMAGEVIEW, AND SETS THE IMAGE TO IT.
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) { //IF IMGS WERE FOUND...
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0 , data.length); //GETS THE IMAGE!
                                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                                    ImageView imageView = new ImageView(getApplicationContext()); // We will create ImageView here instead of in the layout. This is because ImageView is only needed if images were uploaded by userOfProfile to Parse
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            250,
                                            375
                                    ));
                                    imageView.setImageBitmap(bitmap);
                                    linearLayout.addView(imageView);
                                }
                            }
                        });
                    }
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}