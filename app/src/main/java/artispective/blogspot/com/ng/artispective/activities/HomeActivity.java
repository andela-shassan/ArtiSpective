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
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.ArticleListAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.models.article.ArticleResponse;
import artispective.blogspot.com.ng.artispective.models.article.GetArticles;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.utils.ArtiSpectiveEndpoint;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener, LogoutAuthentication, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private NavigationView navigationView;
    private FloatingActionButton addArticleButton;
    private ArrayList<Post> posts;
    private ArticleListAdapter adapter;
    private ProgressDialog progressDialog;
    private String userToken, userId;

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
        fetchArticles();
        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");
    }

    private void setUpListView() {
        adapter = new ArticleListAdapter(this, posts);
        ListView listView = (ListView) findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDividerHeight(20);
    }

    private void fetchArticles() {
        if (ConnectionChecker.isConnected()) {
            showProgressDialog();
            ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.GET_ALL_POST_URL)
                    .getAllArticles().enqueue(new Callback<GetArticles>() {
                @Override
                public void onResponse(Call<GetArticles> call, Response<GetArticles> response) {
                    int code = response.code();
                    if (code == 200) {
                        posts = (ArrayList<Post>) response.body().getPosts();
                        setUpListView();
                    } else {
                        Helper.showToast("Something went wrong. Try again");
                    }
                    dismissProgressDialog();
                }

                @Override
                public void onFailure(Call<GetArticles> call, Throwable t) {
                    dismissProgressDialog();
                }
            });
        } else {
            ConnectionChecker.showNoNetwork();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleLoginLogout();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            fetchArticles();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_event:
                Helper.launchActivity(this, EventActivity.class);
                break;
            case R.id.nav_user:
                Helper.launchActivity(this, ProfileActivity.class);
                break;
            case R.id.nav_login:
                Helper.launchActivity(this, LoginActivity.class);
                break;
            case R.id.nav_logout:
                UserAuthentication.logoutUser(this, userId, userToken);
                break;
            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.refreshDrawableState();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpFAB() {
        addArticleButton = (FloatingActionButton) findViewById(R.id.create_new_fab);
        addArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(HomeActivity.this, CreateArticleActivity.class);
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
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.REMOVE_POST_URL)
                .removePost(userToken, userId, post.getId()).enqueue(
                new Callback<ArticleResponse>() {
                    @Override
                    public void onResponse(Call<ArticleResponse> c, Response<ArticleResponse> r) {
                        int code = r.code();
                        if (code == 200) {
                            posts.remove(post);
                            adapter.notifyDataSetChanged();
                            Helper.showToast("Post removed successfully");
                        } else {
                            String msg = "";
                            try {
                                msg = r.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d("semiu delete art", msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArticleResponse> call, Throwable t) {
                        Helper.showToast("Failed to delete the post");
                    }
                }
        );
    }

    private void editArticle(Post post) {
        Intent intent = new Intent(this, CreateArticleActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

}
