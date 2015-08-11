package me.app.template;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseUser;
import me.app.template.utils.InternetStatus;

public class ChangePassActivity extends AppCompatActivity {

    private EditText mPasswordField;
    private ProgressBar spinner;
    private TextView mErrorField,helptxt;
    private Button mbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        mPasswordField = (EditText) findViewById(R.id.field_password);
        mErrorField = (TextView) findViewById(R.id.error_messages);
        helptxt = (TextView) findViewById(R.id.change_help_text);
        mbutton = (Button) findViewById(R.id.changepassButton);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        if (InternetStatus.getInstance(this).isOnline(this)) {
            // Pass
        } else {
            mErrorField.setText("No internet connection available. Please check your connection settings.");
        }

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            if (!currentUser.containsKey("email")) {
                mPasswordField.setVisibility(View.GONE);
                mbutton.setVisibility(View.GONE);
                helptxt.setText("You are logged in via Twitter. " +
                        "To reset password, visit Twitter.com and change your password there. " +
                        "We don't store any twitter information apart from your twitter username.");
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void initChangePass(final View v) {

        if (mPasswordField.getText().length() == 0) {
            mPasswordField.setText("Password can't be empty!");
            return;
        }
        v.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);
        mErrorField.setText("");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
        ParseUser curUser = ParseUser.getCurrentUser();
        curUser.setPassword(mPasswordField.getText().toString());
        curUser.saveInBackground();
        spinner.setVisibility(View.GONE);
    }
}
