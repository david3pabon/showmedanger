package io.stabilitas.locator.model;

/**
 * Created by David Pabon (@david3pabon)
 */
public class Report {

    private String type;
    private double latitude;
    private double longitud;

    private Report() {}

    public static Report newInstance(String type, double latitude, double longitud) {
        Report report = new Report();
        report.setType(type);
        report.setLatitude(latitude);
        report.setLongitud(longitud);
        return report;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
