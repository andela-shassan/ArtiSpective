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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.EventListAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.models.events.Events;
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

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;


public class EventActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LogoutAuthentication, OnItemClickListener, OnItemLongClickListener {

    private ArrayList<Event> events;
    boolean hasTriedFetch = false;
    private NavigationView navigationView;
    private FloatingActionButton addEventButton;
    private EventListAdapter eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
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
        fetchEvents();

    }

    private void setUpFAB() {
        addEventButton = (FloatingActionButton) findViewById(R.id.create_new_fab);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(EventActivity.this, CreateEventActivity.class);
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
        listView.setDividerHeight(20);
    }

    private void fetchEvents() {
        if (ConnectionChecker.isConnected()) {
            final ProgressDialog progressDialog = showProgressDialog();
            ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.GET_ALL_EVENTS)
                    .getAllEvents().enqueue(new Callback<Events>() {

                @Override
                public void onResponse(Call<Events> call, Response<Events> response) {
                    if (response.body() != null && response.code() == 200) {
                        Events bigEvent = response.body();
                        events = (ArrayList<Event>) bigEvent.getEvents();
                        setUpListView(events);

                    } else {
                        Helper.showToast("Something went wrong. Please try again");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<Events> call, Throwable t) {
                    progressDialog.dismiss();
                    if (!hasTriedFetch) {
                        hasTriedFetch = true;
                        //fetchEvents();
                    }
                }
            });
        } else {
            ConnectionChecker.showNoNetwork();
            if (!hasTriedFetch) {
                hasTriedFetch = true;
               // fetchEvents();
            }
        }
    }

    @NonNull
    private ProgressDialog showProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Events Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        return progressDialog;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            fetchEvents();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                Helper.launchActivity(this, HomeActivity.class);
                finish();
                break;
            case R.id.nav_event:
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
        Intent intent = new Intent(this, DetailEvent.class);
        intent.putExtra("position", position);
        intent.putExtra("exhibitions", events);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Event event = events.get(position);
        if (!Helper.getUserAdminStatus() || !Helper.getUserData("user_id").matches(event
                .getAddedBy().getId())){
            return false;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setMessage("Delete cannot be undo!")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(event);
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
                        editEvent(event);
                    }
                });
        builder.show();
        return true;
    }

    private void editEvent(Event event) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void deleteEvent(final Event event) {
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.REMOVE_EVENT)
                .deleteEvent(Helper.getUserData("user_token"), Helper.getUserData("user_id"),
                        event.getId()).enqueue(new Callback<DeleteEvent>() {
                    @Override
                    public void onResponse(Call<DeleteEvent> call, Response<DeleteEvent> res) {
                        int code = res.code();
                        if (code == 200) {
                            events.remove(event);
                            eventListAdapter.notifyDataSetChanged();
                            Helper.showToast("Event removed successfully");
                        } else {
                            Helper.showToast("Something went wrong. Please try again");
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteEvent> call, Throwable t) {
                        Helper.showToast("Failed to delete event. Try again");
                    }
                });
    }

}
