package artispective.blogspot.com.ng.artispective.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.User;
import artispective.blogspot.com.ng.artispective.models.Users;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        assert fab != null;
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

    private void dismissDialog() {
        if (pd != null){
            pd.dismiss();
        }
    }

    private void getUserById(String token, String id) {
        Observable<Users> observab = Endpoint.RxFactory.getEndpoint().rxGetUserProfile(token, id);
        observab.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Users>() {
                    @Override
                    public void onCompleted() {
                        if (user != null)
                            setView(user);
                        dismissDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        user = null;
                        Log.e("semiu", e.getMessage());
                        dismissDialog();
                    }

                    @Override
                    public void onNext(Users users) {
                        user = users.getUser();
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
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Getting Profile Ready!");
        pd.show();
    }

}
