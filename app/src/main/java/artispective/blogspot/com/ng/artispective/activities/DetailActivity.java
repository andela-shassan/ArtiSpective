package artispective.blogspot.com.ng.artispective.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.DetailPagerAdapter;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.Constants;

public class DetailActivity extends AppCompatActivity {
    private TextView eventName, eventDetails, eventLocation, eventDate;
    private ImageView eventImageView;
    private ScrollView scrollView;
    private ArrayList<Event> events;
    private int currentPosition;
    private GestureDetectorCompat gestureDetector;
    float startPosition = 0;
    private GestureListener gestureListener;
    private Bitmap bitmap1, bitmap2, bitmap3;
    private ShareDialog shareDialog;
    private Event event;
    private DetailPagerAdapter detailPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getViewsReference();
        setUpReadingView();
        //initialize();

        detailPagerAdapter = new DetailPagerAdapter(this, events, currentPosition);
        //detailPagerAdapter.setPrimaryItem(currentPosition);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vPager);
        viewPager.setAdapter(detailPagerAdapter);
        viewPager.setCurrentItem(currentPosition);

    }

    private void initialize() {
        gestureListener = new GestureListener();
        shareDialog = new ShareDialog(this);
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());
    }

    private void setUpReadingView() {
        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            currentPosition = intent.getIntExtra("position", 0);
            events = intent.getParcelableArrayListExtra("exhibitions");
            //setDetailView(currentPosition);
        }
    }

    private void getViewsReference() {
        eventName = (TextView) findViewById(R.id.detail_event_name);
        eventDate = (TextView) findViewById(R.id.detail_event_date);
        eventDetails = (TextView) findViewById(R.id.detail_event_detail);
        eventLocation = (TextView) findViewById(R.id.detail_event_location);
        eventImageView = (ImageView) findViewById(R.id.detail_event_images);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return false;
    }

    private void setDetailView(int position) {
        event = events.get(position);
        if (event == null) return;
        setUpPicassoImageView();

        String[] dateArray = event.getDate().split("-");
        String date = dateArray[2].substring(0, 2) + "/" + dateArray[1] + "/" + dateArray[0];
        eventName.setText(event.getTitle());
        eventLocation.setText(event.getAddress());
        eventDate.setText(date);
        eventDetails.setText(event.getDetails());
    }

    private void setUpPicassoImageView() {
        String url;
        url = (event.getImages().size() > 0) ? event.getImages().get(0) : Constants.DEFAULT_IMAGE;

        Picasso.with(this).load(url).placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image).into(eventImageView);
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
            //shareEventOnFacebook();
        }
        return super.onOptionsItemSelected(item);
    }

    // The app store link should be set as the contentUrl
    private void shareEventOnFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://artispective.blogspot.com.ng/"))
                    .setContentTitle(event.getTitle())
                    .setContentDescription(event.getDetails())
                    .setImageUrl(Uri.parse(event.getImages().get(0)))
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private float minFling = 100;
        private float minVelocity = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            startPosition = e.getXPrecision() * e.getX();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean forward = false;
            boolean backward = false;

            float horizontalDiff = e2.getX() - e1.getX();
            float absHorizontalDiff = Math.abs(horizontalDiff);
            float absVelocityX = Math.abs(velocityX);

            if (absHorizontalDiff > minFling && absVelocityX > minVelocity) {
                backward = horizontalDiff > 0;
                forward = !backward;
            }
            if (forward) {
                if (currentPosition < events.size() - 1) {
                    setDetailView(++currentPosition);
                }
            } else if (backward) {
                if (currentPosition > 0) {
                    setDetailView(--currentPosition);
                }
            }
            return true;
        }

    }

}
