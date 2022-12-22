package com.simple.fullcallrecorder.data;

import android.os.Environment;

import java.io.File;

public class CallRepository {


    public File[] getRecordings(){
        File file = new File(Environment.getExternalStorageDirectory().toString()  +  "/Recordings");
        return file.listFiles();
    }
    public boolean isFileDeleted(String path){
      boolean isFileDeleted = false;
      File file = new File(path);
      if(file.exists()){
         isFileDeleted =  file.delete();
      }
      return isFileDeleted;
    }
    public boolean deleteAllFiles(){
        boolean areFilesDeleted = false;
        File file = new File(Environment.getExternalStorageDirectory().toString() +  "/Recordings");
        String[] files = file.list();
        if(file.isDirectory()){
            for(String fi : files){
                areFilesDeleted  = new File(file,fi).delete();
            }
        }
        return areFilesDeleted;
    }
}
