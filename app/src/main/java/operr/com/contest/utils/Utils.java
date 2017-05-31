package operr.com.contest.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import io.realm.RealmList;
import operr.com.contest.models.RealmString;

/**
 * Created by Sindhura on 5/30/2017.
 */

public class Utils {

    public static void requestInternetConnectionIfNeeded(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(context, "Please connect to the internet for better results", Toast.LENGTH_LONG).show();
        }
    }

    public static String getAddress(RealmList<RealmString> displayAddress) {
        StringBuffer buffer = new StringBuffer();
        for (RealmString address : displayAddress) {
            buffer.append(address.getData()).append(", ");
        }
        return buffer.substring(0, buffer.length() - 2);
    }

    public static void call(Context context, String telephone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + telephone));
        context.startActivity(intent);
    }
}
