package artispective.blogspot.com.ng.artispective.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.ArticleDetailPagerAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.CommentClickListener;
import artispective.blogspot.com.ng.artispective.models.article.ArticleResponse;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailArticle extends AppCompatActivity implements CommentClickListener {

    private ArticleDetailPagerAdapter pagerAdapter;
    private ArrayList<Post> posts;
    private int currentPosition;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private View view;
    private EditText editComment;
    private String comment;
    private Button postComment;
    private String userId, userToken;
    private AlertDialog dialog;
    private Post post;
    private MenuItem item;
    private int commentPodition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getEventsAndPosition();

        pagerAdapter = new ArticleDetailPagerAdapter(this, this, posts);

        setUpViewPager();

        inflater = getLayoutInflater();

        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");
    }

    private void setUpViewPager() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_article, menu);
        item = menu.findItem(R.id.edit_article);
        if (!Helper.getUserAdminStatus()) {
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_article) {
            post = posts.get(viewPager.getCurrentItem());
            Intent intent = new Intent(this, CreateArticleActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCommentClick(int position) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userToken)) {
            Helper.showToast("You must be login to add comment");
            return;
        }

        currentPosition = position;
        final Post post = posts.get(position);
        view = inflater.inflate(R.layout.add_comment_layout, null);
        editComment = (EditText) view.findViewById(R.id.comment);
        editComment.requestFocus();
        postComment = (Button) view.findViewById(R.id.post_comment_button);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPostComment(post);
            }
        });
        buildDialog();

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    private void attemptPostComment(Post post) {
        comment = editComment.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            Helper.showToast("You cannot post an empty comment");
            return;
        }

        if (ConnectionChecker.isConnected()) {
            uploadComment(post);
        } else {
            ConnectionChecker.showNoNetwork();
        }

    }

    private void uploadComment(Post post) {
        Observable<ArticleResponse> observable = Endpoint.RxFactory.getEndpoint()
                .rxAddComment(userToken, userId, post.getId(), comment);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleResponse>() {
                    @Override
                    public void onCompleted() {
                        Helper.showToast("comment added successfully");
                        setUpViewPager();
                        dismissDialog();
                        recreate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu uploadComment", e.getMessage());
                        dismissDialog();
                        Helper.showToast("Something went wrong. Please try again");
                        recreate();
                    }

                    @Override
                    public void onNext(ArticleResponse articleResponse) {

                    }
                });
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        Helper.launchActivity(this, HomeActivity.class);
        finish();
    }

}
