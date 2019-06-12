package yousef.joe.containersmaps.model;

import com.google.maps.android.clustering.ClusterItem;

public class Container implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int occupancyRate;
    private int containerId;
    private int sensorId;
    private int containerTemp;
    private String date;

    public Container(double lat, double lng, int containerId, int sensorId,
                     int occupancyRate, int containerTemp, String date) {
        this.position = new LatLng(lat, lng);
        this.containerId = containerId;
        this.sensorId = sensorId;
        this.occupancyRate = occupancyRate;
        this.containerTemp = containerTemp;
        this.date = date;

    }

    public Container(){
        // Empty Constructor required by Firebase Firestore

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
        return title;
    }

    // Return JSON object of this Container, to be able to get it back when infoView opens
    @Override
    public String getSnippet() {
        return snippet;
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

    public String getDate() {
        return date;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
