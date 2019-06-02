package yousef.joe.containersmaps.model;


import android.util.Log;

import com.google.maps.android.clustering.ClusterItem;
import java.util.Date;

public class Container implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int occupancyRate;
    private int containerId;
    private int sensorId;
    private int containerTemp;
    private Date date;



    // Used to set dragged markers positions
    private LatLng oldPosition;

    public Container(double lat, double lng, int containerId, int sensorId,
                     int occupancyRate, int containerTemp, Date date) {
        this.position = new LatLng(lat, lng);
        this.containerId = containerId;
        this.sensorId = sensorId;
        this.occupancyRate = occupancyRate;
        this.containerTemp = containerTemp;
        this.date = date;

    }

    public Container(){
        // Empty Constuctor required by Firebase Firestore

    }


    @Override
    public com.google.android.gms.maps.model.LatLng getPosition() {
        if(position != null){
            return new com.google.android.gms.maps.model.LatLng(position.getLatitude(),
                    position.getLongitude());
        }
    return null;
    }

    @Override
    public String getTitle() {
        Log.e(getClass().getName(),"getTitlte is called");
        return title;
    }

    // Return JSON object of this Container, to be able to get it back when infoView opens
    @Override
    public String getSnippet() {
        return snippet;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getOccupancyRate() {
        return occupancyRate;
    }

    public int getContainerId() {
        return containerId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public int getContainerTemp() {
        return containerTemp;
    }

    public Date getDate() {
        return date;
    }

    public LatLng getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(LatLng oldPosition) {
        if(this.oldPosition == null){
            this.oldPosition = oldPosition;
        }
    }

    public void setPosition(LatLng  position) {

        this.position = position;
    }

    public void setOccupancyRate(int occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public void setContainerTemp(int containerTemp) {
        this.containerTemp = containerTemp;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
