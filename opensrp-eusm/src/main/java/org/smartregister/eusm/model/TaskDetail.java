package org.smartregister.eusm.model;

import java.io.Serializable;
import java.util.Date;

public class TaskDetail implements Serializable {

    private String taskId;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String productName) {
        this.entityName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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
}