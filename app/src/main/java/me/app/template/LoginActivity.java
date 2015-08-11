package me.app.template;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import me.app.template.utils.InternetStatus;


public class LoginActivity extends NoAuthActivity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private TextView mErrorField;
    private ProgressBar spinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        mUsernameField = (EditText) findViewById(R.id.login_email);
        mPasswordField = (EditText) findViewById(R.id.login_password);
        mErrorField = (TextView) findViewById(R.id.error_messages);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        Parse.initialize(this);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
	/** Get twitter auth data - In parse create new table , in this template I have called it "tw" & add string type columns "keyid" & "secretid"
	 *  The benefit of this method is that if you need to revoke twitter keys, you just need to modify them in the table only. No need to for new app update.
	 *  If you want to avoid doing it this way, then simply remove whole of the below code with just this one line & pass your keys as parameters.
	 *  ParseTwitterUtils.initialize(tw_consumer_key, tw_consumer_secret);
	 *  But with this you will have to push an app update in case you revoke your twitter keys.
	 */
        ParseQuery<ParseObject> qtw = ParseQuery.getQuery("tw");
        qtw.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject obj, ParseException e) {
                if(e==null){
                        String tw_consumer_key = obj.get("keyid").toString();
                        String tw_consumer_secret = obj.get("secretid").toString();
                        ParseTwitterUtils.initialize(tw_consumer_key, tw_consumer_secret);
                }
                else {
                    Log.d("Tw: Error", e.getMessage());
                }
            }

        });
    }

    public void signInNormal(final View v) {
        v.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mUsernameField.getWindowToken(), 0);

        if (mUsernameField.getText().length()==0 || mPasswordField.getText().length()==0){
            v.setEnabled(true);
            spinner.setVisibility(View.GONE);
            mErrorField.setText("All the fields are required. Please fill them.");
            return;
        }

        ParseUser.logInInBackground(mUsernameField.getText().toString(),
                mPasswordField.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            boolean verify = user.getBoolean("emailVerified");
                            if (!verify) {
                                spinner.setVisibility(View.GONE);
                                mErrorField.setText("Are you sure you're verified? Verify by opening link we have sent to your registered email & then login again.");
                            } else {
                                spinner.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Signup failed. Look at the ParseException to see
                            // what happened.
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
                                case ParseException.OBJECT_NOT_FOUND:
                                    mErrorField
                                            .setText("Sorry, those credentials were invalid.");
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
    }

    public void showRegistration(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void forgpassActivity(View v) {
        Intent intent = new Intent(this, ForgotPassActivity.class);
        startActivity(intent);
        finish();
    }


    public void signInTwitter(final View v) {
        v.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("RYC", "Uh oh. The user cancelled the Twitter login.");
                    spinner.setVisibility(View.GONE);
                    v.setEnabled(true);
                } else if (user.isNew()) {
                    Log.d("RYC", "User signed up and logged in through Twitter!");
                    String userscreenName = ParseTwitterUtils.getTwitter().getScreenName();
                    user.put("screenName", userscreenName);
                    try {
                        user.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spinner.setVisibility(View.GONE);
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.d("RYC", "User logged in through Twitter!");
                    String userscreenName = ParseTwitterUtils.getTwitter().getScreenName();
                    user.put("screenName", userscreenName);
                    try {
                        user.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spinner.setVisibility(View.GONE);
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}
