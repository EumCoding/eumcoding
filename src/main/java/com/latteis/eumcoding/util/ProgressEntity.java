package com.latteis.eumcoding.util;

public class ProgressEntity {
    private long bytesRead = 0;
    private long contentLength = 0;

    //Getter, Setter
    public long getBytesRead() {
        return bytesRead;
    }
    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }
    public long getContentLength() {
        return contentLength;
    }
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public int getProgress() {
        return (int) (bytesRead * 100.0 / contentLength);
    }
}