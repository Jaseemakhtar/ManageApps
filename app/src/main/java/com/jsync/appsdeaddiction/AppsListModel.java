package com.jsync.appsdeaddiction;

import android.graphics.drawable.Drawable;

/**
 * Created by jaseem on 1/5/19.
 */

public class AppsListModel {
    private String appName;
    private Drawable appIcon;
    private String appPackageName;
    private String versionName;
    private long installedOn;
    private long updatedOn;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
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
}
