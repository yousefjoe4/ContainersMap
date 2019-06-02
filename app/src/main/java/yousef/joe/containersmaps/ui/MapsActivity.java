package yousef.joe.containersmaps.ui;


import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import yousef.joe.containersmaps.ClusterRenderer;
import yousef.joe.containersmaps.CustomInfoWindowAdapter;
import yousef.joe.containersmaps.R;
import yousef.joe.containersmaps.model.Container;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap map;
    private ClusterManager<Container> clusterManager;
    private final String LOG_TAG = getClass().getSimpleName();
    private TextView markerView;
    ProgressBar progressBar;
    List<Container> containersList = new ArrayList<>();
    SupportMapFragment mapFragment;
    CollectionReference collectionReference =
            FirebaseFirestore.getInstance().collection("Containers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.e(LOG_TAG, "size onCrate = " + containersList.size());


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

        // get markers info from the Database
        getDataFromCloud();

        // Move the camera to a default position
        final LatLng mark = new LatLng(39.898942, 32.776174);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 5));

        // get device metrics (for device c and height)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        // Attach cluster manager to the map
        clusterManager = new ClusterManager<>(this, map);


        // Render only visible items
        NonHierarchicalViewBasedAlgorithm onlyVisibleItemsAlgorithm
                = new NonHierarchicalViewBasedAlgorithm<Container>(metrics.widthPixels,metrics.heightPixels);
        clusterManager.setAlgorithm(onlyVisibleItemsAlgorithm);

        // Change marker view
        clusterManager.setRenderer(new ClusterRenderer(getApplicationContext(),
                map, clusterManager, markerView));

        // Set Custom adapter for marker InfoWindow
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());

        map.setOnCameraIdleListener(clusterManager);

        // Listener to whenever an item is dragged
        map.setOnMarkerDragListener(this);

    }


    @Override
    public void onMarkerDragStart(Marker marker) {
// Item updating logic goes here
    }


    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Item updating logic goes here

    }

    void getDataFromCloud(){
        // Show progress bar to user before starting to getting the info
        progressBar.setVisibility(View.VISIBLE);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();

                    // Iterate over each document and convert it into a Container item
                    for(DocumentSnapshot document : data){
                        // Database object into Container
                        Container container = document.toObject(Container.class);

                        // add the container to the list
                        if(container!=null){
                            containersList.add(container);
                        }
                    }
                    // After all the containers have been added to the list display the list
                    displayItems(containersList);

                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // Display markers to the map
    private void displayItems(List<Container> containersList) {
        clusterManager.addItems(containersList);
        clusterManager.cluster();
    }


}
