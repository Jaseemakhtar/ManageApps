package com.jsync.appsdeaddiction;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jaseem on 30/4/19.
 */

public class TabOne extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<AppsListModel> appsList;
    private AppsListAdapter adapter;

    public  TabOne(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_one, container, false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.apps_recycler_view);
        adapter = new AppsListAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        createList();
        return view;
    }

    private void createList(){
        PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        for(ResolveInfo i : activities){
            try {

                String packageName = i.activityInfo.packageName;

                if(packageName.equals(getActivity().getPackageName()))
                    continue;

                String appName = i.activityInfo.loadLabel(packageManager).toString();
                String versionName = packageManager.getPackageInfo(i.activityInfo.packageName, 0).versionName;
                long installedOn = packageManager.getPackageInfo(i.activityInfo.packageName, 0).firstInstallTime;
                long updatedOn = packageManager.getPackageInfo(i.activityInfo.packageName, 0).lastUpdateTime;
                Drawable icon = i.activityInfo.loadIcon(packageManager);

                /*
                Log.i("ada", "*******************************************");
                Log.i("ada","Package Name: " + packageName);
                Log.i("ada","Version Name: " + versionName);
                Log.i("ada", "Install Time: " + installedOn);
                Log.i("ada", "Last Update Time: " + updatedOn);
                Log.i("ada","App Name: " + appName);
                Log.i("ada", "*******************************************");
                */
                AppsListModel model = new AppsListModel();
                model.setAppName(appName);
                model.setAppPackageName(packageName);
                model.setVersionName(versionName);
                model.setInstalledOn(installedOn);
                model.setUpdatedOn(updatedOn);
                model.setAppIcon(icon);
                adapter.add(model);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
