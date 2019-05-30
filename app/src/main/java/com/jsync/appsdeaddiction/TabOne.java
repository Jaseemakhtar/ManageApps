package com.jsync.appsdeaddiction;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaseem on 30/4/19.
 */

public class TabOne extends Fragment implements AppsListAdapter.RowOnClickListener{

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<AppsListModel> appsList;
    private ArrayList<AppsListModel> appsListDB;
    private AppsListAdapter adapter;
    private AlertDialog alertDialog;
    private MySQLHelper mySQLHelper;
    private int hourFrom, minutesFrom, hourTo, minutesTo;
    private String timeFrom, timeTo;
    private SwipeRefreshLayout swipeRefreshLayout;

    public  TabOne(){
        appsList = new ArrayList<>();
        appsListDB = new ArrayList<>();
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
        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh_all_apps);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new AppsListAdapter(getContext());
        adapter.setRowOnClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        mySQLHelper = new MySQLHelper(getContext());
        createList();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void createList(){
        adapter.clear();
        appsList.clear();
        appsListDB.clear();
        appsListDB = mySQLHelper.getAll();

        PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);

        for(ResolveInfo i : activities){
            try {
                AppsListModel model = new AppsListModel();
                String packageName = i.activityInfo.packageName;

                if(packageName.equals(getActivity().getPackageName()))
                    continue;

                String appName = i.activityInfo.loadLabel(packageManager).toString();
                String versionName = packageManager.getPackageInfo(i.activityInfo.packageName, 0).versionName;
                long installedOn = packageManager.getPackageInfo(i.activityInfo.packageName, 0).firstInstallTime;
                long updatedOn = packageManager.getPackageInfo(i.activityInfo.packageName, 0).lastUpdateTime;

                Uri icon = null;
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                if(appInfo.icon != 0)
                    icon = Uri.parse("android.resource://" + packageName + "/" + appInfo.icon);


                model.setAppName(appName);
                model.setAppPackageName(packageName);
                model.setVersionName(versionName);
                model.setInstalledOn(installedOn);
                model.setUpdatedOn(updatedOn);
                model.setAppIcon(icon.toString());

                for(AppsListModel modelDb: appsListDB){
                    if(modelDb.getAppPackageName().equals(packageName)){
                        model.setRowId(modelDb.getRowId());
                        model.setFrom(modelDb.getFrom());
                        model.setTo(modelDb.getTo());
                    }
                }

                appsList.add(model);
                adapter.add(model);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClickRow(int pos) {
        showSaveDialog(pos);
    }

    private void showSaveDialog(final int pos){
        final boolean[] selected = new boolean[2];

        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.restrict_app_layout, viewGroup, false);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnUpdate = dialogView.findViewById(R.id.btn_update);
        ImageView btnClose = dialogView.findViewById(R.id.img_close);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        final TextView btnFrom = dialogView.findViewById(R.id.btn_from);
        final TextView btnTo = dialogView.findViewById(R.id.btn_to);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mySQLHelper.update(appsList.get(pos)) > 0){
                    Toast.makeText(getContext(), appsList.get(pos).getAppName() + " - Update Successfully", Toast.LENGTH_LONG).show();
                    appsListDB.clear();
                    appsList.clear();
                    adapter.clear();
                    alertDialog.dismiss();
                    createList();
                }else{
                    Toast.makeText(getContext(), appsList.get(pos).getAppName() + " - Failed to Update", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = true;
                for(boolean s: selected){
                    if(!s){
                        saved = false;
                        break;
                    }
                }

                if(saved){
                    mySQLHelper.add(appsList.get(pos));
                    Toast.makeText(getContext(), appsList.get(pos).getAppName() +  ", Blocked - Successfully", Toast.LENGTH_LONG).show();

                    appsListDB.clear();
                    appsList.clear();
                    adapter.clear();
                    alertDialog.dismiss();
                    createList();

                }else{
                    Toast.makeText(getContext(), "You have to select time", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        timeFrom = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hourFrom = timePicker.getHour();
                            minutesFrom = timePicker.getMinute();
                        }else{
                            hourFrom = timePicker.getCurrentHour();
                            minutesFrom = timePicker.getCurrentMinute();
                        }

                        if(hourFrom < 9){
                            timeFrom = "0" + hourFrom + ":";
                        }else if(hourFrom <= 0){
                            timeFrom = "00:";
                        }else{
                            timeFrom = hourFrom + ":";
                        }

                        if(minutesFrom < 9){
                            timeFrom += "0" + minutesFrom;
                        }else if(minutesFrom <= 0){
                            timeFrom += "00";
                        }else{
                            timeFrom += minutesFrom;
                        }

                        btnFrom.setText(timeFrom);
                        appsList.get(pos).setFrom(timeFrom);
                        selected[0] = true;
                    }
                }, 21, 07, true);
                timePickerDialog.show();
            }
        });

        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        timeTo = "";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hourTo = timePicker.getHour();
                            minutesTo = timePicker.getMinute();
                        }else{
                            hourTo = timePicker.getCurrentHour();
                            minutesTo = timePicker.getCurrentMinute();
                        }

                        if(hourTo < 9){
                            timeTo = "0" + hourTo + ":";
                        }else if(hourTo <= 0){
                            timeTo = "00:";
                        }else{
                            timeTo = hourTo + ":";
                        }

                        if(minutesTo < 9){
                            timeTo += "0" + minutesTo;
                        }else if(minutesTo <= 0){
                            timeTo += "00";
                        }else{
                            timeTo += minutesTo;
                        }

                        btnTo.setText(timeTo);
                        appsList.get(pos).setTo(timeTo);
                        selected[1] = true;
                    }
                }, 22, 40, true);
                timePickerDialog.show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mySQLHelper.delete(appsList.get(pos).getRowId()) > 0){
                    Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                    appsListDB.clear();
                    appsList.clear();
                    adapter.clear();
                    alertDialog.dismiss();
                    createList();
                }else{
                    Toast.makeText(getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(appsList.get(pos).getRowId() == -1){
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }else{
            btnFrom.setText(appsList.get(pos).getFrom());
            btnTo.setText(appsList.get(pos).getTo());
            btnSave.setEnabled(false);
        }
    }

}
