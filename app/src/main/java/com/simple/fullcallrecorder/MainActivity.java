package com.simple.fullcallrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.simple.fullcallrecorder.adapters.CallAdapter;
import com.simple.fullcallrecorder.callbacks.DeleteAllItems;
import com.simple.fullcallrecorder.callbacks.DeleteItem;
import com.simple.fullcallrecorder.data.CallViewModel;
import com.simple.fullcallrecorder.databinding.ActivityMainBinding;
import com.simple.fullcallrecorder.models.CallInfoModel;
import com.simple.fullcallrecorder.ui.PlayRecordActivity;
import com.simple.fullcallrecorder.ui.SettingsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String currentPackageName = "com.fingersoft.hillclimb";
    private static final String marketUrl = "https://play.google.com/store/apps/details?id=";
    private CallAdapter callAdapter;
    private CallViewModel callViewModel;
    private ActivityMainBinding binding;
    private String pathStr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Extras.checkTheme(this);
        setContentView(binding.getRoot());

        callViewModel = new ViewModelProvider(this).get(CallViewModel.class);
        setSupportActionBar(binding.toolbar);
        initRecycler();

        callViewModel.filterRecordings();
        callViewModel.arrayListMutableLiveData.observe(this, new Observer<ArrayList<CallInfoModel>>() {
            @Override
            public void onChanged(ArrayList<CallInfoModel> callInfoModelArrayList) {
                binding.noRecordingsTxt.setVisibility(View.GONE);
                callAdapter = new CallAdapter(callInfoModelArrayList);
                binding.recyclerView.setAdapter(callAdapter);
                callAdapter.itemClick(new CallAdapter.onCallListener() {
                    @Override
                    public void onDeleteRecord(String path,int position) {
                        boolean isDeleted = callViewModel.isFileRemoved(path);
                        Extras.deleteRecordDialog(MainActivity.this, new DeleteItem() {
                            @Override
                            public void call() {
                                if(isDeleted){
                                    callViewModel.filterRecordings();
                                    callAdapter.removeItem(position);
                                    Toast.makeText(MainActivity.this,"File Successfully Deleted",Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this,"File was not deleted",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onPlayRecord(String path) {
                        Intent intent = new Intent(MainActivity.this, PlayRecordActivity.class);
                        intent.putExtra("path",path);
                        startActivity(intent);
                    }
                    @Override
                    public void onShareRecord(String path) {
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        File file = new File(path);
//                        Uri uri = FileProvider.getUriForFile(MainActivity.this,getPackageName(),file);
//                        intent.setType("audio/*");
//                        intent.putExtra(Intent.EXTRA_STREAM,uri);
//                        startActivity(Intent.createChooser(intent,"Share Via :"));

                        // second method
                        File file = new File(path);
                        Uri uri = FileProvider.getUriForFile(MainActivity.this,getPackageName(),file);
                        Intent intent1 = new ShareCompat.IntentBuilder(MainActivity.this)
                                .setStream(uri)
                                .getIntent()
                                .setAction(Intent.ACTION_SEND)
                                .setDataAndType(uri,"audio/*")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent1);

                    }
                    @Override
                    public void onItemSelected(String path) {
                        pathStr = path;
                    }
                });
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
                R.string.openDrawer , R.string.closeDrawer);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        binding.nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.settings){
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                }
                else if (item.getItemId() == R.id.rate){
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                    try {
                        marketIntent.setData(Uri.parse("market://details?id" + currentPackageName));
                    }catch (Exception e){
                        marketIntent.setData(Uri.parse(marketUrl + currentPackageName));
                    }
                    startActivity(marketIntent);
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void initRecycler(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.deleteRecords){
            // delete all records
            boolean areAllDeleted = callViewModel.areAllFilesDeleted();
            if(callAdapter != null){
                Extras.deleteAllRecordingsDialog(MainActivity.this, new DeleteAllItems() {
                    @Override
                    public void call() {
                        if(areAllDeleted){
                            callViewModel.filterRecordings();
                            callAdapter.removeAllItems();
                            Toast.makeText(MainActivity.this,"File Successfully Deleted",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this,"File was not deleted",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(MainActivity.this,"There is no file to delete",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (item.getItemId() == R.id.recordInfo){
             if(pathStr.equals("")){
                 Toast.makeText(MainActivity.this,"Please select a position first",Toast.LENGTH_LONG).show();
                 return false;
             }
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(pathStr);
            String bitRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String date = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String location = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
            String str = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);


            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Bite Rate : " + bitRate + "\n" + "Date : " + date + "\n" + "Duration : " + duration + "\n"  +
                            "Title : " + title + "\n" + "Location : " + location + "\n" + "Genre " + str)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onDestroy();
        }

    }


}