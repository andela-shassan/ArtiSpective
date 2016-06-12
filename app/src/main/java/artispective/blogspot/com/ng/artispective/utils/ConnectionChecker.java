package artispective.blogspot.com.ng.artispective.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import artispective.blogspot.com.ng.artispective.R;

/**
 * Created by Nobest on 15/05/2016.
 */
public class ConnectionChecker {

    private static boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ContextProvider.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private static boolean isDataNetworkConnected(){
        Runtime runtime = Runtime.getRuntime();
        try {
            java.lang.Process process = runtime.exec("system/bin/ping -c 1 8.8.8.8");
            return process.waitFor() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnected(){
        return isNetworkAvailable() && isDataNetworkConnected();
    }

    public static void showNoNetwork(){
        Toast.makeText(ContextProvider.getContext(), ContextProvider.getContext()
                .getString(R.string.no_network_text), Toast.LENGTH_LONG).show();
        Log.w("semiu", "Network error can not fetch the events");
    }
}