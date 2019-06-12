package yousef.joe.containersmaps.ui;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;

import java.util.ArrayList;
import java.util.List;

import yousef.joe.containersmaps.Api;
import yousef.joe.containersmaps.ClusterRenderer;
import yousef.joe.containersmaps.CustomInfoWindowAdapter;
import yousef.joe.containersmaps.R;
import yousef.joe.containersmaps.Utils;
import yousef.joe.containersmaps.model.Container;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap map;
    private ClusterManager<Container> clusterManager;
    private TextView markerView;
    ProgressBar progressBar;
    SupportMapFragment mapFragment;
    FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        progressBar = findViewById(R.id.progress_bar);

        // Inflate cluster item icon
        // Why passing the inflated view directly doesn't work???
        View inflatedView = LayoutInflater.from(this).inflate(R.layout.info_view, null);
        markerView = inflatedView.findViewById(R.id.info_view);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Setup cluster manager
        initClusterManager();

        // Move the camera to a default position
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Utils.EVREKA_LAT_LNG, 5));


        // get markers info from the Database
        getDataFromCloud();


        // Set only the marker to be clickable
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String containerJson = marker.getTitle();

                // Open Marker Detail Activity to let user chang marker position
                Intent intent = new Intent(MapsActivity.this,
                        MarkerDetailActivity.class);
                intent.putExtra(MarkerDetailActivity.MARKER_DETAILS_EXTRA, containerJson);
                startActivity(intent);
            }
        });

        map.setOnCameraIdleListener(clusterManager);

    }

    private void initClusterManager() {
        // Attach cluster manager to the map
        clusterManager = new ClusterManager<>(this, map);

        // get device metrics (for device width and height)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Render only visible items
        NonHierarchicalViewBasedAlgorithm onlyVisibleItemsAlgorithm
                = new NonHierarchicalViewBasedAlgorithm<Container>(metrics.widthPixels, metrics.heightPixels);
        clusterManager.setAlgorithm(onlyVisibleItemsAlgorithm);

        // Change marker icon
        clusterManager.setRenderer(new ClusterRenderer(getApplicationContext(),
                map, clusterManager, markerView));

        // Set Custom adapter for marker InfoWindow
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(this));
    }


    void getDataFromCloud() {
        // Show progress bar to user before starting to getting the info
        progressBar.setVisibility(View.VISIBLE);

        Api.getContainers(this,new Api.ContainersReceived() {
            @Override
            public void onDataReceived(List<Container> containersList) {
                displayItemsToMap(containersList);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Display markers to the map
    private void displayItemsToMap(List<Container> containersList) {
        // Clear old data
        map.clear();
        clusterManager.clearItems();

        // Set new data
        clusterManager.addItems(containersList);
        clusterManager.cluster();
    }


}
