package com.simple.fullcallrecorder.ui;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.content.Context;
import android.content.Intent;
import android.hardware.lights.LightState;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.google.common.collect.ObjectArrays;
import com.simple.fullcallrecorder.Extras;
import com.simple.fullcallrecorder.R;
import com.simple.fullcallrecorder.databinding.ActivitySettingsBinding;

import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private  GoogleSignInClient GoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.preferences,rootKey);


            SwitchPreference recordCall = findPreference("record_call");
            SwitchPreference speakerOn = findPreference("speaker");
            SwitchPreference googleDrive = findPreference("google_drive");
            ListPreference themePreference = findPreference("theme");
            ListPreference modePreference = findPreference("recording_mode");
            ListPreference formatPreference = findPreference("recording_format");
            ListPreference audioPreference = findPreference("audio_source");
            Objects.requireNonNull(recordCall).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    boolean isChecked = (boolean) newValue;
                    if(isChecked){
                        Extras.setPrefs(true,"record_call",requireContext());
                    } else {
                        Extras.setPrefs(false,"record_call",requireContext());
                    }
                    return true;
                }
            });
            Objects.requireNonNull(speakerOn).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    boolean isChecked = (boolean) newValue;
                    if(isChecked){
                        Extras.setPrefs(true,"speaker_on",requireContext());
                    } else {
                        Extras.setPrefs(false,"speaker_on",requireContext());
                    }
                    return true;
                }
            });
            Objects.requireNonNull(googleDrive).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    boolean isChecked = (boolean) newValue;
                    if(isChecked){
                        googleSignIn();
                    } else {
                        signOutUser();
                    }
                    return true;
                }
            });
            Objects.requireNonNull(themePreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    if(preference instanceof ListPreference){
                        for(CharSequence val : ((ListPreference) preference).getEntryValues()){
                            if(val.toString().equals("Light")){
                                Extras.setThemePrefs(val.toString(),"theme",requireContext());
                                themePreference.setSummary("Light Mode");
                                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                            } else {
                                Extras.setThemePrefs(val.toString(),"theme",requireContext());
                                themePreference.setSummary("Dark Mode");
                                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                            }
                        }
                    }
                    return true;
                }
            });
            Objects.requireNonNull(modePreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    if(preference instanceof ListPreference){
                        ListPreference listPreference = (ListPreference) preference;
                        for(CharSequence value : listPreference.getEntryValues()){
                            Extras.setRecordingMode(value.toString(),"mode",requireContext());
                        }
                    }
                    return true;
                }
            });
            Objects.requireNonNull(formatPreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    if(preference instanceof ListPreference){
                        ListPreference listPreference = (ListPreference) preference;
                        for(CharSequence value : listPreference.getEntryValues()){
                            Extras.setRecordFormat(value.toString(),"format",requireContext());
                        }
                    }
                    return true;
                }
            });
            Objects.requireNonNull(audioPreference).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    if(preference instanceof ListPreference){
                        ListPreference listPreference = (ListPreference) preference;
                        for(CharSequence val  : listPreference.getEntryValues()){
                            Extras.setAudioSource(val.toString(),"audio_source",requireContext());
                        }
                    }
                    return true;
                }
            });

            /// SET SUMMARIES
            if(Extras.getCurrentAudioSource(requireContext()).equals("microphone")){
                audioPreference.setSummary("Microphone");
            } else if(Extras.getCurrentAudioSource(requireContext()).equals("call")){
                audioPreference.setSummary("Voice Call");
            } else if(Extras.getCurrentAudioSource(requireContext()).equals("recognition")){
                audioPreference.setSummary("Voice Recognition");
            } else if(Extras.getCurrentAudioSource(requireContext()).equals("communication")){
                audioPreference.setSummary("Voice Communication");
            }

            if(Extras.getCurrentFormat(requireContext()).equals("3gp")){
                formatPreference.setSummary("3GP");
            } else {
                formatPreference.setSummary("AAC");
            }

            if(Extras.getCurrentMode(requireContext()).equals("mono")){
                modePreference.setSummary("Mono");
            } else {
                modePreference.setSummary("Stereo");
            }
        }

        private  void googleSignIn(){
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(DriveScopes.DRIVE))
                    .requestEmail()
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(),options);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        }
        private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            handleSignIn(result.getData());
                        }
                    }
                });

        private void handleSignIn(Intent data) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if(task.isSuccessful()){
                                Extras.setPrefs(true,"google_drive",requireContext());
                            }
                        }
                    });
        }
        private void signOutUser(){
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(DriveScopes.DRIVE))
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(),options);
            googleSignInClient.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Extras.setPrefs(false,"google_drive",requireContext());
                            }
                        }
                    });
        }
    }

}