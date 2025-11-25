package com.polyu.cmms.model;

import java.util.Date;

public class Activity {
    private int activityId;
    private String activityType;
    private String title;
    private String description;
    private String status;
    private Date date;
    private Date expectedDowntime;
    private int createdByStaffId;
    private Integer weatherId;
    private Integer buildingId;
    private Integer areaId;
    private String hazardLevel;
    
    // 构造函数、getter和setter方法
    public Activity() {}
    
    public Activity(int activityId, String activityType, String title, String description, 
                   String status, Date date, Date expectedDowntime, int createdByStaffId, 
                   Integer weatherId, Integer buildingId, Integer areaId, String hazardLevel) {
        this.activityId = activityId;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
        this.expectedDowntime = expectedDowntime;
        this.createdByStaffId = createdByStaffId;
        this.weatherId = weatherId;
        this.buildingId = buildingId;
        this.areaId = areaId;
        this.hazardLevel = hazardLevel;
    }
    
    // getter and setter methods
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Date getExpectedDowntime() { return expectedDowntime; }
    public void setExpectedDowntime(Date expectedDowntime) { this.expectedDowntime = expectedDowntime; }
    public int getCreatedByStaffId() { return createdByStaffId; }
    public void setCreatedByStaffId(int createdByStaffId) { this.createdByStaffId = createdByStaffId; }
    public Integer getWeatherId() { return weatherId; }
    public void setWeatherId(Integer weatherId) { this.weatherId = weatherId; }
    public Integer getBuildingId() { return buildingId; }
    public void setBuildingId(Integer buildingId) { this.buildingId = buildingId; }
    public Integer getAreaId() { return areaId; }
    public void setAreaId(Integer areaId) { this.areaId = areaId; }
    public String getHazardLevel() { return hazardLevel; }
    public void setHazardLevel(String hazardLevel) { this.hazardLevel = hazardLevel; }
}