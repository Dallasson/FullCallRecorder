package com.simple.fullcallrecorder.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.simple.fullcallrecorder.Extras;
import com.simple.fullcallrecorder.MainActivity;
import com.simple.fullcallrecorder.databinding.ActivityPermissionBinding;

import java.util.Map;

public class PermissionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.simple.fullcallrecorder.databinding.ActivityPermissionBinding binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        Extras.checkTheme(this);
        setContentView(binding.getRoot());


        binding.grantPermissions.setOnClickListener(v -> {
            // launch request logic
            launcher.launch(Extras.getPermissions());
        });

    }

    private final ActivityResultLauncher<String[]> launcher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean isReadExternalGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                    boolean isWriteExternalGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                    boolean isCallPhoneGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_PHONE_STATE));
                    boolean isRecordAudioGranted = Boolean.TRUE.equals(result.get(Manifest.permission.RECORD_AUDIO));
                    boolean isReadContactsGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_CONTACTS));
                    boolean isReadCallLogGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_CALL_LOG));

                    if(isReadExternalGranted && isWriteExternalGranted && isCallPhoneGranted &&
                            isRecordAudioGranted && isReadContactsGranted && isReadCallLogGranted){
                        // permissions are granted
                       if(Extras.isAppNotOptimized(PermissionActivity.this)){
                           Intent intent = new Intent(PermissionActivity.this,MainActivity.class);
                           startActivity(intent);
                           finish();
                       } else {
                           Intent intent = new Intent(PermissionActivity.this, AccessibilityActivity.class);
                           startActivity(intent);
                           finish();
                       }
                    } else {
                        launcher.launch(Extras.getPermissions());
                    }
                }
            }
    );
}