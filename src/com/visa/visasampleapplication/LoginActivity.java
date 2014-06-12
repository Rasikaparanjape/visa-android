package com.visa.visasampleapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import net.authorize.Merchant;
//import net.authorize.android.AuthNetActivityBase;
//import net.authorize.android.SimpleActivity;
//import net.authorize.auth.PasswordAuthentication;
//import net.authorize.data.mobile.MobileDevice;
//import net.authorize.auth.SessionTokenAuthentication;
//import net.authorize.data.mobile.MobileDevice;
//import net.authorize.util.StringUtils;
//import net.authorize.xml.MessageType;







import java.util.ArrayList;

/**
 * Activity which displays a login screen to the user. */
public class LoginActivity extends Activity { //change to AuthNetActivityBase
    /** A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system. */
	private ArrayList<String> dummyCredentials = new ArrayList<String>();


    /** The default email to populate the email field with. */
    public static final String EXTRA_LOGINID = "com.example.android.authenticatordemo.extra.LOGINID";

    /** Keep track of the login task to ensure we can cancel it if requested. */
    private UserLoginTask mAuthTask = null;

    /** Values for loginID and password at the time of the login attempt. */
    private String mLoginID;
    private String mPassword;
    
    
    /** UI references. */
    private EditText mLoginIDView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getActionBar().show();
        // Set up the login form.
        mLoginID = getIntent().getStringExtra(EXTRA_LOGINID);
        mLoginIDView = (EditText) findViewById(R.id.loginID);
        mLoginIDView.setText(mLoginID);

        mPasswordView = (EditText) findViewById(R.id.password);

        // respond correlated to pressing return on the key pad and logging in
        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                            KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                        	Log.d("Tricia's Tag","onEditorAction");
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        // respond to login button
        findViewById(R.id.log_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    	Log.d("Tricia's Tag", "onClick");
                        attemptLogin();
                    }
                });
    }

    /** Disable the back button. */
    @Override
    public void onBackPressed() {
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        Log.d("Tricia's Tag", "Inflated the menu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//handle item selection
    	if (item.getItemId() == R.id.dev_info) {
    		//TODO: HANDLE DEV INFO
    		openDevInfo();
    		return true;
    	} else {
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    /** Opens an AlertDialog with developer information. */
    public void openDevInfo() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	TextView title = new TextView(this);
    	title.setText("Developer Information");
    	title.setGravity(Gravity.CENTER);
    	title.setPadding(10, 10, 10, 10);
    	title.setTextColor(Color.WHITE);
    	title.setTextSize(20);
    	builder.setCustomTitle(title);
    	String message = "This application utilizes the Authorize.Net SDK available on GitHub"
    			+ " under the username AuthorizeNet. Authorize.Net is a wholly owned subsidiary of Visa.";
    	builder.setMessage(message).setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
    	AlertDialog info = builder.create();
    	info.show();
    	//TODO: center the title
    	TextView messageView = (TextView) info.findViewById(android.R.id.message);
    	messageView.setTextSize(13);
    	messageView.setGravity(Gravity.CENTER);
    }

    /** Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made. */
    public void attemptLogin() {
    	Log.d("Tricia's Tag", "entering attemptLogin");
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mLoginID = mLoginIDView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid log in ID.
        if (TextUtils.isEmpty(mLoginID)) {
            mLoginIDView.setError(getString(R.string.error_field_required));
            focusView = mLoginIDView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /** Shows the progress UI and hides the login form. */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /** Represents an asynchronous login/registration task used to authenticate
     * the user. */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
        	dummyCredentials.add("test1@visa.com:test1password");
        	dummyCredentials.add("test2@visa.com:test2password");
            Log.d("Tricia's Tag", "first print: " + dummyCredentials.toString());
        	boolean contains = false;
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            //TODO: ADD DATA NETWORK CONNECTED STUFF 

            for (String credential : dummyCredentials) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mLoginID)) {
                    // Account exists, return true if the password matches.
                	contains = true;
                    return pieces[1].equals(mPassword);
                }
            }
            // if account does not exist, create account and return true
            /** if (!contains) {
            	dummyCredentials.add(mLoginID + ":" + mPassword);
            	Log.d("Tricia's Tag", "second print: " + dummyCredentials.toString());
            } */
            return true;
            // TODO: register the new account here.
        }

        
        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("Tricia's tag", "onPostExecute");
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //TODO: Link to the credit card page
            	Intent chargeCardIntent = new Intent(LoginActivity.this, ChargeCardActivity.class);
            	startActivity(chargeCardIntent);
                finish();

            } else {
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
