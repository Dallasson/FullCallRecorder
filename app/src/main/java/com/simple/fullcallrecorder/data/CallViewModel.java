package com.simple.fullcallrecorder.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.simple.fullcallrecorder.models.CallInfoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class CallViewModel extends AndroidViewModel {
    private final CallRepository callRepository = new CallRepository();
    public MutableLiveData<ArrayList<CallInfoModel>> arrayListMutableLiveData;
    private final ArrayList<CallInfoModel> callInfoModelArrayList;
    public CallViewModel(@NonNull Application application) {
        super(application);
        callInfoModelArrayList = new ArrayList<CallInfoModel>();
        arrayListMutableLiveData = new MutableLiveData<>();
    }

     public void filterRecordings(){
        try{
            callInfoModelArrayList.clear();
            File[] files = callRepository.getRecordings();
            for(File file : Objects.requireNonNull(files)){
                if(file.getName().endsWith(".3gp")){
                    callInfoModelArrayList.add(new CallInfoModel(file.getName(),file.getPath(),file.getFreeSpace(),file.lastModified()));
                }
                arrayListMutableLiveData.postValue(callInfoModelArrayList);
            }
        }catch (Exception e){
            // exception
        }
    }
     public boolean isFileRemoved(String path){
        return callRepository.isFileDeleted(path);
     }
     public boolean areAllFilesDeleted(){
        return callRepository.deleteAllFiles();
     }
}
