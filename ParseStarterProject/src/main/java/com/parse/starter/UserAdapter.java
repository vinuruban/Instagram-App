package com.parse.starter;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserObject> {

    UserObject currentUser;

    public UserAdapter(Activity context, ArrayList<UserObject> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Here we are overriding a method from the ArrayAdapter class!
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        currentUser = getItem(position);

        TextView userTextView = (TextView) listItemView.findViewById(R.id.userTextView);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        userTextView.setText(currentUser.getUsername());

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
