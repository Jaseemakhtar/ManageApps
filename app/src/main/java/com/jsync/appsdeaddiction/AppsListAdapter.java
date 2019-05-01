package com.jsync.appsdeaddiction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jaseem on 1/5/19.
 */

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.AppsListHolder> {

    private ArrayList<AppsListModel> appsList;

    public AppsListAdapter(){
        appsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AppsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_row, parent, false);
        return new AppsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsListHolder holder, int position) {
        holder.appName.setText(appsList.get(position).getAppName());
        holder.appPackage.setText(appsList.get(position).getAppPackageName());
        holder.appIcon.setImageDrawable(appsList.get(position).getAppIcon());
    }

    public void add(AppsListModel model){
        appsList.add(model);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    static class AppsListHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appPackage;

        public AppsListHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.apps_icon);
            appName = itemView.findViewById(R.id.apps_name);
            appPackage = itemView.findViewById(R.id.app_package_name);
        }
    }
}
