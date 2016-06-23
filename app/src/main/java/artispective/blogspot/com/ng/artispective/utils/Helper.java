package artispective.blogspot.com.ng.artispective.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import artispective.blogspot.com.ng.artispective.models.model.Event;

public class Helper {

    private static SharedPreferences sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ContextProvider.getContext());

    public static void launchActivity(Context context, Class dest) {
        context.startActivity(new Intent(context, dest));
    }

    public static String getUserData(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static void setUserData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public static void setUserAdminStatus(boolean adminStatus) {
        sharedPreferences.edit().putBoolean("user_admin", adminStatus).apply();
    }

    public static boolean getUserAdminStatus() {
        return sharedPreferences.getBoolean("user_admin", false);
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        if (uri.getHost().contains("com.android.providers.media")) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media
                            .EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            return  getRealPathFromURI_BelowAPI11(context,uri);
        }

    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void hideSoftKeyboard(Context context, View view) {
        try {
            InputMethodManager im;
            im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showToast(String message) {
        Toast.makeText(ContextProvider.getContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void addToCalendar(Context context, Event e) {
        Calendar beginTime = Calendar.getInstance();
        String date = e.getDate();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]) - 1;
        int day = Integer.parseInt(dates[2].substring(0,2));
        int hour = Integer.parseInt(dates[2].substring(3,5));
        int minute = Integer.parseInt(dates[2].substring(6,8));
        beginTime.set(year, month, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, 22, 0);
        String details;
        details = (e.getDetails().length() < 100)? e.getDetails(): e.getDetails().substring(0,100);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, e.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, details)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, e.getAddress())
                .putExtra(CalendarContract.Events.ALLOWED_REMINDERS, CalendarContract.Events.MAX_REMINDERS)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
        context.startActivity(intent);

    }

    public static void sendNote(Context context, Event event) {
        String u;
        u = (event.getImages().size() > 0)? event.getImages().get(0): Constants.DEFAULT_IMAGE;
        Uri pictureUri = Uri.parse(u);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, event.getDetails());
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.putExtra(Intent.EXTRA_TITLE, event.getTitle());
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share images..."));
    }


    @Nullable
    public static Uri getLocalBitmapUri(Context context, ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        Uri bmpUri = null;
        try {

            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 60, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static String formatDate(String date) {
        String[] dateArray = date.split("-");
        return dateArray[2].substring(0, 2) + "/" + dateArray[1] + "/" + dateArray[0]+"  ";
    }

    public static void launchHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
