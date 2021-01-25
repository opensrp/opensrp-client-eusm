package org.smartregister.eusm.domain;

import androidx.annotation.NonNull;

import org.smartregister.tasking.model.TaskDetails;

import java.util.Date;

public class TaskDetail extends TaskDetails {

    private String entityName;

    private String productImage;

    private boolean header;

    private String quantity;

    private String productSerial;

    private Date dateChecked;

    private boolean checked;

    private boolean nonProductTask;

    private boolean emptyView;

    private boolean hasProblem;

    private String productId;

    private String condition;

    private String appropriateUsage;

    private String availability;

    private String stockId;

    private String planId;

    private String forEntity;

    private String groupId;

    public TaskDetail(@NonNull String taskId) {
        super(taskId);
    }

    public TaskDetail() {
        super("");
    }

    public boolean hasProblem() {
        return hasProblem;
    }

    public void setHasProblem(boolean hasProblem) {
        this.hasProblem = hasProblem;
    }

    public boolean isEmptyView() {
        return emptyView;
    }

    public void setEmptyView(boolean emptyView) {
        this.emptyView = emptyView;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String productName) {
        this.entityName = productName;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProductSerial() {
        return productSerial;
    }

    public void setProductSerial(String productSerial) {
        this.productSerial = productSerial;
    }

    public Date getDateChecked() {
        return dateChecked;
    }

    public void setDateChecked(Date dateChecked) {
        this.dateChecked = dateChecked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isNonProductTask() {
        return nonProductTask;
    }

    public void setNonProductTask(boolean nonProductTask) {
        this.nonProductTask = nonProductTask;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAppropriateUsage() {
        return appropriateUsage;
    }

    public void setAppropriateUsage(String appropriateUsage) {
        this.appropriateUsage = appropriateUsage;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getForEntity() {
        return forEntity;
    }

    public void setForEntity(String forEntity) {
        this.forEntity = forEntity;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
