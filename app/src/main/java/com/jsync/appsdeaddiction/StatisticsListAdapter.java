package com.jsync.appsdeaddiction;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jaseem on 30/5/19.
 */

public class StatisticsListAdapter extends RecyclerView.Adapter<StatisticsListAdapter.ViewHolder>{
    private DateFormat mDateFormat = new SimpleDateFormat();
    private ArrayList<StatisticsListModel> appsList;

    public StatisticsListAdapter(){
        appsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_usage_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uri = appsList.get(position).getAppIcon();
        if(uri != null)
            holder.appIcon.setImageURI(Uri.parse(uri));
        holder.txtAppName.setText(appsList.get(position).getAppName());
        holder.txtLastAccess.setText("Last Accessed: " + mDateFormat.format(new Date(appsList.get(position).getLastAccessed())));
        long seconds = (appsList.get(position).getTotalTime() / 1000);
        int minutes = (int) (seconds / 60);
        if(minutes <= 0)
            holder.txtUsage.setText(seconds + "secs");
        else
            holder.txtUsage.setText(minutes + "mins");
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public void add(StatisticsListModel model){
        appsList.add(model);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView txtAppName;
        TextView txtLastAccess;
        TextView txtUsage;

        public ViewHolder(View view) {
            super(view);
            appIcon = view.findViewById(R.id.app_icon);
            txtAppName = view.findViewById(R.id.app_name);
            txtLastAccess = view.findViewById(R.id.app_last_access);
            txtUsage = view.findViewById(R.id.app_day_consumed);
        }
    }
}
