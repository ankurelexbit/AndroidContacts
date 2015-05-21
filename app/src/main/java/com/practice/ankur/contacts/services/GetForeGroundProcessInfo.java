package com.practice.ankur.contacts.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.practice.ankur.contacts.R;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GetForeGroundProcessInfo extends Service {

    private static ActivityManager activityManager;
    private static PackageManager packageManager;
    private static String lastActiveApp = "";
    private static final String logger = GetForeGroundProcessInfo.class.getName();

    public GetForeGroundProcessInfo() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String foregroundApp = getForegroundAppInfo();
                if (foregroundApp != null && isNewApp(foregroundApp))
                    sendProcessNotification(foregroundApp);
            }
        }, 0, 10, TimeUnit.SECONDS);
        return START_STICKY;
    }

    private String getForegroundAppInfo(){
        ActivityManager.RunningAppProcessInfo runningApp = null;
        ActivityManager.RunningAppProcessInfo thisApp = null;
        String appName = null;

        if (activityManager == null)
            activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = appList.iterator();

        while (iterator.hasNext()){
            thisApp = iterator.next();
            Log.d(logger, "Got process : " + thisApp.processName);
            if (thisApp.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                runningApp = thisApp;
                break;
            }
        }
        int pid = runningApp.pid;
        Log.d(logger, "Got Pid : " + pid);
        Log.d(logger, "Got Processname : " + runningApp.processName);

        if (packageManager == null)
            packageManager = this.getPackageManager();
        try {
            appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(runningApp.processName, PackageManager.GET_META_DATA)).toString();
        }catch (PackageManager.NameNotFoundException e){
            Log.e(logger, "Unable to get package name for process : " + runningApp.processName + " Pid : " + pid);
        }
        Log.d(logger, "Got appName : " + appName);
        return appName;
    }

//    private boolean isRunningService(String processName) {
//        if (processName == null || processName.isEmpty())
//            return false;
//
//        ActivityManager.RunningServiceInfo service;
//
//        if(activityManager==null)
//            activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        List <ActivityManager.RunningServiceInfo> l = activityManager.getRunningServices(9999);
//        Iterator <ActivityManager.RunningServiceInfo> i = l.iterator();
//        while(i.hasNext()){
//            service = i.next();
//            if(service.process.equals(processName))
//                return true;
//        }
//
//        return false;
//    }

    private void sendProcessNotification(String process){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(process + " has been started")
                .setContentTitle("Update");
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private boolean isNewApp(String appName){
        Log.d(logger, "last active App : " + lastActiveApp);
        Log.d(logger, "New App : " + appName);
        if (lastActiveApp.equalsIgnoreCase(appName)){
            return false;
        }else {
            lastActiveApp = appName;
            return true;
        }
    }
}
