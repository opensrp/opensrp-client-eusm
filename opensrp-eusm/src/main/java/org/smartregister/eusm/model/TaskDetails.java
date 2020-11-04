package org.smartregister.eusm.model;

import android.location.Location;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.util.AppUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.smartregister.eusm.util.AppConstants.BusinessStatus.BEDNET_DISTRIBUTED;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.BLOOD_SCREENING_COMPLETE;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.COMPLETE;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.FAMILY_REGISTERED;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.FULLY_RECEIVED;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.NONE_RECEIVED;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.NOT_ELIGIBLE;
import static org.smartregister.eusm.util.AppConstants.BusinessStatus.NOT_VISITED;
import static org.smartregister.eusm.util.AppConstants.COMMA;
import static org.smartregister.eusm.util.AppConstants.HYPHEN;
import static org.smartregister.eusm.util.AppConstants.Intervention.BEDNET_DISTRIBUTION;
import static org.smartregister.eusm.util.AppConstants.Intervention.BLOOD_SCREENING;
import static org.smartregister.eusm.util.AppConstants.Intervention.CASE_CONFIRMATION;
import static org.smartregister.eusm.util.AppConstants.Intervention.MDA_ADHERENCE;
import static org.smartregister.eusm.util.AppConstants.Intervention.MDA_DISPENSE;
import static org.smartregister.eusm.util.AppConstants.Intervention.REGISTER_FAMILY;

/**
 * Created by samuelgithengi on 3/20/19.
 */
public class TaskDetails extends BaseTaskDetails implements Comparable<TaskDetails>, Serializable {

    private Location location;

    private String structureName;

    private String familyName;

    private float distanceFromUser;

    private String sprayStatus;

    private String taskDetails;

    private boolean distanceFromCenter;

    private Integer taskCount;

    private Integer completeTaskCount;

    private boolean familyRegistered;

    private boolean bednetDistributed;

    private boolean bloodScreeningDone;

    private String reasonReference;

    private String houseNumber;

    private boolean familyRegTaskExists;

    private boolean mdaAdhered;

    private boolean fullyReceived;

    private boolean partiallyReceived;

    private boolean noneReceived;

    private boolean notEligible;

    private String aggregateBusinessStatus;

    public TaskDetails(@NonNull String taskId) {
        super(taskId);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public String getSprayStatus() {
        return sprayStatus;
    }

    public void setSprayStatus(String sprayStatus) {
        this.sprayStatus = sprayStatus;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    public boolean isDistanceFromCenter() {
        return distanceFromCenter;
    }

    public void setDistanceFromCenter(boolean distanceFromCenter) {
        this.distanceFromCenter = distanceFromCenter;
    }

    public Integer getCompleteTaskCount() {
        return completeTaskCount;
    }

    public void setCompleteTaskCount(Integer completeTaskCount) {
        this.completeTaskCount = completeTaskCount;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public boolean isFamilyRegistered() {
        return familyRegistered;
    }

    public boolean isFamilyRegTaskExists() {
        return familyRegTaskExists;
    }

    public void setFamilyRegTaskExists(boolean familyRegTaskExists) {
        this.familyRegTaskExists = familyRegTaskExists;
    }


    public boolean isNotEligible() {
        return notEligible;
    }



    private boolean isFamilyRegisteredOrNoTaskExists() {
        return isFamilyRegistered() || !isFamilyRegTaskExists();
    }


    @Override
    public int compareTo(@NonNull TaskDetails other) {
        return Double.compare(distanceFromUser, other.getDistanceFromUser());
    }
}
