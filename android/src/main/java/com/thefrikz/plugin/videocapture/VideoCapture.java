package com.example.plugin;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

@NativePlugin(
    permissions = {
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
)
public class VideoCapture extends Plugin {
    private MediaRecorder mediaRecorder;
    private String videoFilePath;

    @PluginMethod
    public void startRecording(PluginCall call) {
        if (hasRequiredPermissions()) {
            int duration = call.getInt("duration", 30);
            String quality = call.getString("quality", "high");

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setOutputFile(getOutputFile().toString());
            mediaRecorder.setVideoSize(1280, 720);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setMaxDuration(duration * 1000);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                call.error("Error al iniciar la grabación: " + e.getMessage());
                return;
            }

            getBridge().getActivity().runOnUiThread(() -> {
                getBridge().getActivity().getWindow().getDecorView().postDelayed(() -> {
                    stopRecording(call);
                }, duration * 1000);
            });

            call.success();
        } else {
            pluginRequestAllPermissions();
        }
    }

    @PluginMethod
    public void stopRecording(PluginCall call) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        call.success();
    }

    private File getOutputFile() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Videos");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        videoFilePath = dir.getAbsolutePath() + "/video.mp4";
        return new File(videoFilePath);
    }

    private boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
