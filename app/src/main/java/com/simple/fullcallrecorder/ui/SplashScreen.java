package com.simple.fullcallrecorder.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.simple.fullcallrecorder.Extras;
import com.simple.fullcallrecorder.MainActivity;
import com.simple.fullcallrecorder.databinding.ActivitySplashScreenBinding;

import javax.net.ssl.ManagerFactoryParameters;

public class SplashScreen extends AppCompatActivity {

    private static final long ELAPSED_TIME = 5000;
    private ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        Extras.checkTheme(this);
        setContentView(binding.getRoot());

        Sprite doubleBounce = new DoubleBounce();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);


        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                binding.spinKit.setVisibility(View.GONE);
                if(arePermissionsGranted() && isAppNotOptimized()){
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else if (arePermissionsGranted() && !isAppNotOptimized()){
                    Intent mainIntent = new Intent(SplashScreen.this, AccessibilityActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    Intent permissionIntent = new Intent(SplashScreen.this,PermissionActivity.class);
                    startActivity(permissionIntent);
                    finish();
                }
            }
        },ELAPSED_TIME);
    }

    private boolean arePermissionsGranted(){
        return  ContextCompat.checkSelfPermission(this, Extras.getPermissions()[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Extras.getPermissions()[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Extras.getPermissions()[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Extras.getPermissions()[3]) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAppNotOptimized(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(getPackageName());
     }
}