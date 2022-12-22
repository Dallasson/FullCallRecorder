package com.simple.fullcallrecorder.models;

public class CallInfoModel {

    String recordTitle;
    String recordPath;
    long totalSpace;
    long lastModified;

    public CallInfoModel(String recordTitle, String recordPath, long totalSpace, long lastModified) {
        this.recordTitle = recordTitle;
        this.recordPath = recordPath;
        this.totalSpace = totalSpace;
        this.lastModified = lastModified;
    }

    public String getRecordTitle() {
        return recordTitle;
    }

    public void setRecordTitle(String recordTitle) {
        this.recordTitle = recordTitle;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
