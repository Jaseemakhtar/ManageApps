package com.jsync.appsdeaddiction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaseem on 30/4/19.
 */

public class TabOne extends Fragment implements AppsListAdapter.RowOnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<AppsListModel> appsList;
    private AppsListAdapter adapter;
    private AlertDialog alertDialog;
    private MySQLHelper mySQLHelper;

    public  TabOne(){
        appsList = new ArrayList<>();
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
        adapter.setRowOnClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        createList();
        mySQLHelper = new MySQLHelper(getContext());
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
                //Drawable icon = i.activityInfo.loadIcon(packageManager);
                Uri icon = null;
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                if(appInfo.icon != 0) {
                    icon = Uri.parse("android.resource://" + packageName + "/" + appInfo.icon);
                }

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
                model.setAppIcon(icon.toString());
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

        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.restrict_app_layout, viewGroup, false);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnUpdate = dialogView.findViewById(R.id.btn_update);
        ImageView btnClose = dialogView.findViewById(R.id.img_close);

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
                }else{
                    Toast.makeText(getContext(), appsList.get(pos).getAppName() + " - Failed to Update", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
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
                mySQLHelper.add(appsList.get(pos));
                Toast.makeText(getContext(), "Locked " + appsList.get(pos).getAppName() + " - Successfully", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

}
