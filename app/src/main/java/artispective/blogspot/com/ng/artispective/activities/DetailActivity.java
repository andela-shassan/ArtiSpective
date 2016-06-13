package artispective.blogspot.com.ng.artispective.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.DetailPagerAdapter;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class DetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ArrayList<Event> events;
    private int currentPosition;
    private ShareDialog shareDialog;
    private Event event;
    private DetailPagerAdapter detailPagerAdapter;
    private ViewPager viewPager;
    private String userId;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                Intent intent = new Intent(DetailActivity.this, CreateEventActivity.class);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share_event) {
            shareEventOnFacebook();
        }
        return super.onOptionsItemSelected(item);
    }

    // The app store link should be set as the contentUrl
    private void shareEventOnFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            event = events.get(viewPager.getCurrentItem());

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://artispective.blogspot.com.ng/"))
                    .setContentTitle(event.getTitle())
                    .setContentDescription(event.getDetails())
                    .setImageUrl(Uri.parse(event.getImages().get(0)))
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
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

    @Override
    protected void onDestroy() {
        viewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }
}
