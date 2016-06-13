package artispective.blogspot.com.ng.artispective.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
}
