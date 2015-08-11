package me.app.template;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class RegisterActivity extends NoAuthActivity {

    private EditText mUsernameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mErrorField;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        mUsernameField = (EditText) findViewById(R.id.register_username);
        mEmailField = (EditText) findViewById(R.id.register_email);
        mPasswordField = (EditText) findViewById(R.id.register_password);
        mErrorField = (TextView) findViewById(R.id.error_messages);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(final View v) {
        if (mUsernameField.getText().length() == 0
                || mPasswordField.getText().length() == 0 || mEmailField.getText().length() == 0) {

                mErrorField.setText("All the fields are required. Please fill them.");
            return;
        }

        v.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mEmailField.getWindowToken(), 0);
        ParseUser user = new ParseUser();
        user.put("screenName", mUsernameField.getText().toString());
        user.setUsername(mUsernameField.getText().toString());
        user.setEmail(mEmailField.getText().toString());
        user.setPassword(mPasswordField.getText().toString());
        mErrorField.setText("");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RegisterActivity.this, "Please check your mail for confirmation.",
                            Toast.LENGTH_LONG).show();
                    spinner.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            mErrorField
                                    .setText("Sorry, this username has already been taken.");
                            break;
                        case ParseException.USERNAME_MISSING:
                            mErrorField
                                    .setText("Sorry, you must supply a username to register.");
                            break;
                        case ParseException.PASSWORD_MISSING:
                            mErrorField
                                    .setText("Sorry, you must supply a password to register.");
                            break;
                        case ParseException.CONNECTION_FAILED:
                            mErrorField
                                    .setText("Internet connection was not found. Please see your connection settings.");
                            break;
                        default:
                            mErrorField.setText(e.getLocalizedMessage());
                            break;
                    }
                    v.setEnabled(true);
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground(
                new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            /*Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    R.string.alert_dialog_success,
                                    Toast.LENGTH_SHORT);
                            toast.show();*/
                        } else {
                            e.printStackTrace();

                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    R.string.alert_dialog_failed,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }

    public void showLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
