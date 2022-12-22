package com.simple.fullcallrecorder.receiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.User;
import com.simple.fullcallrecorder.Extras;
import com.simple.fullcallrecorder.MainActivity;
import com.simple.fullcallrecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CallReceiver extends BroadcastReceiver {
    private MediaRecorder mediaRecorder;
    private AudioRecord audioRecord;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Extras.shouldRecordCall(context)) {
            return;
        }
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE") && !intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String state = bundle.getString(TelephonyManager.EXTRA_STATE);

        if (Objects.equals(state, TelephonyManager.EXTRA_STATE_RINGING)) {
            // ringing
            Toast.makeText(context, "Ringing", Toast.LENGTH_LONG).show();
        } else if (Objects.equals(state, TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // answered
            Toast.makeText(context, "OFFHOOK", Toast.LENGTH_LONG).show();
            createMediaRecorder(context, bundle);
            turnOnOffSound(context);
        } else if (Objects.equals(state, TelephonyManager.EXTRA_STATE_IDLE)) {
            // idle , call ended
            Toast.makeText(context, "IDLE", Toast.LENGTH_LONG).show();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            stopMediaRecorder(context);
        }
    }

    @SuppressLint("MissingPermission")
    private void createMediaRecorder(Context context, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaRecorder = new MediaRecorder(context);
        } else {
            mediaRecorder = new MediaRecorder();
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Recordings/");
        if (!file.exists()) {
            file.mkdir();
        }
        if (bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) == null) {
            return;
        }

        //-------------
        String filePath = file.getAbsolutePath() + "/" + bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) + ".3gp";
        if(Extras.getCurrentAudioSource(context).equals("microphone")){
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } else if(Extras.getCurrentAudioSource(context).equals("call")){
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        } else if(Extras.getCurrentAudioSource(context).equals("recognition")){
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        } else if(Extras.getCurrentAudioSource(context).equals("communication")){
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        }
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if(Extras.getCurrentFormat(context).equals("3gp")){
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        } else {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        }
        mediaRecorder.setAudioEncodingBitRate(44100);
        if(Extras.getCurrentMode(context).equals("mono")){
            Log.d("TAG","Mono Channel On");
            mediaRecorder.setAudioChannels(1);
        } else {
            Log.d("TAG","Stereo Channel On");
            mediaRecorder.setAudioChannels(2);
        }
        mediaRecorder.setOutputFile(filePath);

        Log.d("TAG", "File Path is : " + filePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            showNotification(context, "Recording Call", "The call is being recorded");
        } catch (Exception e) {
            // recorder exception
            Log.d("Receiver Tag", "Media Recorder Error " + e.getMessage());
        }
    }

    private void stopMediaRecorder(Context context)   {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        showNotification(context, "Call Record Ended", "The call was successfully recorded");
        if (Extras.isGoogleDriveSynced(context)) {
            uploadToGoogleDrive(context);
        }
    }

    private void showNotification(Context cxt, String title, String msg) {
        Intent intent = new Intent(cxt, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(cxt, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(cxt, Extras.NOTIFICATION_ID)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(com.simple.fullcallrecorder.R.drawable.ic_baseline_circle_notifications_24)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat.from(cxt).notify(1, notificationCompat.build());

    }

    private void uploadToGoogleDrive(Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()  {
                File tempFile = new File(Environment.getExternalStorageDirectory().toString() + "/Recordings/");
                String filePath = tempFile + "/100" + ".3gp";
                if(getDriveService(context) != null){
                    Drive drive = getDriveService(context);
                    File directoryFile = new File(filePath);
                    com.google.api.services.drive.model.File googleDriveFile = new com.google.api.services.drive.model.File();
                    googleDriveFile.setName("Recordings");
                    FileContent fileContent = new FileContent("audio/3gp", directoryFile);
                    assert drive != null;
                    try {
                        drive.files()
                                .create(googleDriveFile, fileContent)
                                .execute();
                    } catch (Exception e) {
                        Log.d("TAG","Exception is " + e.getMessage());
                    }
                }}
        });
        thread.start();
    }

    private Drive getDriveService(Context context) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE));
            googleAccountCredential.setSelectedAccount(account.getAccount());

            return new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    googleAccountCredential)
                    .setApplicationName("Full Recording")
                    .build();
        }
        return null;
    }

    private void turnOnOffSound(Context context){
        Log.d("TAG","isSpeakerOn " + Extras.isSpeakerTurnedOn(context));
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(Extras.isSpeakerTurnedOn(context));
    }


}
