package io.github.godock.androidaudiotest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class AudioCapture {

    private static final String TAG = "AudioCapture";

    private static final int mBytesPerSample = 2;   // short
    private static final int mFrameDuration = 20;   // in ms
    private AudioRecord mRecorder;
    private int mFrameBufferSize;
    private Thread mRecordingThread;
    private AtomicBoolean mIsRecording = new AtomicBoolean(false);

    public AudioCapture(int audioSource, int sampleRate, int numChannels){
        Log.i(TAG, "construct: audioSource=" + audioSource
                + ", sampleRate=" + sampleRate+ ", numChannels=" + numChannels);

        int channelConfig = numChannels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;

        int minBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT);

        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.w(TAG, "construct: getMinBufferSize failed");
            return;
        } else {
            Log.i(TAG, "construct: minBufferSize=" + minBufferSize);
        }

        mFrameBufferSize = sampleRate * numChannels * mBytesPerSample * mFrameDuration / 1000;

        int bufferSize = mFrameBufferSize > minBufferSize ? mFrameBufferSize + minBufferSize : minBufferSize * 2;

        try {
            mRecorder = new AudioRecord(
                    audioSource,
                    sampleRate,
                    channelConfig,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.w(TAG, "construct: new AudioRecord failed.");
                mRecorder.release();
                mRecorder = null;
                return;
            }

            Log.i(TAG, "construct: new AudioRecord successful, bufferSize = " + bufferSize);
        } catch (Exception e) {
            Log.w(TAG, "construct: new AudioRecord failed, error: " + e.getMessage());
        }
    }

    public void startCapture(String fileName) {
        if (mRecorder == null) {
            Log.w(TAG, "startCapture failed: recorder is null");
            return;
        }
        Log.i(TAG, "startCapture: state: " + mRecorder.getState()
                + ", sampleRate: " + mRecorder.getSampleRate()
                + ", audioSource: " + mRecorder.getAudioSource());
        NativeManager.startRecording(fileName, mRecorder.getSampleRate(), mRecorder.getChannelCount());

        if (!mIsRecording.compareAndSet(false, true)) {
            Log.w(TAG, "startCapture: isRecording value is not expected");
            return;
        }

        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "startCapture: Recorder Thread Enter, id: " + mRecordingThread.getId());
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                try {
                    mRecorder.startRecording();
                } catch (Exception e) {
                    Log.w(TAG, "startCapture: startRecording failed, message is " + e.getMessage());
                    return;
                }

                Log.i(TAG, "startCapture: recorder is started");

                byte[] frameBuffer = new byte[mFrameBufferSize];

                while (mIsRecording.get()) {
                    int ret = mRecorder.read(frameBuffer, 0, mFrameBufferSize);
                    if ((AudioRecord.ERROR_INVALID_OPERATION != ret) && (AudioRecord.ERROR_BAD_VALUE != ret)) {
                        NativeManager.putData(frameBuffer, mFrameBufferSize);
                    } else {
                        Log.i(TAG, "startCapture: Recording Thread read data error, ret=" + ret);
                        SystemClock.sleep(5);
                    }
                }

                try {
                    mRecorder.stop();
                } catch (Exception e) {
                    Log.w(TAG, "startCapture: stop recoder failed");
                }

                Log.i(TAG, "startCapture: Recorder Thread Exit");
            }
        }, "Audio Recorder");

        mRecordingThread.start();
    }

    public void stopCapture() {
        Log.i(TAG, "stopCapture enter");

        if (mRecorder == null) {
            Log.w(TAG, "stopCapture failed: recorder is null");
            return;
        }

        mIsRecording.set(false);

        if (mRecordingThread != null) {
            try {
                mRecordingThread.join();
            } catch (InterruptedException e) {
                Log.w(TAG, "stopCapture: recordingThread join failed, message is " + e.getMessage());
            } finally {
                mRecordingThread = null;
            }
        }

        if (mRecorder != null) {
            try {
                mRecorder.release();
            } catch (Exception e) {
                Log.w(TAG, "startCapture: release recorder failed, message is " + e.getMessage());
            } finally {
                mRecorder = null;
            }
        }
        NativeManager.stopRecording();

        Log.i(TAG, "stopCapture exit");
    }
}
