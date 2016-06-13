package artispective.blogspot.com.ng.artispective.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.User;
import artispective.blogspot.com.ng.artispective.models.Users;
import artispective.blogspot.com.ng.artispective.utils.ArtiSpectiveEndpoint;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView app_name, userName, userEmail, userPhone, userOccupation;
    private User user;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app_name = (TextView) findViewById(R.id.profile_app_name);
        userEmail = (TextView) findViewById(R.id.email_text);
        userName = (TextView) findViewById(R.id.name_text);
        userPhone = (TextView) findViewById(R.id.phone_text);
        userOccupation = (TextView) findViewById(R.id.occupation_text);

        app_name.setTypeface(EasyFonts.tangerineBold(this));

        String token = Helper.getUserData("user_token");
        showProgress();
        getUserById(token, Helper.getUserData("user_id"));
        setUpFAB();
        if (user != null) {
            setView(user);
        }

    }

    private void setUpFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserById(String token, String id) {
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.USER_PROFILE)
                .getUserProfile(token, id).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.code() == 200) {
                    user = response.body().getUser();
                    if (user != null)
                        setView(user);
                }
                if (pd != null){
                    pd.dismiss();
                    return;
                }
                showMessage("User Not found. Please try again");
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                user = null;
                if (pd != null){
                    pd.dismiss();
                }
                showMessage("Failed to get user. Please try again");
            }

        });
    }

    private void setView(User user) {
        userName.setText(user.getFirstName() + " "+ user.getLastName());
        userEmail.setText(user.getEmailAddress());
        userPhone.setText(user.getPhoneNumber());
        userOccupation.setText(user.getCraft());
    }

    private void showProgress(){
        pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Getting Profile Ready!");
        pd.setCancelable(true);
        pd.show();
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
