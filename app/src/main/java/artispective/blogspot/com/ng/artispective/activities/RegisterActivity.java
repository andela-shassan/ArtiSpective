package artispective.blogspot.com.ng.artispective.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.interfaces.RegisterAuthentication;
import artispective.blogspot.com.ng.artispective.models.User;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;

public class RegisterActivity extends AppCompatActivity implements RegisterAuthentication {
    private EditText fullName, email, occupation, password, confirmPassword, phoneNumber;
    private String userName, userEmail, userOccupation, userPassword, cPassword, userPhoneNumber;
    private View progressView;
    private Button signUPButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = new User();
        getViewReference();

        Intent intent = getIntent();
        if (intent.hasExtra("user")){
            user = intent.getExtras().getParcelable("user");
            if (user != null){
                String title = getResources().getString(R.string.update_profile_title);
                setTitle(title);
                signUPButton.setText(title);
                setUpUpdateProfile(user);
            }
        }

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO){
                    attemptSignUp();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void getViewReference() {
        this.fullName = (EditText) findViewById(R.id.full_name);
        this.email = (EditText) findViewById(R.id.register_email);
        this.occupation = (EditText) findViewById(R.id.craft);
        this.password = (EditText) findViewById(R.id.register_password);
        this.confirmPassword = (EditText) findViewById(R.id.confirm_password);
        this.phoneNumber = (EditText) findViewById(R.id.phone_number);
        signUPButton = (Button) findViewById(R.id.sign_up_button);
        this.progressView = findViewById(R.id.register_progress);
        TextView appName = (TextView) findViewById(R.id.reg_app_name);
        appName.setTypeface(EasyFonts.tangerineBold(this));
        signUPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        setErrorToNull();

        userName = this.fullName.getText().toString().trim();
        userEmail = this.email.getText().toString().trim();
        userOccupation = this.occupation.getText().toString().trim();
        userPassword = this.password.getText().toString().trim();
        cPassword = this.confirmPassword.getText().toString().trim();
        userPhoneNumber = this.phoneNumber.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            this.fullName.setError(getString(R.string.error_empty_full_name));
            focusView = this.fullName;
            cancel = true;
        }else if (!isValidUsername(userName)) {
            this.email.setError(getString(R.string.error_full_name_short));
            focusView = this.fullName;
            cancel = true;
        }

        if (TextUtils.isEmpty(userPhoneNumber)) {
            this.phoneNumber.setError(getString(R.string.error_field_required));
            focusView = this.phoneNumber;
            cancel = true;
        }else if (!isPhoneNumberValid(userPhoneNumber)) {
            this.phoneNumber.setError(getString(R.string.error_phone_number_invalid));
            focusView = this.phoneNumber;
            cancel = true;
        }

        if (TextUtils.isEmpty(userOccupation)) {
            this.occupation.setError(getString(R.string.error_field_required));
            focusView = this.occupation;
            cancel = true;
        } else if (!isOccupationValid(userOccupation)) {
            this.occupation.setError(getString(R.string.error_occupation));
            focusView = this.occupation;
            cancel = true;
        }

        if (TextUtils.isEmpty(userEmail)) {
            this.email.setError(getString(R.string.error_field_required));
            focusView = this.email;
            cancel = true;
        } else if (!isEmailValid(userEmail)) {
            this.email.setError(getString(R.string.error_invalid_email));
            focusView = this.email;
            cancel = true;
        }

        if (TextUtils.isEmpty(userPassword)) {
            this.password.setError(getString(R.string.error_field_required));
            focusView = this.password;
            cancel = true;
        } else if (!isPasswordValid(userPassword)) {
            this.password.setError(getString(R.string.error_invalid_password));
            focusView = this.password;
            cancel = true;
        }

        if (TextUtils.isEmpty(cPassword)) {
            this.confirmPassword.setError(getString(R.string.error_field_required));
            focusView = this.confirmPassword;
            cancel = true;
        } else if (!isConfirmPasswordValid(cPassword)) {
            this.confirmPassword.setError(getString(R.string.error_invalid_confirm_password));
            focusView = this.confirmPassword;
            cancel = true;
        }



        if (cancel) {
            focusView.requestFocus();
        } else {
            if (ConnectionChecker.isConnected()) {
                String button = String.valueOf(signUPButton.getText());
                showProgress(true);
                String[] names = userName.split(" ");
                String lastName = names[0];
                String otherNames = "";
                for (int i = 1; i < names.length; i++){
                    otherNames += names[i]+" ";
                }
                user.setFirstName(otherNames);
                user.setLastName(lastName);
                user.setEmailAddress(userEmail);
                user.setPassword(userPassword);
                user.setPhoneNumber(userPhoneNumber);
                user.setCraft(userOccupation);

                if (getTitle().equals("Register")){
                    UserAuthentication.register(this, user);
                }else if (getTitle().equals("Update Profile") || button.equals("Update Profile")) {
                    UserAuthentication.updateUserProfile(this, user);
                }
            }
            else{
                ConnectionChecker.showNoNetwork();
            }
        }

    }

    public void showProgress(boolean visible) {
        if (visible){
            signUPButton.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        }else{
            signUPButton.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }
    }

    private boolean isPhoneNumberValid(String userPhoneNumber) {
        return userPhoneNumber.length() >= 11;
    }

    private boolean isConfirmPasswordValid(String userConfirmPassword) {
        return userConfirmPassword.matches(this.userPassword);
    }

    private boolean isPasswordValid(String userPassword) {
        return userPassword.length() >= 4;
    }

    private boolean isOccupationValid(String occupation) {
        return occupation.length() > 2;
    }

    private boolean isEmailValid(String userEmail) {
        return userEmail.contains("@") && userEmail.length() > 3;
    }

    private boolean isValidUsername(String userName) {
        return userName.length() >= 4 && userName.contains(" ");
    }

    private void setErrorToNull() {
        fullName.setError(null);
        email.setError(null);
        occupation.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        phoneNumber.setError(null);
    }

    private void setUpUpdateProfile(User user) {
        fullName.setText(user.getLastName() + " " + user.getFirstName());
        email.setText(user.getEmailAddress());
        occupation.setText(user.getCraft());
        phoneNumber.setText(user.getPhoneNumber());
    }

    public void switchToLogin(View view) {
        Helper.launchActivity(this, LoginActivity.class);
    }

    @Override
    public void onSuccess() {
        showToast("Registration Successful: You can login to your account now");
        alternateProgressViewSignUpButton();
        Helper.launchActivity(this, LoginActivity.class);
        finish();
    }

    @Override
    public void onUpdateSuccess(){
        Helper.launchActivity(this, ProfileActivity.class);
        finish();
    }

    @Override
    public void onFailure() {
        showToast("Unable to register user at the moment. Please try again");
        alternateProgressViewSignUpButton();
        recreate();
    }

    private void alternateProgressViewSignUpButton() {
        progressView.setVisibility(View.INVISIBLE);
        signUPButton.setVisibility(View.VISIBLE);
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
