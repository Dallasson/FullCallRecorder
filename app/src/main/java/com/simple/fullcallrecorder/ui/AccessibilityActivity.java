package com.simple.fullcallrecorder.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.simple.fullcallrecorder.Extras;
import com.simple.fullcallrecorder.MainActivity;
import com.simple.fullcallrecorder.databinding.ActivityAccessibilityBinding;

import java.util.Set;

import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_android_components_ActivityComponent;

public class AccessibilityActivity extends AppCompatActivity {

    private PowerManager pm;
    private ActivityAccessibilityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccessibilityBinding.inflate(getLayoutInflater());
        Extras.checkTheme(this);
        setContentView(binding.getRoot());

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // check for accessibility
        binding.enableAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!isIgnoringBatteryOptimization()){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } else {
                        Intent mainIntent = new Intent(AccessibilityActivity.this,MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimization(){
        return pm.isIgnoringBatteryOptimizations(getPackageName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(AccessibilityActivity.this,"Called",Toast.LENGTH_LONG).show();
    }
}