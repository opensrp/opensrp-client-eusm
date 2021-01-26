package org.smartregister.eusm.domain;

import org.smartregister.tasking.model.CardDetails;

public class EusmCardDetail extends CardDetails {
    private String structureId;
    private Float distance;
    private String distanceMeta;
    private String taskStatus;
    private String structureName;
    private String structureType;
    private String commune;
    private String numOfTasks;
    private String communeId;

    public EusmCardDetail(String status) {
        super(status);
    }

    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getDistanceMeta() {
        return distanceMeta;
    }

    public void setDistanceMeta(String distanceMeta) {
        this.distanceMeta = distanceMeta;
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

    public String getNumOfTasks() {
        return numOfTasks;
    }

    public void setNumOfTasks(String numOfTasks) {
        this.numOfTasks = numOfTasks;
    }

    public String getCommuneId() {
        return communeId;
    }

    public void setCommuneId(String communeId) {
        this.communeId = communeId;
    }
}
