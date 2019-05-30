package com.jsync.appsdeaddiction;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jaseem on 30/4/19.
 */

public class TabThree extends Fragment {
    private RecyclerView recyclerView;
    private UsageStatsManager mUsageStatsManager;
    private LinearLayoutManager mLayoutManager;
    private StatisticsListAdapter adapter;
    private ArrayList<String> packageNames;
    private ArrayList<StatisticsListModel> appsList;
    public TabThree(){
        packageNames = new ArrayList<>();
        appsList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_three, container, false);
        recyclerView = view.findViewById(R.id.statistics_recycler_view);

        adapter = new StatisticsListAdapter();

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        createList();
        return view;
    }

    public void createList(){
        appsList.clear();
        packageNames.clear();
        mUsageStatsManager = (UsageStatsManager)getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);

        PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        for(ResolveInfo i : activities){
            try {
                StatisticsListModel model = new StatisticsListModel();
                String packageName = i.activityInfo.packageName;
                String appName = i.activityInfo.loadLabel(packageManager).toString();

                Uri icon = null;
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                if(appInfo.icon != 0)
                    icon = Uri.parse("android.resource://" + packageName + "/" + appInfo.icon);

                model.setAppName(appName);
                model.setPackageName(packageName);
                model.setAppIcon(icon.toString());

                appsList.add(model);
                packageNames.add(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < usageStatsList.size(); i++) {
            String packageName = usageStatsList.get(i).getPackageName();

            if(packageNames.contains(packageName)){
                int index = packageNames.indexOf(packageName);
                appsList.get(index).setTotalTime(usageStatsList.get(i).getTotalTimeInForeground());
                appsList.get(index).setLastAccessed(usageStatsList.get(i).getLastTimeUsed());
                adapter.add(appsList.get(index));
            }

        }
    }

    public List<UsageStats> getUsageStatistics(int intervalType) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i("ada", "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    "Permission is needed",
                    Toast.LENGTH_LONG).show();
        }
        return queryUsageStats;
    }
}
