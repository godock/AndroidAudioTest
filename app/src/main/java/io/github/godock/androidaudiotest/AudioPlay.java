package io.github.godock.androidaudiotest;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import java.util.concurrent.atomic.AtomicBoolean;


public class AudioPlay {

    private static final String TAG = "AudioPlay";

    private static final int mBytesPerSample = 2;   // short
    private static final int mFrameDuration = 20;   // in ms

    private AudioTrack mAudioTracker;
    private int mFrameBufferSize;
    private Thread mAudioTrackingThread;
    private AtomicBoolean mIsAudioTracking = new AtomicBoolean(false);

    public AudioPlay(int streamType, int sampleRate, int numChannels) {
        Log.i(TAG, "construct: streamType=" + streamType
                + ", sampleRate=" + sampleRate+ ", numChannels=" + numChannels);

        int channelConfig = numChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

        int minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT);

        if (minBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.w(TAG, "construct: getMinBufferSize failed");
            return;
        } else {
            Log.i(TAG, "construct: minBufferSize=" + minBufferSize);
        }

        mFrameBufferSize = sampleRate * numChannels * mBytesPerSample * mFrameDuration / 1000;

        try {
            mAudioTracker = new AudioTrack(
                    streamType,
                    sampleRate,
                    channelConfig,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize,
                    AudioTrack.MODE_STREAM);

            Log.i(TAG, "construct: new AudioTrack successful, minBufferSize = " + minBufferSize);
        } catch (Exception e) {
            Log.w(TAG, "construct: new AudioTrack failed, error: " + e.getMessage());
        }
    }

    public void startPlay(String fileName) {
        if (mAudioTracker == null) {
            Log.w(TAG, "startPlay: audioTracker is null");
            return;
        }

        if (!mIsAudioTracking.compareAndSet(false, true)) {
            Log.w(TAG, "startPlay: isAudioTracking value is not expected");
            return;
        }

        NativeManager.startTracking(fileName, mAudioTracker.getSampleRate(), mAudioTracker.getChannelCount());

        mAudioTrackingThread = new Thread(new Runnable() {
            public void run() {
                Log.i(TAG, "startPlay: tracking thread enter, id: " + mAudioTrackingThread.getId());
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                try {
                    mAudioTracker.play();
                } catch (Exception e) {
                    Log.w(TAG, "startPlay: audioTracker play failed, message is " + e.getMessage());
                    return;
                }

                byte[] frameBuffer = new byte[mFrameBufferSize];

                while (mIsAudioTracking.get()) {
                    NativeManager.getData(frameBuffer, mFrameBufferSize);
                    int result = mAudioTracker.write(frameBuffer, 0, mFrameBufferSize);
                    if (result != mFrameBufferSize) {
                        Log.i(TAG, "startPlay: Tracker Thread error with result=" + result);
                        SystemClock.sleep(5);
                    }
                }

                try {
                    mAudioTracker.stop();
                } catch (Exception e) {
                    Log.w(TAG, "startPlay: audioTracker stop failed, message is " + e.getMessage());
                }

                Log.i(TAG, "startPlay: audio tracking thread exit");
            }
        }, "Audio Tracker");

        mAudioTrackingThread.start();
    }

    public void stopPlay() {
        Log.i(TAG, "stopPlay: stop play");

        if (mAudioTracker == null) {
            Log.i(TAG, "stopPlay: stop play failed, audioTracker is null");
            return;
        }

        mIsAudioTracking.set(false);

        if (mAudioTrackingThread != null) {
            try {
                mAudioTrackingThread.join();
            } catch (InterruptedException e) {
                Log.w(TAG, "stopPlay: audioTrackingThread join failed, message is " + e.getMessage());
            } finally {
                mAudioTrackingThread = null;
            }
        }

        if (mAudioTracker != null) {
            try {
                mAudioTracker.release();
            } catch (Exception e) {
                Log.w(TAG, "stopPlay: audioTracker release failed, message is " + e.getMessage());
            } finally {
                mAudioTracker = null;
            }
        }

        NativeManager.stopTracking();
    }
}
