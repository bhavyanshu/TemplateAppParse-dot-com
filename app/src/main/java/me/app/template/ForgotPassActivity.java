package me.app.template;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import me.app.template.utils.InternetStatus;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText mEmailField;
    private ProgressBar spinner;
    private TextView mErrorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mErrorField = (TextView) findViewById(R.id.error_messages);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        if (InternetStatus.getInstance(this).isOnline(this)) {
            // Pass
        } else {
            mErrorField.setText("No internet connection available. Please check your connection settings.");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPassActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void initForgotPass(final View v) {

        if (mEmailField.getText().length() == 0) {
            mErrorField.setText("Please enter a valid email address.");
            return;
        }

        v.setEnabled(false);
        spinner.setVisibility(View.VISIBLE);
        mErrorField.setText("");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mEmailField.getWindowToken(), 0);

        ParseUser.requestPasswordResetInBackground(mEmailField.getText().toString(), new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassActivity.this, "Password reset instructions sent!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPassActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                    // An email was successfully sent with reset instructions.
                } else {
                    v.setEnabled(true);
                    spinner.setVisibility(View.GONE);
                    mErrorField.setText("Sorry, password reset failed. Please try again.");
                }
            }
        });
    }
}
