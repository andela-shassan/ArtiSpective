package artispective.blogspot.com.ng.artispective.utils;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.leakcanary.LeakCanary;

public class ContextProvider extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(this);
        LeakCanary.install((Application) context);
    }

    public static Context getContext() {
        return context;
    }
}