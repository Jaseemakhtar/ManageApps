package com.jsync.appsdeaddiction;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaseem on 30/4/19.
 */

public class TabTwo extends Fragment implements AppsListAdapter.RowOnClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<AppsListModel> appsListDB;
    private AppsListAdapter adapter;
    private AlertDialog alertDialog;
    private MySQLHelper mySQLHelper;
    int hourFrom, minutesFrom, hourTo, minutesTo;
    private String timeFrom, timeTo;

    public TabTwo(){
        appsListDB = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_two, container, false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.restricted_apps_recycle_view);
        adapter = new AppsListAdapter(getContext());
        adapter.setRowOnClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        mySQLHelper = new MySQLHelper(getContext());
        createList();
        return view;
    }

    @Override
    public void onClickRow(int pos) {
        showSaveDialog(pos);
    }

    private void createList(){
        appsListDB = mySQLHelper.getAll();
        for(AppsListModel modelDb: appsListDB){
            AppsListModel model = new AppsListModel();
            model.setAppName(modelDb.getAppName());
            model.setAppPackageName(modelDb.getAppPackageName());
            model.setAppIcon(modelDb.getAppIcon());
            model.setRowId(modelDb.getRowId());
            model.setFrom(modelDb.getFrom());
            model.setTo(modelDb.getTo());
            adapter.add(model);
        }
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
                if(mySQLHelper.update(appsListDB.get(pos)) > 0){
                    Toast.makeText(getContext(), appsListDB.get(pos).getAppName() + " - Update Successfully", Toast.LENGTH_LONG).show();
                    appsListDB.clear();
                    adapter.clear();
                    alertDialog.dismiss();
                    createList();
                }else{
                    Toast.makeText(getContext(), appsListDB.get(pos).getAppName() + " - Failed to Update", Toast.LENGTH_LONG).show();
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
                    mySQLHelper.add(appsListDB.get(pos));
                    Toast.makeText(getContext(), appsListDB.get(pos).getAppName() +  ", Blocked - Successfully", Toast.LENGTH_LONG).show();

                    appsListDB.clear();
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
                        appsListDB.get(pos).setFrom(timeFrom);
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
                        appsListDB.get(pos).setTo(timeTo);
                        selected[1] = true;
                    }
                }, 22, 40, true);
                timePickerDialog.show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mySQLHelper.delete(appsListDB.get(pos).getRowId()) > 0){
                    Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();

                    appsListDB.clear();
                    adapter.clear();
                    alertDialog.dismiss();
                    createList();
                }else{
                    Toast.makeText(getContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnFrom.setText(appsListDB.get(pos).getFrom());
        btnTo.setText(appsListDB.get(pos).getTo());
        btnSave.setEnabled(false);
        btnSave.setVisibility(View.GONE);
    }
}
