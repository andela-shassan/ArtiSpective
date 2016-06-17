package artispective.blogspot.com.ng.artispective.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.model.BigEvent;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.ArtiSpectiveEndpoint;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Constants;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2324;
    private EditText editName, editDate, editLocation, editDetail;
    private String eventName, eventDate, eventLocation, eventDetail;
    private ImageView eventImage, eventImage2, eventImage3;
    private Button saveButton;
    private static final int SELECT_IMAGE_CODE = 1960;
    private static final int SELECT_IMAGE_CODE2 = 1961;
    private static final int SELECT_IMAGE_CODE3 = 1962;
    public Bitmap bitmap, bitmap2, bitmap3;
    private ProgressDialog progressDialog;
    private Bitmap placeHolderBitmap;
    private File file, file2, file3;
    private String filePath, filePath2, filePath3;
    private String placeholderPath;
    private Event event;
    private String userToken;
    private String userId;
    private boolean updateEvent;
    private RequestBody usersId, usersToken, eventsName, eventsDetail, eLocat, eventDated;
    private RequestBody eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        findViews();
        permissionChecker();

        updateEvent = false;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("event")) {
            event = intent.getParcelableExtra("event");
            setTitle("Update Event");
            setUpEditView(event);
            updateEvent = true;
        }
        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");

    }

    private void setUpEditView(Event event) {
        String[] dates = event.getDate().split("-");
        String date = dates[2].substring(0,2) + "/" + dates[1] + "/" + dates[0];
        editName.setText(event.getTitle());
        editDate.setText(date);
        editLocation.setText(event.getAddress());
        editDetail.setText(event.getDetails());
        saveButton.setText(R.string.update_event_button);
    }

    private void permissionChecker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(editName, R.string.read_external_storage, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull
    final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(editName, "Permission Granted", Snackbar.LENGTH_LONG);
            } else {
                Snackbar.make(editName, "Permission Require to continue", Snackbar.LENGTH_LONG);
            }
        }
    }

    private void findViews() {
        editName = (EditText) findViewById(R.id.event_name);
        editDate = (EditText) findViewById(R.id.event_date);
        editLocation = (EditText) findViewById(R.id.event_location);
        editDetail = (EditText) findViewById(R.id.event_additional_details);
        saveButton = (Button) findViewById(R.id.save_button);
        assert saveButton != null;
        saveButton.setOnClickListener(this);
        eventImage = (ImageView) findViewById(R.id.event_banner);
        assert eventImage != null;
        eventImage.setOnClickListener(this);
        eventImage2 = (ImageView) findViewById(R.id.event_banner2);
        assert eventImage2 != null;
        eventImage2.setOnClickListener(this);
        eventImage3 = (ImageView) findViewById(R.id.event_banner3);
        assert eventImage3 != null;
        eventImage3.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void attemptSaveEvent() {
        Helper.hideSoftKeyboard(this, saveButton);
        if (file == null && !updateEvent) {
            Helper.showToast("At least main image is required to post an event!");
            return;
        }
        this.eventName = editName.getText().toString().trim();
        this.eventDate = editDate.getText().toString().trim();
        this.eventLocation = editLocation.getText().toString().trim();
        this.eventDetail = editDetail.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(eventName)) {
            this.editName.setError(getString(R.string.error_empty_event_name));
            focusView = this.editName;
            cancel = true;
        } else if (!isValidEventName(eventName)) {
            this.editName.setError(getString(R.string.error_full_name_short));
            focusView = this.editName;
            cancel = true;
        }

        if (TextUtils.isEmpty(eventDate)) {
            this.editDate.setError(getString(R.string.error_empty_event_date));
            focusView = this.editDate;
            cancel = true;
        } else if (!isValidEventDate(eventDate)) {
            this.editDate.setError(getString(R.string.error_full_invalid_date));
            focusView = this.editDate;
            cancel = true;
        }

        if (TextUtils.isEmpty(eventLocation)) {
            this.editLocation.setError(getString(R.string.error_empty_event_location));
            focusView = this.editLocation;
            cancel = true;
        }

        if (TextUtils.isEmpty(eventDetail)) {
            this.editDetail.setError(getString(R.string.error_field_required));
            focusView = this.editDetail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (ConnectionChecker.isConnected()) {
                setProgressDialog();

                String[] dateArray = eventDate.split("/");
                String date = dateArray[1] + "/" + dateArray[0] + "/" + dateArray[2];

                if (event != null) {
                    eventId = RequestBody.create(MediaType.parse("text/plain"), event.getId());

                }
                usersId = RequestBody.create(MediaType.parse("text/plain"), this.userId);
                usersToken = RequestBody.create(MediaType.parse("text/plain"), this.userToken);
                eventsName = RequestBody.create(MediaType.parse("text/plain"), this.eventName);
                eventsDetail = RequestBody.create(MediaType.parse("text/plain"), this.eventDetail);
                eLocat = RequestBody.create(MediaType.parse("text/plain"), this.eventLocation);
                eventDated  = RequestBody.create(MediaType.parse("text/plain"), date);

                if (event == null) {
                    uploadEvent();
                } else if (file != null) {
                    updateEvent(file);
                } else if (file == null) {
                    updateEvent();
                }


            } else {
                ConnectionChecker.showNoNetwork();
            }
        }

    }

    private void uploadEvent() {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody eventImage = RequestBody.create(MEDIA_TYPE_PNG, file);

        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.ADD_EVENT_URL).addEvent(
                usersId, usersToken, eventsName, eventsDetail, eLocat, eventDated,
                eventImage).enqueue(

                new Callback<BigEvent>() {

                    @Override
                    public void onResponse(Call<BigEvent> call, Response<BigEvent> response) {
                        int code = response.code();
                        Log.v("semiu create code", " "+ code );
                        if (code == 200) {
                            Event e = response.body().getEvent();
                            Helper.showToast(e.getTitle() + " Created successfully");
                            Helper.launchActivity(CreateEventActivity.this, EventActivity.class);
                            CreateEventActivity.this.finish();
                        } else {
                            Helper.showToast("Something went wrong");
                        }
                        dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<BigEvent> call, Throwable t) {
                        Helper.showToast("Failed to add the event");
                        dismissProgressDialog();
                    }
                }
        );
    }

    private void updateEvent(File file) {

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody eventImage = RequestBody.create(MEDIA_TYPE_PNG, file);

        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.ADD_EVENT_URL).updateEvent(
                eventId, usersId, usersToken, eventsName, eventsDetail, eLocat,
                eventDated, eventImage).enqueue(

                new Callback<BigEvent>() {

                    @Override
                    public void onResponse(Call<BigEvent> call, Response<BigEvent> response) {
                        int code = response.code();
                        Log.v("semiu update code", " "+ code );
                        if (code == 200) {
                            Event e = response.body().getEvent();
                            Helper.showToast(e.getTitle() + " Updated successfully");
                            Helper.launchActivity(CreateEventActivity.this, EventActivity.class);
                            CreateEventActivity.this.finish();
                        } else {
                            Helper.showToast("Something went wrong update with file");
                        }
                        dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<BigEvent> call, Throwable t) {
                        Helper.showToast("Failed to update the event with file");
                        dismissProgressDialog();
                    }
                }
        );
    }

    private void updateEvent() {

        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.ADD_EVENT_URL).updateEvent(
                eventId, usersId, usersToken, eventsName, eventsDetail, eLocat, eventDated)
                .enqueue(new Callback<BigEvent>() {

                    @Override
                    public void onResponse(Call<BigEvent> call, Response<BigEvent> response) {
                        int code = response.code();
                        Log.v("semiu update code", " "+ code );
                        if (code == 200) {
                            Event e = response.body().getEvent();
                            Helper.showToast(e.getTitle() + " Updated successfully");
                            Helper.launchActivity(CreateEventActivity.this, EventActivity.class);
                            CreateEventActivity.this.finish();
                        } else {
                            Helper.showToast("Something went wrong update without file");
                        }
                        dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<BigEvent> call, Throwable t) {
                        Helper.showToast("Failed to update the event without file");
                        dismissProgressDialog();
                    }
                }
        );
    }

    private boolean isValidEventDate(String eventDate) {
        return eventDate.length() == 10 && eventDate.contains("/");
    }

    private boolean isValidEventName(String eventName) {
        return eventName.length() > 2;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_button:
                attemptSaveEvent();
                break;
            case R.id.event_banner:
                openGallery(SELECT_IMAGE_CODE);
                break;
            case R.id.event_banner2:
                openGallery(SELECT_IMAGE_CODE2);
                break;
            case R.id.event_banner3:
                openGallery(SELECT_IMAGE_CODE3);
                break;
            default:
                break;
        }

    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Event " + eventName + " is being created");
        progressDialog.setTitle("Uploading Data");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void openGallery(int imageRequestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Choose Image"), imageRequestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case SELECT_IMAGE_CODE:
                setBitmapToImageView(resultCode, data, eventImage);
                bitmap = placeHolderBitmap;
                filePath = placeholderPath;
                file = new File(filePath);
                break;
            case SELECT_IMAGE_CODE2:
                setBitmapToImageView(resultCode, data, eventImage2);
                bitmap2 = placeHolderBitmap;
                filePath2 = placeholderPath;
                file2 = new File(filePath2);
                break;
            case SELECT_IMAGE_CODE3:
                setBitmapToImageView(resultCode, data, eventImage3);
                bitmap3 = placeHolderBitmap;
                filePath3 = placeholderPath;
                file3 = new File(filePath3);
                break;
            default:
                break;
        }

    }

    private void setBitmapToImageView(int resultCode, Intent data, ImageView imageView) {
        if (resultCode == RESULT_OK && data != null) {
            assert data.getData() != null;
            try {

                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView.setImageBitmap(b);
                placeHolderBitmap = b;
                placeholderPath = Helper.getRealPathFromURI_API19(this, data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
