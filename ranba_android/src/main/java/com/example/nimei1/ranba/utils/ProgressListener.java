package com.example.nimei1.ranba.utils;

public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);
}
