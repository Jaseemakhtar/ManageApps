package com.jsync.appsdeaddiction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
    private RowOnClickListener rowOnClickListener;
    private Context context;

    public AppsListAdapter(Context context){
        appsList = new ArrayList<>();
        this.context = context;
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
        //holder.appIcon.setImageDrawable(appsList.get(position).getAppIcon());
        holder.appIcon.setImageURI(Uri.parse(appsList.get(position).getAppIcon()));
        Drawable lockIcon;
        if(appsList.get(position).getRowId() > -1){
            lockIcon = context.getDrawable(R.drawable.ic_lock_closed);
        }else{
            lockIcon = context.getDrawable(R.drawable.ic_lock_open);
        }
        holder.imgLocked.setImageDrawable(lockIcon);
    }

    public void add(AppsListModel model){
        appsList.add(model);
        notifyDataSetChanged();
    }

    public void setRowOnClickListener(RowOnClickListener rowOnClickListener){
        this.rowOnClickListener = rowOnClickListener;
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    class AppsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        ImageView imgLocked;

        public AppsListHolder(View view) {
            super(view);
            appIcon = view.findViewById(R.id.apps_icon);
            appName = view.findViewById(R.id.apps_name);
            appPackage = view.findViewById(R.id.app_package_name);
            imgLocked = view.findViewById(R.id.img_locked_state);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(rowOnClickListener !=  null){
                rowOnClickListener.onClickRow(getAdapterPosition());
            }
        }
    }

    interface RowOnClickListener{
        void onClickRow(int pos);
    }
}
