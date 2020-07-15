package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
}