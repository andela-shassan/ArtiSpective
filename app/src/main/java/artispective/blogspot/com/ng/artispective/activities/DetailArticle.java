package artispective.blogspot.com.ng.artispective.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.ArticleDetailPagerAdapter;
import artispective.blogspot.com.ng.artispective.adapters.CommentAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.CommentClickListener;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.utils.Helper;

public class DetailArticle extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        CommentClickListener {

    private ArticleDetailPagerAdapter pagerAdapter;
    private ArrayList<Post> posts;
    private int currentPosition;
    private ViewPager viewPager;
    private ListView listView;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getEventsAndPosition();

        pagerAdapter = new ArticleDetailPagerAdapter(this, this, posts);

        viewPager = (ViewPager) findViewById(R.id.articlePager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPosition);

    }

    private void getEventsAndPosition() {
        Intent intent = getIntent();
        if (intent.hasExtra("position")) {
            currentPosition = intent.getIntExtra("position", 0);
            posts = intent.getParcelableArrayListExtra("articles");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Post post = posts.get(position);
        Log.d("semiu post scroll", post.getHeading() +" "+post.getId());
//        listView = (ListView) findViewById(R.id.comment_list_view_home);
//        commentAdapter = new CommentAdapter(this, (ArrayList<PostComment>) post.getComments());
//        listView.setAdapter(commentAdapter);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCommentClick(int position) {
        Post post = posts.get(position);
        Log.d("semiu post clicked", post.getHeading() +" "+post.getId());
        Helper.showToast(post.getHeading() + " clicked");

    }
}
