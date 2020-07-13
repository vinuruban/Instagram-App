/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  /**
   * PREREQUISITE: START UP PARSE SERVER - INSTRUCTION ARE FOUND HERE: GDRIVE > AppDev > Udemy > Section 8 - Instagram Clone! > Setting Up Parse On AWS
   * **/

  EditText username;
  EditText password;
  TextView loginStatus;
  Button logOut;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    username = (EditText) findViewById(R.id.username);
    password = (EditText) findViewById(R.id.password);

    /** SIGN UP USER **/
    TextView signUp = (TextView) findViewById(R.id.signUp);
    signUp.setOnClickListener(new AdapterView.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
          Toast.makeText(getApplicationContext(), "Username and password needed", Toast.LENGTH_SHORT).show();
        }
        else {
          ParseUser user = new ParseUser();

          user.setUsername(username.getText().toString());
          user.setPassword(password.getText().toString());

          user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
                Toast.makeText(getApplicationContext(), "Signed up", Toast.LENGTH_SHORT).show();
              }
              else {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      }
    });



    /** LOGIN USER **/
    Button login = (Button) findViewById(R.id.loginButton);
    login.setOnClickListener(new AdapterView.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
          Toast.makeText(getApplicationContext(), "Username and password needed", Toast.LENGTH_SHORT).show();
        }
        else {
          ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
              if (user != null) {
                checkStatus();
                logOut = (Button) findViewById(R.id.logOutButton);
                logOut.setVisibility(View.VISIBLE);

                /** NOW THAT LOG OUT BUTTON IS VISIBLE, SET LISTENER ON THE BUTTON **/
                logOut.setOnClickListener(new AdapterView.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    logOut.setVisibility(View.GONE);
                    ParseUser.logOut(); //LOGS USER OUT!!!!
                    checkStatus();
                  }
                });

              }
              else {
                Toast.makeText(getApplicationContext(), "User does not exist - please sign up", Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      }
    });



    loginStatus = (TextView) findViewById(R.id.loginStatus);
    checkStatus();



    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  /** CHECK WHETHER A USER IS LOGGED IN OR NOT **/
  public void checkStatus() {
    if (ParseUser.getCurrentUser() != null) {
      loginStatus.setText("Status: " + username.getText().toString() + " is logged in");
    } else {
      loginStatus.setText("Status: logged out state");
    }
  }

}