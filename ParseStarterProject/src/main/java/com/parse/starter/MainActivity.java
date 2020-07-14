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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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
    TextView signUp = (TextView) findViewById(R.id.signUpLink);
    signUp.setOnClickListener(this); //note that we are coding the Listener differently to minimise overriding onClick() method. We pass "this" since this class implements View.OnClickListener. This way, onClick() is overridden only once to save code!!!

    /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
    ImageView logoImageView = (ImageView) findViewById(R.id.logo);
    RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
    logoImageView.setOnClickListener(this); //read note in SIGN UP USER
    backgroundLayout.setOnClickListener(this); //read note in SIGN UP USER

    /** TO CHECK LOGIN/LOGOUT STATE **/
    loginStatus = (TextView) findViewById(R.id.loginStatus);
    checkStatus();

    /** WHEN USER HITS ENTER AFTER FILLING PASSWORD... **/
    password.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
          loginUser(view);
          closeKeyboard(); //close keyboard after logging in
        }
        return false;
      }
    });

    /** NEEDED FOR THE PARSE SERVER **/
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }



  /** THE ONLY onClick() THAT IS OVERRIDDEN. ALL THE ONCLICKLISTENER CODE GOES HERE **/
  @Override
  public void onClick(View view) {
    /** SIGN UP USER **/
    if (view.getId() == R.id.signUpLink) {
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
    } /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
    else if (view.getId() == R.id.logo || view.getId() == R.id.backgroundLayout) {
      closeKeyboard();
    }
  }

  /** LOGIN USER **/
  public void loginUser(View view) {
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
          }
          else {
            Toast.makeText(getApplicationContext(), "User does not exist - please sign up", Toast.LENGTH_SHORT).show();
          }
        }
      });
    }
  }

  /** LOG OUT USER **/
  public void logoOutUser(View view) {
    logOut.setVisibility(View.GONE);
    ParseUser.logOut(); //LOGS USER OUT!!!!
    checkStatus();
  }

  /** CHECK WHETHER A USER IS LOGGED IN OR NOT **/
  public void checkStatus() {
    if (ParseUser.getCurrentUser() != null) {
      loginStatus.setText("Status: " + username.getText().toString() + " is logged in");
    } else {
      loginStatus.setText("Status: logged out state");
    }
  }

  /** TO LET KEYBOARD DISAPPEAR WHEN CLICKED OUTSIDE **/
  public void closeKeyboard() {
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
  }


}