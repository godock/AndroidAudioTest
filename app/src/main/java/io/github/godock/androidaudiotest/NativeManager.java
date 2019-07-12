package io.github.godock.androidaudiotest;

public class NativeManager {
    static {
        System.loadLibrary("native-lib");
    }


    private NativeManager() {
    }

    public static native boolean startRecording(String file, int sampleRate, int numChannels);
    public static native boolean stopRecording();
    public static native int putData(byte[] buffer, int bytes);

    public static native boolean startTracking(String file, int sampleRate, int numChannels);
    public static native boolean stopTracking();
    public static native int getData(byte[] buffer, int bytes);
}

