package artispective.blogspot.com.ng.artispective.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.ArticleListAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.models.article.ArticleResponse;
import artispective.blogspot.com.ng.artispective.models.article.GetArticles;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener, LogoutAuthentication, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private NavigationView navigationView;
    private FloatingActionButton addArticleButton;
    private ArrayList<Post> posts;
    private ArticleListAdapter adapter;
    private ProgressDialog progressDialog;
    private String userToken, userId;
    private TextView noNetworkTextView;
    private Button refreshButton;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setUpFAB();
        toggleLoginLogout();
        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");
        fetchArticlesAsObservable();
    }

    private void setUpListView() {
        adapter = new ArticleListAdapter(this, posts);
        ListView listView = (ListView) findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDividerHeight(20);
        adapter.notifyDataSetChanged();
    }

    private void fetchArticlesAsObservable() {
        if (!ConnectionChecker.isConnected()) {
            toggleRefreshVisibility();
            return;
        }
        showProgressDialog();
        Observable<GetArticles> observable = Endpoint.RxFactory.getEndpoint().rxGetAllArticles();
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetArticles>() {
                    @Override
                    public void onCompleted() {
                        setUpListView();
                        adapter.notifyDataSetChanged();
                        dismissProgressDialog();
                        toggleRefreshVisibility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        toggleRefreshVisibility();
                        Log.d("semiu", e.getMessage());
                    }

                    @Override
                    public void onNext(GetArticles post) {
                        posts = (ArrayList<Post>) post.getPosts();
                    }
                });

    }

    private void toggleRefreshVisibility() {
        if (posts == null) {
            setRefreshMessageVisibility(View.VISIBLE);
        } else if (posts.size() < 1) {
            setRefreshMessageVisibility(View.VISIBLE);
        } else {
            setRefreshMessageVisibility(View.GONE);
        }
    }

    private void setRefreshMessageVisibility(int visibility) {
        noNetworkTextView.setVisibility(visibility);
        refreshButton.setVisibility(visibility);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleLoginLogout();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Helper.launchHome(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchArticlesAsObservable();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_event:
                Helper.launchActivity(this, EventActivity.class);
                return true;
            case R.id.nav_user:
                Helper.launchActivity(this, ProfileActivity.class);
                return true;
            case R.id.nav_login:
                Helper.launchActivity(this, LoginActivity.class);
                return true;
            case R.id.nav_logout:
                closeDrawer();
                UserAuthentication.logoutUser(this, userId, userToken);
                return true;
            default:
                closeDrawer();
                return false;
        }

    }

    private void closeDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }

    private void setUpFAB() {
        addArticleButton = (FloatingActionButton) findViewById(R.id.create_new_fab);
        addArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(HomeActivity.this, CreateArticleActivity.class);
            }
        });

        noNetworkTextView = (TextView) findViewById(R.id.no_data_note);
        refreshButton = (Button) findViewById(R.id.no_data_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchArticlesAsObservable();
            }
        });
    }

    private void toggleLoginLogout() {
        addArticleButton.setVisibility(View.GONE);
        String id = Helper.getUserData("user_id");
        String token = Helper.getUserData("user_token");

        if (!id.equals("") && !token.equals("")) {
            toggleViewVisibility(true);
        } else {
            toggleViewVisibility(false);
        }

    }

    private void toggleViewVisibility(boolean navVisibility) {
        Menu nav = navigationView.getMenu();
        nav.findItem(R.id.nav_login).setVisible(!navVisibility);
        nav.findItem(R.id.nav_logout).setVisible(navVisibility);
        nav.findItem(R.id.nav_user).setVisible(navVisibility);
        if (Helper.getUserAdminStatus()) {
            addArticleButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess() {
        toggleLoginLogout();
    }

    @Override
    public void onFailure() {
        toggleLoginLogout();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailArticle.class);
        intent.putExtra("position", position);
        intent.putExtra("articles", posts);
        startActivity(intent);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Post post = posts.get(position);
        if (!Helper.getUserAdminStatus()){
            return false;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setMessage("Delete cannot be undo!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteArticle(post);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editArticle(post);
                    }
                });
        builder.show();
        return true;
    }

    private void deleteArticle(final Post post) {
        Observable<ArticleResponse> observable = Endpoint.RxFactory.getEndpoint()
                .rxRemovePost(userToken, userId, post.getId());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleResponse>() {
                    @Override
                    public void onCompleted() {
                        posts.remove(post);
                        adapter.notifyDataSetChanged();
                        Helper.showToast("Post removed successfully");
                        toggleRefreshVisibility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu deletePost", e.getMessage());
                        Helper.showToast("Error deleting post");
                    }

                    @Override
                    public void onNext(ArticleResponse articleResponse) {

                    }
                });
    }

    private void editArticle(Post post) {
        Intent intent = new Intent(this, CreateArticleActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        closeDrawer();
        super.onStop();
    }
}
