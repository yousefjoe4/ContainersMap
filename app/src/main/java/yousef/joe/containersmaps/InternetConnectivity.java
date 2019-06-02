package yousef.joe.containersmaps;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetConnectivity {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return
                cm != null &&
                        cm.getActiveNetworkInfo() != null &&
                        cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
