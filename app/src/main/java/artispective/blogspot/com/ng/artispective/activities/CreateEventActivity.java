package artispective.blogspot.com.ng.artispective.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.model.BigEvent;
import artispective.blogspot.com.ng.artispective.models.model.Event;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.CustomDatePicker;
import artispective.blogspot.com.ng.artispective.utils.CustomTimePicker;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2324;
    private EditText editName, editDate, editTime, editLocation, editDetail;
    private String eventName, eventDate, eventTime, eventLocation, eventDetail;
    private ImageView eventImage, eventImage2, eventImage3, dateButton, timeButtom;
    private Button saveButton;
    private static final int SELECT_IMAGE_CODE = 1960;
    private static final int SELECT_IMAGE_CODE2 = 1961;
    private static final int SELECT_IMAGE_CODE3 = 1962;
    private ProgressDialog progressDialog;
    private File file, file2, file3;
    private String filePath, filePath2, filePath3;
    private String placeholderPath;
    private Event event;
    private String userToken;
    private String userId;
    private boolean updateEvent;
    private RequestBody usersId, usersToken, eventsName, eventsDetail, eLocat, eventDated;
    private RequestBody eventId;
    private String dateTime;
    private MediaType MEDIA_TYPE_PNG;



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
        MEDIA_TYPE_PNG = MediaType.parse("image/png");
        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");

    }

    private void setUpEditView(Event event) {
        String[] dates = event.getDate().split("-");
        String date = dates[2].substring(0,2) + "/" + dates[1] + "/" + dates[0];
        String time = dates[2].substring(3,8);
        editName.setText(event.getTitle());
        editName.requestFocus();
        editDate.setText(date);
        editTime.setText(time);
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
        editTime = (EditText) findViewById(R.id.event_time);
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
        dateButton = (ImageView) findViewById(R.id.date_button);
        assert dateButton != null;
        dateButton.setOnClickListener(this);
        timeButtom = (ImageView) findViewById(R.id.time_button);
        assert timeButtom != null;
        timeButtom.setOnClickListener(this);
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
        this.eventTime = editTime.getText().toString().trim();
        this.eventLocation = editLocation.getText().toString().trim();
        this.eventDetail = editDetail.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(eventTime)) {
            this.eventTime = "07:00";
        }

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

                String[] dates = eventDate.split("/");
                dateTime = String.format("%s-%s-%sT%s", dates[2], dates[1], dates[0], eventTime);
                Log.d("semiu date", dateTime);

                if (event != null) {
                    eventId = RequestBody.create(MediaType.parse("text/plain"), event.getId());

                }
                usersId = RequestBody.create(MediaType.parse("text/plain"), this.userId);
                usersToken = RequestBody.create(MediaType.parse("text/plain"), this.userToken);
                eventsName = RequestBody.create(MediaType.parse("text/plain"), this.eventName);
                eventsDetail = RequestBody.create(MediaType.parse("text/plain"), this.eventDetail);
                eLocat = RequestBody.create(MediaType.parse("text/plain"), this.eventLocation);
                eventDated  = RequestBody.create(MediaType.parse("text/plain"), dateTime);

                if (event == null) {
                    if (file3 != null && file2 != null && file != null) {
                        uploadEventFile3();
                    } else if (file2 != null && file != null) {
                        uploadEventFile2();
                    } else {
                        uploadEventFile1();
                    }
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

    private void uploadEventFile1() {
        RequestBody eventImage = RequestBody.create(MEDIA_TYPE_PNG, file);
        Observable<BigEvent> observable = Endpoint.RxFactory.getEndpoint().rxAddEvent(userToken,
                usersId, eventsName, eventsDetail, eLocat, eventDated, eventImage);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigEvent>() {
                    @Override
                    public void onCompleted() {
                        addEventSuccessResponse("Event created successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu", e.getMessage());
                        dismissProgressDialog();
                    }

                    @Override
                    public void onNext(BigEvent bigEvent) {

                    }
                });
    }

    private void uploadEventFile2() {
        RequestBody eventImage = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody eventImage2 = RequestBody.create(MEDIA_TYPE_PNG, file2);
        Observable<BigEvent> observable = Endpoint.RxFactory.getEndpoint().rxAddEvent2(userToken,
                usersId, eventsName, eventsDetail, eLocat, eventDated, eventImage, eventImage2);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigEvent>() {
                    @Override
                    public void onCompleted() {
                        addEventSuccessResponse("Event created successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu", e.getMessage());
                        dismissProgressDialog();
                    }

                    @Override
                    public void onNext(BigEvent bigEvent) {

                    }
                });
    }

    private void uploadEventFile3() {
        RequestBody eventImage = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody eventImage2 = RequestBody.create(MEDIA_TYPE_PNG, file2);
        RequestBody eventImage3 = RequestBody.create(MEDIA_TYPE_PNG, file3);

        Observable<BigEvent> observable = Endpoint.RxFactory.getEndpoint().rxAddEvent3(userToken,
        usersId, eventsName, eventsDetail, eLocat, eventDated, eventImage,eventImage2,eventImage3);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigEvent>() {
                    @Override
                    public void onCompleted() {
                        addEventSuccessResponse("Event created successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu", e.getMessage());
                        dismissProgressDialog();
                    }

                    @Override
                    public void onNext(BigEvent bigEvent) {

                    }
                });
    }


    private void addEventSuccessResponse(String message) {
        Helper.launchActivity(this, EventActivity.class);
        dismissProgressDialog();
        Helper.showToast(message);
        finish();
    }

    private void updateEvent(File file) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody image = RequestBody.create(MEDIA_TYPE_PNG, file);

        Observable<BigEvent> observable = Endpoint.RxFactory.getEndpoint().rxUpdateEvent(
                eventId, usersId, usersToken, eventsName, eventsDetail, eLocat, eventDated, image);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigEvent>() {
                    @Override
                    public void onCompleted() {
                        addEventSuccessResponse("Event Updated successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu", e.getMessage());
                        dismissProgressDialog();
                        Helper.showToast("Something went wrong update with file");
                    }

                    @Override
                    public void onNext(BigEvent bigEvent) {

                    }
                });
    }

    private void updateEvent() {
        Observable<BigEvent> observable = Endpoint.RxFactory.getEndpoint().rxUpdateEvent(
                userToken, event.getId(), userId, eventName, eventDetail, eventLocation, dateTime);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BigEvent>() {
                    @Override
                    public void onCompleted() {
                        addEventSuccessResponse("Event Updated successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("semiu", e.getMessage());
                        dismissProgressDialog();
                        Helper.showToast("Something went wrong update with file");
                    }

                    @Override
                    public void onNext(BigEvent bigEvent) {

                    }
                });
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
            case R.id.date_button:
                DialogFragment dialog = new CustomDatePicker();
                dialog.show(getSupportFragmentManager(), "date_picker");
                break;
            case R.id.time_button:
                DialogFragment fragment = new CustomTimePicker();
                fragment.show(getSupportFragmentManager(), "time_picker");
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
                filePath = placeholderPath;
                file = new File(filePath);
                break;
            case SELECT_IMAGE_CODE2:
                setBitmapToImageView(resultCode, data, eventImage2);
                filePath2 = placeholderPath;
                file2 = new File(filePath2);
                break;
            case SELECT_IMAGE_CODE3:
                setBitmapToImageView(resultCode, data, eventImage3);
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
                placeholderPath = Helper.getRealPathFromURI_API19(this, data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int month = 1 + monthOfYear;
        String day = (dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth +"";
        String mth = (month < 10) ? "0" + month : month + "";
        editDate.setText(String.format("%s/%s/%s", day, mth, year));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min = (minute < 10) ? "0" + minute : minute + "";
        String hour = (hourOfDay < 10) ? "0" + hourOfDay : hourOfDay + "";
        editTime.setText(String.format("%s:%s", hour, min));
    }

    @Override
    protected void onDestroy() {
        clearForm();
        super.onDestroy();
    }

    private void clearForm() {
        editName.setText(null);
        editDate.setText(null);
        editTime.setText(null);
        editLocation.setText(null);
        editDetail.setText(null);
        eventImage.setImageDrawable(null);
        eventImage2.setImageDrawable(null);
        eventImage3.setImageDrawable(null);
    }
}
