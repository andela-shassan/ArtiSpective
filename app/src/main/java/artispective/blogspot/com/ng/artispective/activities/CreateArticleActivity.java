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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import artispective.blogspot.com.ng.artispective.R;
import artispective.blogspot.com.ng.artispective.models.article.ArticleResponse;
import artispective.blogspot.com.ng.artispective.models.article.Post;
import artispective.blogspot.com.ng.artispective.utils.ConnectionChecker;
import artispective.blogspot.com.ng.artispective.utils.Endpoint;
import artispective.blogspot.com.ng.artispective.utils.Helper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CreateArticleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2324;
    private static final int SELECT_IMAGE_CODE = 1960;
    private static final int SELECT_IMAGE_CODE2 = 1961;
    private static final int SELECT_IMAGE_CODE3 = 1962;
    private EditText editTitle, editDetails;
    private String articleTitle, articleDetails;
    private ImageView articleImage;
    private Button saveButton;
    public Bitmap bitmap, bitmap2, bitmap3;
    private ProgressDialog progressDialog;
    private Bitmap placeHolderBitmap;
    private File file, file2, file3;
    private String filePath, filePath2, filePath3;
    private String placeholderPath;
    private String userId, userToken;
    private RequestBody usersId, usersToken, asTitle, asDetails, articlesImage;
    private Post post;
    private RequestBody postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        findViews();
        permissionChecker();

        Toolbar toolbar = (Toolbar) findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("New Article");

        getPostToEdit();
    }

    private void getPostToEdit() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("post")) {
            post = intent.getParcelableExtra("post");
            setTitle("Edit Article");
            saveButton.setText(R.string.update);
            setUpEditView(post);
        }
    }

    private void setUpEditView(Post post) {
        editTitle.setText(post.getHeading());
        editDetails.setText(post.getBody());
        Picasso.with(this).load(post.getImage()).into(articleImage);
    }

    private void findViews() {
        editTitle = (EditText) findViewById(R.id.article_title);
        editDetails = (EditText) findViewById(R.id.article_details);
        saveButton = (Button) findViewById(R.id.article_save_button);
        saveButton.setOnClickListener(this);
        articleImage = (ImageView) findViewById(R.id.article_banner1);
        articleImage.setOnClickListener(this);
        userId = Helper.getUserData("user_id");
        userToken = Helper.getUserData("user_token");
    }


    private void permissionChecker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(editTitle, R.string.read_external_storage, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull
    final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(editTitle, "Permission Granted", Snackbar.LENGTH_LONG);
            } else {
                Snackbar.make(editDetails, "Permission Require to continue", Snackbar.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.article_save_button:
                attemptSaveArticle();
                break;
            case R.id.article_banner1:
                openGallery(SELECT_IMAGE_CODE);
                break;
            default:
                break;

        }
    }

    private void attemptSaveArticle() {
        setErrorToNull();
        Helper.hideSoftKeyboard(this, saveButton);

        this.articleTitle = editTitle.getText().toString().trim();
        this.articleDetails = editDetails.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(articleTitle)) {
            this.editTitle.setError(getString(R.string.error_field_required));
            focusView = this.editTitle;
            cancel = true;
        }

        if (TextUtils.isEmpty(articleDetails)) {
            this.editDetails.setError(getString(R.string.error_field_required));
            focusView = this.editDetails;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (ConnectionChecker.isConnected()) {
                showProgressDialog();

                usersId = RequestBody.create(MediaType.parse("text/plain"), this.userId);
                usersToken = RequestBody.create(MediaType.parse("text/plain"), this.userToken);
                asTitle = RequestBody.create(MediaType.parse("text/plain"), this.articleTitle);
                asDetails = RequestBody.create(MediaType.parse("text/plain"), this.articleDetails);
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
                if (file != null) {
                    articlesImage = RequestBody.create(MEDIA_TYPE_PNG, file);
                }
                if (post != null) {
                    postId = RequestBody.create(MediaType.parse("text/plain"), post.getId());
                }

                if (getTitle().toString().contains("New") ||
                        saveButton.getText().toString().contains("Save")) {
                    uploadNewArticle();

                } else {
                    uploadEditedArticleWithoutFile();
                }

            } else {
                ConnectionChecker.showNoNetwork();
            }
        }
    }

    private void setErrorToNull() {
        editTitle.setError(null);
        editDetails.setError(null);
    }

    private void uploadNewArticle() {
        Observable<ArticleResponse> observable = Endpoint.RxFactory.getEndpoint()
                .rxAddArticle(userToken, usersId, asTitle, asDetails, articlesImage);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArticleResponse>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressDialog();
                        Helper.launchActivity(CreateArticleActivity.this, HomeActivity.class);
                        Helper.showToast("Article added successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Log.e("semiu upload article", e.getMessage());
                        Helper.showToast("Something went wrong. Try again");
                    }

                    @Override
                    public void onNext(ArticleResponse articleResponse) {

                    }
                });
    }

    private void uploadEditedArticleWithoutFile() {
        Observable<ArticleResponse> observable = Endpoint.RxFactory.getEndpoint()
                .rxUpdateArticle(userToken, userId, post.getId(), articleTitle, articleDetails);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArticleResponse>() {
                    @Override
                    public void onCompleted() {
                        updateResponse();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Helper.showToast("Something went wrong. Try again");
                        Log.e("semiu up woI", e.getMessage());
                    }

                    @Override
                    public void onNext(ArticleResponse articleResponse) {

                    }
                });
    }

    private void uploadEditedArticleWithFile() {
        Observable<ArticleResponse> observable = Endpoint.RxFactory.getEndpoint()
                .rxUpdateArticle(userToken, usersId, postId, asTitle, asDetails, articlesImage);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArticleResponse>() {
                    @Override
                    public void onCompleted() {
                        updateResponse();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Helper.showToast("Something went wrong. Try again");
                        Log.e("semiu up wI", e.getMessage());
                    }

                    @Override
                    public void onNext(ArticleResponse articleResponse) {

                    }
                });
    }

    private void updateResponse() {
        dismissProgressDialog();
        Helper.showToast("Article Updated successfully");
        Helper.launchActivity(CreateArticleActivity.this, HomeActivity.class);
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
                setBitmapToImageView(resultCode, data, articleImage);
                bitmap = placeHolderBitmap;
                filePath = placeholderPath;
                 file = new File(filePath);
                break;
            case SELECT_IMAGE_CODE2:
//                setBitmapToImageView(resultCode, data, articleImage2);
                bitmap2 = placeHolderBitmap;
                filePath2 = placeholderPath;
                file2 = new File(filePath2);
                break;
            case SELECT_IMAGE_CODE3:
//                setBitmapToImageView(resultCode, data, articleImage3);
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Event " + articleTitle + " is being created");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        clearForm();
        super.onDestroy();
    }

    private void clearForm() {
        editTitle.setText(null);
        editDetails.setText(null);
        articleImage.setImageDrawable(null);
    }
}
