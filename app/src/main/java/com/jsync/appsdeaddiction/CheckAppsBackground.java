package com.jsync.appsdeaddiction;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by jaseem on 5/5/19.
 */

public class CheckAppsBackground extends Service {
    private IBinder binder = new LocalBinder();
    private MySQLHelper mySQLHelper;
    private ArrayList<AppsListModel> appsListDB;
    private ArrayList<String> packageNames;
    private Thread myThread;

    public static final String REMAINING = "remaining";

    public static final String APP_ICON = "icon";

    class LocalBinder extends Binder{
        public CheckAppsBackground getService(){
            return CheckAppsBackground.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, "protectionId")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(getString(R.string.app_name))
                    .setContentIntent(pendingIntent).build();

        startForeground(11, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myThread = new Thread(new CheckThread(), "MyThread");
        myThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class CheckThread implements Runnable{
        @Override
        public void run(){
            mySQLHelper = new MySQLHelper(getApplicationContext());
            packageNames = new ArrayList<>();

            appsListDB = mySQLHelper.getAll();
            for(AppsListModel model: appsListDB){
                packageNames.add(model.getAppPackageName());
            }
            String p;

            while(true){
                Log.i("ada", "Running: ");
                try {
                    p = retrieveAppName();
                    if(packageNames.contains(p)){
                        Log.i("ada", "You are accessing : " + p + " which is locked");

                        String icon = appsListDB.get(packageNames.indexOf(p)).getAppIcon();
                        String from = appsListDB.get(packageNames.indexOf(p)).getFrom();
                        String to = appsListDB.get(packageNames.indexOf(p)).getTo();


                        String[] fromTime = from.split(":");
                        String[] toTime = to.split(":");

                        long fromHour = Integer.parseInt(fromTime[0]);
                        long fromMinute = Integer.parseInt(fromTime[1]);

                        long toHour = Integer.parseInt(toTime[0]);
                        long toMinute = Integer.parseInt(toTime[1]);

                        Calendar calendar = Calendar.getInstance();
                        long currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                        long currentMinute = calendar.get(Calendar.MINUTE);

                        long start = (fromHour * 60) + fromMinute;
                        long current = (currentHour * 60) + currentMinute;
                        long end = (toHour * 60) + toMinute;

                        if(current < start){
                            //System.out.println("Still " + (start - current) + "minutes remaining.");
                            startActivity((start - current), icon);
                        }else if(current >= start && current <= end){
                            //System.out.println("In between");
                            //Do nothing, let the app be opened;
                        }else{
                            long tE = 23 * 60 + 59;
                            long cE = tE - end;
                            long tD = cE + start;
                            //System.out.println("Still " + (tD) + "minutes remaining.");
                            startActivity(tD, icon);
                        }
                    }
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startActivity(long remain, String icon){
        Intent l = new Intent(getApplicationContext(), LockedActivity.class);
        l.putExtra(APP_ICON, icon);
        l.putExtra(REMAINING, remain);
        startActivity(l);
    }

    private String retrieveAppName() {
        if (Build.VERSION.SDK_INT > 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            return currentApp;
        }else {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            return mm;
        }
    }

}
