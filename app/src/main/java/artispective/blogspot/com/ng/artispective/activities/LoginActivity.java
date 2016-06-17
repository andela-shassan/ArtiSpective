package artispective.blogspot.com.ng.artispective.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vstechlab.easyfonts.EasyFonts;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.interfaces.LoginAuthentication;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;

public class LoginActivity extends AppCompatActivity implements OnClickListener,
        LoginAuthentication {

    private EditText email, password;
    private View mProgressView;
    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.emailAddress);
        mProgressView = findViewById(R.id.login_progress);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        TextView app_name = (TextView) findViewById(R.id.login_app_name);
        app_name.setTypeface(EasyFonts.tangerineBold(this));

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO){
                    attemptLogin();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void attemptLogin() {
        Helper.hideSoftKeyboard(this, signInButton);
        email.setError(null);
        password.setError(null);

        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            this.password.setError(getString(R.string.error_empty_password));
            focusView = this.password;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            this.email.setError(getString(R.string.error_invalid_password));
            focusView = this.password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError(getString(R.string.error_field_required));
            focusView = this.email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            this.email.setError(getString(R.string.error_invalid_email));
            focusView = this.email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            UserAuthentication.loginUser(this, email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_in_button:
                attemptLogin();
                break;
            default:
                break;
        }
    }

    public void switchToRegister(View view) {
        Helper.launchActivity(this, RegisterActivity.class);
    }

    public void switchToForgetPassword(View view) {

    }

    @Override
    public void onSuccess() {
        Helper.showToast("Login Successful");
        Helper.launchActivity(this, HomeActivity.class);
        finish();
    }

    @Override
    public void onFailure() {
        Helper.showToast("Something went wrong. Please try again");
        recreate();
    }


}

