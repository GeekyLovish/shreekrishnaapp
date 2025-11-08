package com.raman.kumar.shrikrishan.apiNetworking;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import android.os.Handler;
import android.os.Looper;

public class ProgressRequestBody extends RequestBody {

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onError();
        void onFinish();
    }

    private final File file;
    private final String contentType;
    private final UploadCallbacks listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ProgressRequestBody(File file, String contentType, UploadCallbacks listener) {
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[2048];
        try (InputStream in = new FileInputStream(file)) {
            long uploaded = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);
                int progress = (int) ((100 * uploaded) / fileLength);
                // Post progress update safely to main thread
                mainHandler.post(() -> listener.onProgressUpdate(progress));
            }
        } catch (Exception e) {
            mainHandler.post(listener::onError);
        }
        mainHandler.post(listener::onFinish);
    }
}


