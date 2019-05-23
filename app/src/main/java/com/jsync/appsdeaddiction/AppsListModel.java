package com.jsync.appsdeaddiction;

import android.graphics.drawable.Drawable;

/**
 * Created by jaseem on 1/5/19.
 */

public class AppsListModel {
    private String appName;
    private String appPackageName;
    private String versionName;
    private long installedOn;
    private long updatedOn;
    private String from;
    private String to;
    private String appIcon;
    private int rowId = -1;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(long installedOn) {
        this.installedOn = installedOn;
    }

    public long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String uIcon) {
        this.appIcon = uIcon;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }
}
