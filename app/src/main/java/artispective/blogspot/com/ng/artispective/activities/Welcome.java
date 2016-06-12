package artispective.blogspot.com.ng.artispective.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vstechlab.easyfonts.EasyFonts;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class Welcome extends AppCompatActivity {
    private TextView app_name, welcome, to;
    private Button continue_button;
    private Boolean firstLaunch;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        app_name = (TextView) findViewById(R.id.app_name);
        welcome = (TextView) findViewById(R.id.app_name_welcome);
        to = (TextView) findViewById(R.id.app_name_to);
        welcome.setTypeface(EasyFonts.captureIt(getApplicationContext()));
        to.setTypeface(EasyFonts.captureIt(getApplicationContext()));
        app_name.setTypeface(EasyFonts.tangerineBold(getApplicationContext()));

        continue_button = (Button) findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(Welcome.this, HomeActivity.class);
                Welcome.this.finish();
            }
        });

        if (!isFirstLaunch()){
            Helper.launchActivity(this, HomeActivity.class);
            finish();
        }

    }

    private boolean isFirstLaunch() {
        if (firstLaunch == null) {
            firstLaunch = sharedPreferences.getBoolean("first_time", true);
            if (firstLaunch) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("first_time", false);
                editor.apply();
            }
        }
        return firstLaunch;
    }



}
