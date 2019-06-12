package yousef.joe.containersmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import yousef.joe.containersmaps.model.Container;

public class ClusterRenderer extends DefaultClusterRenderer<Container> {

    private Context context;
    private TextView view;

    public ClusterRenderer(Context context, GoogleMap googleMap,
                           ClusterManager<Container> clusterManager, TextView view) {
        super(context, googleMap, clusterManager);

        this.context = context;
        this.view = view;
    }

    @Override
    protected void onBeforeClusterItemRendered(Container container, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(container, markerOptions);

        if(container != null){
            BitmapDescriptor customMarker = Utils.getCustomMarkerView(context,
                    container.getOccupancyRate());

            markerOptions.icon(customMarker);

        }


    }
}
