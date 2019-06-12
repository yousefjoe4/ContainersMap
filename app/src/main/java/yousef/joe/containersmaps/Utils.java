package yousef.joe.containersmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.maps.android.ui.IconGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import yousef.joe.containersmaps.model.Container;
import yousef.joe.containersmaps.ui.MapsActivity;

public class Utils {

    public static final LatLng EVREKA_LAT_LNG = new LatLng(39.898942, 32.776174);


    // Generate a BitmapDescriptor
    public static BitmapDescriptor getCustomMarkerView(Context context, int occupancyRate){
        // Get the custom view needed
        View view = inflateContainerView(context, occupancyRate);

        IconGenerator iconGenerator = new IconGenerator(context);

        if(view.getParent() != null){
            ((ViewGroup)view.getParent()).removeView(view);
            iconGenerator.setContentView(view);
        }

        // Covert icon generator into a bitmap
        Bitmap icon = iconGenerator.makeIcon();

       return BitmapDescriptorFactory.fromBitmap(icon);
    }

    // Inflate new TextView and add the occupancy rate to it
    private static TextView inflateContainerView(Context context, int occupancyRate) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.info_view, null);
        TextView markerView = inflatedView.findViewById(R.id.info_view);

        String rate = occupancyRate + "%";
        markerView.setText(rate);

        return markerView;
    }

    public static List<Container> generateDummyContainers() {
        List<Container> containerList = new ArrayList<>();
        // Set some lat/lng coordinates to start with.
        double lat = EVREKA_LAT_LNG.latitude;
        double lng = EVREKA_LAT_LNG.longitude;

        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            // generate random values
            int latRandomNumber = random.nextInt(11) - 5;
            int lngRandomNumber = random.nextInt(11) - 5;
            int rateRandomNumber = (random.nextInt(11)) * 10;
            int tempRandomNumber = random.nextInt(36);

            // set container and sensor IDs
            int containerId = i;
            int sensorId = i + 30;

            double latOffset = latRandomNumber / 100d;
            double lngOffset = lngRandomNumber / 100d;

            lat = lat + latOffset;
            lng = lng + lngOffset;
            Container container = new Container(
                    lat,
                    lng,
                    containerId,
                    sensorId,
                    rateRandomNumber,
                    tempRandomNumber,
                    getCurrentDate());

            String containerJson = new Gson().toJson(container);
            container.setSnippet(containerJson);
            containerList.add(container);
        }
        return containerList;
    }
    private static String getCurrentDate() {
        Date calender = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(calender);
    }
}
