package com.simple.fullcallrecorder.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simple.fullcallrecorder.R;
import com.simple.fullcallrecorder.models.CallInfoModel;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder>{

    public onCallListener onCallListener;
    public interface  onCallListener {
        void onDeleteRecord(String path,int position);
        void onPlayRecord(String path);
        void onShareRecord(String path);
        void onItemSelected(String path);
    }
    public void itemClick(onCallListener onCallListener){
        this.onCallListener = onCallListener;
    }
    ArrayList<CallInfoModel> callInfoModelArrayList;
    public CallAdapter(ArrayList<CallInfoModel> callInfoModelArrayList){
        this.callInfoModelArrayList = callInfoModelArrayList;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recordings_rows,null);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        CallInfoModel callInfoModel = callInfoModelArrayList.get(position);
        holder.recordTitle.setText(callInfoModel.getRecordTitle().replace(".3gp",""));

        holder.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallListener != null){
                  int pos = holder.getAdapterPosition();
                  if(pos != RecyclerView.NO_POSITION){
                      onCallListener.onPlayRecord(callInfoModel.getRecordPath());
                  }
                }
            }
        });
        holder.deleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallListener != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        onCallListener.onDeleteRecord(callInfoModel.getRecordPath(),holder.getAdapterPosition());
                    }
                }
            }
        });
        holder.shareRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallListener != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        onCallListener.onShareRecord(callInfoModel.getRecordPath());
                    }
                }
            }
        });
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCallListener != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        onCallListener.onItemSelected(callInfoModel.getRecordPath());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return callInfoModelArrayList.size();
    }

    public static class CallViewHolder extends  RecyclerView.ViewHolder{
        private ImageView record;
        private TextView recordTitle;
        private ImageView deleteRecord;
        private ImageView shareRecord;
        private LinearLayout row;
        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            record = itemView.findViewById(R.id.record);
            recordTitle = itemView.findViewById(R.id.recordTitle);
            deleteRecord  = itemView.findViewById(R.id.delete);
            shareRecord = itemView.findViewById(R.id.share);
            row = itemView.findViewById(R.id.row);
        }
    }

    public void removeItem(int position){
        callInfoModelArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAllItems(){
        callInfoModelArrayList.clear();
        notifyDataSetChanged();
    }
}
