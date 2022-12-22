package com.simple.fullcallrecorder;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.AbsListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.simple.fullcallrecorder.callbacks.DeleteAllItems;
import com.simple.fullcallrecorder.callbacks.DeleteItem;

import java.util.List;

public class Extras {

    public static final String NOTIFICATION_ID = "ID";
    public static final String NOTIFICATION_STR = "STR";
    public static String[] getPermissions(){
        return new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG
        };
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enableServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for(AccessibilityServiceInfo serviceInfo : enableServices){
            ServiceInfo info = serviceInfo.getResolveInfo().serviceInfo;
            if(info.packageName.equals(context.getPackageName()) && info.name.equals(service.getName())){
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isAppNotOptimized(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return  powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public static void deleteRecordDialog(Activity context, DeleteItem deleteItem){
        new AlertDialog.Builder(context)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this record ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem.call();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
    public static void deleteAllRecordingsDialog(Activity context , DeleteAllItems deleteAllItems){
        new AlertDialog.Builder(context)
                .setTitle("Deleting All Records")
                .setMessage("Are you sure you want to delete all recordings ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllItems.call();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static void setPrefs(boolean value,String key,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static void setThemePrefs(String value,String key,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void setRecordingMode(String value,String key,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("recordingMode",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void setRecordFormat(String value,String key,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("formatPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void setAudioSource(String value,String key,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sourcePrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static boolean shouldRecordCall(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("record_call",false);
    }

    public static boolean isSpeakerTurnedOn(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("speaker_on",false);
    }

    public static boolean isGoogleDriveSynced(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("google_drive",false);
    }

    public static String isDarkModeOn(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        return sharedPreferences.getString("theme","Light");
    }

    public static void checkTheme(Context context){
        if(Extras.isDarkModeOn(context).equals("Light")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static String getCurrentMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("recordingMode",Context.MODE_PRIVATE);
        return sharedPreferences.getString("mode","mono");
    }

    public static String getCurrentFormat(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("formatPrefs",Context.MODE_PRIVATE);
        return sharedPreferences.getString("format","3gp");
    }

    public static String getCurrentAudioSource(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sourcePrefs",Context.MODE_PRIVATE);
        return sharedPreferences.getString("audio_source","call");
    }
}
