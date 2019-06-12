package yousef.joe.containersmaps.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import yousef.joe.containersmaps.Api;
import yousef.joe.containersmaps.InternetConnectivity;
import yousef.joe.containersmaps.R;
import yousef.joe.containersmaps.Utils;
import yousef.joe.containersmaps.model.Container;

public class MarkerDetailActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    static final String MARKER_DETAILS_EXTRA = "markerDetailsExtra";
    Container container;
    private GoogleMap mMap;
    MarkerOptions marker;
    Button saveButton;
    Button cancelButton;
    private static final String DIALOG_SHOWN = "dialogShown";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);

        sharedPreferences = getSharedPreferences(getClass().getName(), MODE_PRIVATE);

        // Get container details from the previous activity
        String containerJson = getIntent().getStringExtra(MARKER_DETAILS_EXTRA);
        container = new Gson().fromJson(containerJson, Container.class);

        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get marker position and set it to the container
                LatLng latLng = marker.getPosition();
                yousef.joe.containersmaps.model.LatLng position =
                        new yousef.joe.containersmaps.model.LatLng(latLng.latitude, latLng.longitude);

                container.setPosition(position);

                // Get new JSON version fo the current state of the container
                String containerJson = new Gson().toJson(container);

                container.setSnippet(containerJson);

                // Update the container details to the cloud
                updateContainer();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // if the container is null close the activity
        if (container == null) {
            // Send Telemetries
            Bundle bundle = new Bundle();
            bundle.putString("onMapReady", " container is = null");
            FirebaseAnalytics.getInstance(this).logEvent("MarkerDetailActivity", bundle);

            Toast.makeText(this, "An error has occurred", Toast.LENGTH_LONG).show();
            finish();
        }


        // If it's first time for the user to change marker position, inform them how to change it
        if (!sharedPreferences.getBoolean(DIALOG_SHOWN, false)) {
            showTutorialDialog();
        }

        // get Container position
        LatLng markerPosition = container.getPosition();

        // Prepare the marker with the position
        marker = new MarkerOptions().position(markerPosition);

        // Get custom view for the marker
        BitmapDescriptor customMarkerView = Utils.getCustomMarkerView(this, container.getOccupancyRate());

        // Change marker icon
        marker.icon(customMarkerView);

        marker.draggable(true);

        // Add the marker to the map
        mMap.addMarker(marker);


        mMap.setOnMarkerDragListener(this);

        // Move the camera of the map to the position of the marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 13));
    }

    // Tell the user that they could change the container position by dragging it
    private void showTutorialDialog() {
        // Set in the memory that this user has been shown a message already
        sharedPreferences.edit().putBoolean(DIALOG_SHOWN, true).apply();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage("Long press the container to drag it and change it's position");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        dialog.show();
    }


    @Override
    public void onMarkerDragStart(Marker internalMarker) {

    }

    @Override
    public void onMarkerDrag(Marker internalMarker) {

    }

    @Override
    public void onMarkerDragEnd(Marker internalMarker) {
        // show save and cancel buttons
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        // Set the new position to the original position
        marker.position(internalMarker.getPosition());
    }


    private void updateContainer() {
        if (InternetConnectivity.isOnline(this)) {
            Api.updateContainer(MarkerDetailActivity.this, container, new Api.UpdateContainer() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MarkerDetailActivity.this,
                            "Container updated successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(MarkerDetailActivity.this,
                    "Please check your internet connectivity", Toast.LENGTH_LONG).show();
        }

    }
}
