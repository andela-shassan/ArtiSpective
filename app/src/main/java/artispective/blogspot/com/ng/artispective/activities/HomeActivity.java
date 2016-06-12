package artispective.blogspot.com.ng.artispective.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.EventListAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.models.events.AllEvents;
import artispective.blogspot.com.ng.artispective.models.model.DeleteEvent;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.ArtiSpectiveEndpoint;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, LogoutAuthentication,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<Event> events;
    boolean hasTriedFetch = false;
    private NavigationView navigationView;
    private FloatingActionButton addEventButton;
    private EventListAdapter eventListAdapter;

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
        fetchExhibition();

    }

    private void setUpFAB() {
        addEventButton = (FloatingActionButton) findViewById(R.id.create_new_fab);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(HomeActivity.this, CreateEventActivity.class);
            }
        });
    }

    private void setUpListView(ArrayList<Event> events) {
        eventListAdapter = new EventListAdapter(this, events);
        ListView listView = (ListView) findViewById(R.id.event_list_view);
        assert listView != null;
        listView.setAdapter(eventListAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDividerHeight(10);
    }

    private void fetchExhibition() {
        if (ConnectionChecker.isConnected()) {
            final ProgressDialog progressDialog = showProgressDialog();
            ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.GET_ALL_EVENTS)
                    .getAllEvents().enqueue(new Callback<AllEvents>() {

                @Override
                public void onResponse(Call<AllEvents> call, Response<AllEvents> response) {
                    Log.v("semiu getAllEvent code", response.code() + "");
                    if (response.body() != null && response.code() == 200) {
                        AllEvents bigEvent = response.body();
                        events = (ArrayList<Event>) bigEvent.getEvents();
                        Log.e("semiu events size", events.size() + " ");
                        setUpListView(events);

                    } else {
                        showToast("Something went wrong. Please try again");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<AllEvents> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Failed to load the events. Please try again");
                }
            });
        } else {
            ConnectionChecker.showNoNetwork();
            if (!hasTriedFetch) {
                hasTriedFetch = true;
                fetchExhibition();
            }
        }
    }

    @NonNull
    private ProgressDialog showProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Events");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        return progressDialog;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            // reload the events from network
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
            case R.id.nav_user:
                Helper.launchActivity(this, ProfileActivity.class);
                break;
            case R.id.nav_login:
                Helper.launchActivity(this, LoginActivity.class);
                break;
            case R.id.nav_logout:
                logUserOut();
                break;
            default:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.refreshDrawableState();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logUserOut() {
        UserAuthentication.logoutUser(this, Helper.getUserData("user_id"),
                Helper.getUserData("user_token"));
    }

    private void toggleLoginLogout() {
        addEventButton.setVisibility(View.GONE);
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
            addEventButton.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("exhibitions", events);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (!Helper.getUserAdminStatus()){
            return false;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this event? This cannot be undo!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
        return true;
    }

    private void deleteEvent(int position) {
        final Event event = events.get(position);
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.REMOVE_EVENT)
                .deleteEvent(Helper.getUserData("user_token"), Helper.getUserData("user_id"),
                        event.getId()).enqueue(new Callback<DeleteEvent>() {
                    @Override
                    public void onResponse(Call<DeleteEvent> call, Response<DeleteEvent> response) {
                        int code = response.code();
                        if (code == 200) {
                            events.remove(event);
                            eventListAdapter.notifyDataSetChanged();
                            showToast("Event removed successfully");
                        } else {
                            showToast("Something went wrong. Please try again");
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteEvent> call, Throwable t) {
                        showToast("Failed to delete event. Try again");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
