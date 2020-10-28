package org.smartregister.eusm.model;

import java.io.Serializable;

public class StructureDetail implements Serializable {
    private boolean nearby;
    private Float distance;
    private String distanceMeta;
    private boolean isHeader;
    private String taskStatus;
    private String structureName;
    private String structureType;
    private String commune;

    public String getDistanceMeta() {
        return distanceMeta;
    }

    public void setDistanceMeta(String distanceMeta) {
        this.distanceMeta = distanceMeta;
    }

    public boolean isNearby() {
        return nearby;
    }

    public void setNearby(boolean nearby) {
        this.nearby = nearby;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getStructureType() {
        return structureType;
    }

    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    @Override
    public String toString() {
        return "StructureDetail{" +
                "nearby=" + nearby +
                ", distance=" + distance +
                ", distanceMeta='" + distanceMeta + '\'' +
                ", isHeader=" + isHeader +
                ", taskStatus='" + taskStatus + '\'' +
                ", structureName='" + structureName + '\'' +
                ", structureType='" + structureType + '\'' +
                ", commune='" + commune + '\'' +
                '}';
    }
}
