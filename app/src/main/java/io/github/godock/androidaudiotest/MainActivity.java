package io.github.godock.androidaudiotest;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private Switch mSwitchAudioRecord = null;
    private LinearLayout mLayoutRecordSettings = null;
    private Spinner mSpinnerRecordAudioSource = null;
    private Spinner mSpinnerRecordSampleRate = null;
    private Spinner mSpinnerRecordChannels = null;
    private EditText mEditTextRecordFileName = null;

    private Switch mSwitchAudioTrack = null;
    private LinearLayout mLayoutTrackSettings = null;
    private Spinner mSpinnerTrackStreamType = null;
    private Spinner mSpinnerTrackSampleRate = null;
    private Spinner mSpinnerTrackChannels = null;
    private EditText mEditTextTrackFileName = null;

    private Button mButtonStart = null;
    private Button mButtonStop = null;

    private AudioCapture mAudioCapture = null;
    private AudioPlay mAudioPlay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitchAudioRecord = findViewById(R.id.switchAudioRecord);
        mLayoutRecordSettings = findViewById(R.id.layoutRecordSettings);

        mSwitchAudioRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLayoutRecordSettings.setVisibility(View.VISIBLE);
                    mButtonStart.setEnabled(true);
                } else {
                    mLayoutRecordSettings.setVisibility(View.GONE);
                    mButtonStart.setEnabled(mSwitchAudioTrack.isChecked());
                }
            }
        });

        mSpinnerRecordAudioSource = findViewById(R.id.spinnerAudioSources);
        mSpinnerRecordAudioSource.setSelection(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mSpinnerRecordSampleRate = findViewById(R.id.spinnerRecordSampleRates);
        mSpinnerRecordChannels = findViewById(R.id.spinnerRecordChannels);
        mEditTextRecordFileName = findViewById(R.id.editTextRecordFileName);

        mSwitchAudioTrack = findViewById(R.id.switchAudioTrack);
        mLayoutTrackSettings = findViewById(R.id.layoutTrackSettings);

        mSwitchAudioTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLayoutTrackSettings.setVisibility(View.VISIBLE );
                    mButtonStart.setEnabled(true);
                } else {
                    mLayoutTrackSettings.setVisibility(View.GONE);
                    mButtonStart.setEnabled(mSwitchAudioRecord.isChecked());
                }
            }
        });

        mSpinnerTrackStreamType = findViewById(R.id.spinnerStreamTypes);
        mSpinnerTrackSampleRate = findViewById(R.id.spinnerTrackSampleRates);
        mSpinnerTrackChannels = findViewById(R.id.spinnerTrackChannels);
        mEditTextTrackFileName = findViewById(R.id.editTextTrackFileName);

        mButtonStart = findViewById(R.id.buttonStart);
        if (mButtonStart != null) {
            mButtonStart.setOnClickListener(btnClick);
        }

        mButtonStop = findViewById(R.id.buttonStop);
        if (mButtonStop != null) {
            mButtonStop.setOnClickListener(btnClick);
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonStart: {
                    if (mSwitchAudioRecord.isChecked()) {
                        String fileName = mEditTextRecordFileName.getText().toString();
                        if (fileName.length() == 0) {
                            fileName = "cap";
                        }
                        fileName = getString(R.string.file_path) + fileName + ".pcm";
                        int audioSource = (int) mSpinnerRecordAudioSource.getSelectedItemId();
                        int sampleRate = Integer.parseInt(mSpinnerRecordSampleRate.getSelectedItem().toString());
                        int numChannels = Integer.parseInt(mSpinnerRecordChannels.getSelectedItem().toString());
                        mAudioCapture = new AudioCapture(audioSource, sampleRate, numChannels);
                        mAudioCapture.startCapture(fileName);
                        mLayoutRecordSettings.setEnabled(false);
                    } else {
                        if (mAudioCapture != null) {
                            mAudioCapture.stopCapture();
                            mAudioCapture = null;
                        }
                    }

                    if (mSwitchAudioTrack.isChecked()) {
                        String fileName = mEditTextTrackFileName.getText().toString();
                        if (fileName.length() == 0) {
                            fileName = "play";
                        }
                        fileName = getString(R.string.file_path) + fileName + ".pcm";
                        int streamType = (int) mSpinnerTrackStreamType.getSelectedItemId();
                        int sampleRate = Integer.parseInt(mSpinnerTrackSampleRate.getSelectedItem().toString());
                        int numChannels = Integer.parseInt(mSpinnerTrackChannels.getSelectedItem().toString());
                        mAudioPlay = new AudioPlay(streamType, sampleRate, numChannels);
                        mAudioPlay.startPlay(fileName);
                        mLayoutTrackSettings.setEnabled(false);
                    } else {
                        if (mAudioPlay != null) {
                            mAudioPlay.stopPlay();
                            mAudioPlay = null;
                        }
                    }
                    mButtonStart.setEnabled(false);
                    mButtonStop.setEnabled(true);
                    mSwitchAudioRecord.setEnabled(false);
                    mSwitchAudioTrack.setEnabled(false);
                    break;
                }
                case R.id.buttonStop: {
                    if (mAudioCapture != null) {
                        mAudioCapture.stopCapture();
                        mAudioCapture = null;
                    }
                    if (mAudioPlay != null) {
                        mAudioPlay.stopPlay();
                        mAudioPlay = null;
                    }

                    mButtonStart.setEnabled(true);
                    mButtonStop.setEnabled(false);

                    mSwitchAudioRecord.setEnabled(true);
                    mSwitchAudioTrack.setEnabled(true);

                    if (mSwitchAudioRecord.isChecked()) {
                        mLayoutRecordSettings.setEnabled(true);
                    }
                    if (mSwitchAudioTrack.isChecked()) {
                        mLayoutTrackSettings.setEnabled(true);
                    }
                    break;
                }
            }
        }
    };
}

