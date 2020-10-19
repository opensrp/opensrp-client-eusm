package org.smartregister.eusm.model;

public class ServicePoint {
    private boolean nearby;
    private double distance;
    private boolean isHeader;
    private String status;
    private String name;
    private String type;

    public boolean isNearby() {
        return nearby;
    }

    public void setNearby(boolean nearby) {
        this.nearby = nearby;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
