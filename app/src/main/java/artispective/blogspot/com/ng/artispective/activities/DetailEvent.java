package artispective.blogspot.com.ng.artispective.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.DetailPagerAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.ShareItemClickListener;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.ShareSheet;

public class DetailEvent extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        ShareItemClickListener {
    private ArrayList<Event> events;
    private int currentPosition;
    private ShareDialog shareDialog;
    private Event event;
    private DetailPagerAdapter detailPagerAdapter;
    private ViewPager viewPager;
    private String userId;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private ShareSheet shareSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getEventsAndPosition();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_edit_event);
        userId = Helper.getUserData("user_id");
        shareDialog = new ShareDialog(this);

        detailPagerAdapter = new DetailPagerAdapter(this, events);
        viewPager = (ViewPager) findViewById(R.id.vPager);
        viewPager.setAdapter(detailPagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        setUpFAB();

    }

    private void setUpFAB() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = events.get(viewPager.getCurrentItem());
                Intent intent = new Intent(DetailEvent.this, CreateEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewPager.addOnPageChangeListener(this);
    }

    private void getEventsAndPosition() {
        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            currentPosition = intent.getIntExtra("position", 0);
            events = intent.getParcelableArrayListExtra("exhibitions");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        event = events.get(viewPager.getCurrentItem());
        if (id == R.id.add_to_calendar) {
            Helper.addToCalendar(this, event);
        } else if (id == R.id.share_event) {
            shareSheet = new ShareSheet(this, this);
            shareSheet.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        event = events.get(position);
        if (!userId.equals(event.getAddedBy().getId())) {
            floatingActionButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void shareEventOnFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            event = events.get(viewPager.getCurrentItem());
            String u;
            u = (event.getImages().size() > 0)? event.getImages().get(0): Constants.DEFAULT_IMAGE;

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://artispective.blogspot.com.ng/"))
                    .setContentTitle(event.getTitle())
                    .setContentDescription(event.getDetails())
                    .setImageUrl(Uri.parse(u))
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

    private void shareOnTwitter() {
        event = events.get(viewPager.getCurrentItem());
        Intent shareIntent = findTwitterClient();
        if (shareIntent == null) {
            Helper.showToast("Twitter application not installed");
            return;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, event.getTitle()+"\n"
                +event.getDetails() +"\n" + "http://artispective.blogspot.com.ng/");
        startActivity(Intent.createChooser(shareIntent, "Share"));

    }

    private void shareOnGooglePlus() {
        event = events.get(viewPager.getCurrentItem());
        Intent googleShare = findGooglePlusClient();
        if (googleShare == null) {
            Helper.showToast("Google+ application not installed");
            return;
        }
        googleShare.putExtra(Intent.EXTRA_TEXT, event.getDetails());
        googleShare.putExtra(Intent.EXTRA_TITLE, event.getTitle());
        startActivity(Intent.createChooser(googleShare, "Share"));
    }


    public Intent findTwitterClient() {
        final String[] twitterApps = {"com.twitter.android", "com.twidroid",
                "com.handmark.tweetcaster", "com.thedeck.android" };

        Intent tweetIntent = new Intent();
        tweetIntent.setType("text/plain");
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (int i = 0; i < twitterApps.length; i++) {
            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                Log.e("semiu package", p);
                if (p != null && p.startsWith(twitterApps[i])) {
                    tweetIntent.setPackage(p);
                    return tweetIntent;
                }
            }
        }

        return null;
    }

    public Intent findGooglePlusClient() {
        final String googlePlus = "com.google.android.apps.plus";

        Intent googlePIntent = new Intent();
        googlePIntent.setType("text/plain");
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                googlePIntent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : list) {
                String p = resolveInfo.activityInfo.packageName;
                Log.e("semiu package G+ ", p);
                if (p.startsWith(googlePlus)) {
                    googlePIntent.setPackage(p);
                    return googlePIntent;
                }
            }

        return null;
    }

    @Override
    public void onFacebookClicked() {
        shareEventOnFacebook();
    }


    @Override
    public void onTwitterClicked() {
        shareOnTwitter();
    }

    @Override
    public void onGooglePlusClicked() {
        shareOnGooglePlus();
    }

    @Override
    protected void onDestroy() {
        viewPager.removeOnPageChangeListener(this);
        detailPagerAdapter.finishUpdate(null);
        detailPagerAdapter = null;
        super.onDestroy();
    }
}
