package yousef.joe.containersmaps.model;

public class LatLng {
        private double latitude;
        private double longitude;

        public LatLng() {}
        public LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
