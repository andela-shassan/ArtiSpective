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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.adapters.EventListAdapter;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.models.events.Events;
import artispective.blogspot.com.ng.artispective.models.model.DeleteEvent;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import artispective.blogspot.com.ng.artispective.utils.UserAuthentication;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;


public class EventActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LogoutAuthentication, OnItemClickListener, OnItemLongClickListener {

    private ArrayList<Event> events;
    private NavigationView navigationView;
    private FloatingActionButton addEventButton;
    private EventListAdapter eventListAdapter;
    private TextView noNetworkTextView;
    private Button refreshButton;
    private DrawerLayout drawer;

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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setUpFAB();
        toggleLoginLogout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchObservableEvents();
    }

    private void setUpFAB() {
        addEventButton = (FloatingActionButton) findViewById(R.id.create_new_fab);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchActivity(EventActivity.this, CreateEventActivity.class);
            }
        });

        noNetworkTextView = (TextView) findViewById(R.id.no_data_note);
        refreshButton = (Button) findViewById(R.id.no_data_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchObservableEvents();

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

    private void fetchObservableEvents() {
        if (!ConnectionChecker.isConnected()) {
            toggleRefreshVisibility();
            return;
        }
        final ProgressDialog progressDialog = showProgressDialog();
        Observable<Events> observable = Endpoint.RxFactory.getEndpoint().rxGetAllEvents();
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Events>() {

                    @Override
                    public void onCompleted() {
                        progressDialog.dismiss();
                        setUpListView(events);
                        toggleRefreshVisibility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("semiu", e.getMessage());
                        toggleRefreshVisibility();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onNext(Events event) {
                        events = (ArrayList<Event>) event.getEvents();
                    }
                });
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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            fetchObservableEvents();
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
                return true;
            case R.id.nav_user:
                Helper.launchActivity(this, ProfileActivity.class);
                return true;
            case R.id.nav_login:
                Helper.launchActivity(this, LoginActivity.class);
                return true;
            case R.id.nav_logout:
                closeDrawer();
                logUserOut();
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
        builder.setTitle("Choose an action").setMessage("Delete cannot be undo!")
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
        Observable<DeleteEvent> observable = Endpoint.RxFactory.getEndpoint().rxDeleteEvent(
                Helper.getUserData("user_token"), Helper.getUserData("user_id"), event.getId());
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeleteEvent>() {
                    @Override
                    public void onCompleted() {
                        events.remove(event);
                        eventListAdapter.notifyDataSetChanged();
                        Helper.showToast("Event removed successfully");
                        toggleRefreshVisibility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Helper.showToast("Something went wrong. Please try again");
                    }

                    @Override
                    public void onNext(DeleteEvent deleteEvent) {

                    }
                });
    }

    private void toggleRefreshVisibility() {
        if (events == null) {
            setRefreshMessageVisibility(View.VISIBLE);
        } else if (events.size() < 1) {
            setRefreshMessageVisibility(View.VISIBLE);
        } else {
            setRefreshMessageVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        closeDrawer();
        super.onStop();
    }

    private void setRefreshMessageVisibility(int visibility) {
        noNetworkTextView.setVisibility(visibility);
        refreshButton.setVisibility(visibility);
    }

}
