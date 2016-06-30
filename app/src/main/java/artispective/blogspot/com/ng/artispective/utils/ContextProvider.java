package artispective.blogspot.com.ng.artispective.utils;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

public class ContextProvider extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(context);
    }

    public static Context getContext() {
        return context;
    }
}
