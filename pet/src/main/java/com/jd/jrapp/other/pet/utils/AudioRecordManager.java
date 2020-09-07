package com.jd.jrapp.other.pet.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * 录制音频的控制器
 */
public class AudioRecordManager {

    private static final String TAG = "AudioRecordManager";
    private volatile static AudioRecordManager INSTANCE;
    private MediaRecorder mediaRecorder;
    private String audioFileName;
    private RecordStatus recordStatus = RecordStatus.STOP;

    public enum RecordStatus {
        READY,
        START,
        STOP
    }

    private AudioRecordManager() {

    }

    public static AudioRecordManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AudioRecordManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AudioRecordManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(String audioFileName) {
        this.audioFileName = audioFileName;
        recordStatus = RecordStatus.READY;
    }

    public void startRecord() {
        if (recordStatus == RecordStatus.READY && mediaRecorder!=null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFileName);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            recordStatus = RecordStatus.START;
        }
    }

    public void stopRecord() {
        if (recordStatus == RecordStatus.START && mediaRecorder!=null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recordStatus = RecordStatus.STOP;
            audioFileName = null;
        }
    }

    public void cancelRecord() {
        if (recordStatus == RecordStatus.START && mediaRecorder!=null) {
            String file = audioFileName;
            stopRecord();
            File file1 = new File(file);
            file1.delete();
        }
    }

    /**
     * 获得录音的音量，范围 0-32767, 归一化到0 ~ 1
     *
     * @return
     */
    public synchronized float getMaxAmplitude() {
        if (recordStatus == RecordStatus.START && mediaRecorder != null) {
            try {

                return mediaRecorder.getMaxAmplitude() * 1.0f / 32768;


            } catch (RuntimeException e) {

                e.printStackTrace();

            }
        }
        return 0;
    }

}
