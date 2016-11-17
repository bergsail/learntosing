package com.example.wtz.learntosing.media;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wtz on 15/9/7.
 */
public class AudioManager {
    private MediaRecorder mRecorder;
    private String mDirString;
    private String mCurrentFilePathString;

    private boolean isPrepared;

    /**
     * 单例化这个类
     */
    private static AudioManager mInstance;

    private AudioManager(String dir) {
        mDirString = dir;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 回调函数，准备完毕，button才会显示录音框
     */
    public interface AudioStageListener {
        void wellPrepared();
    }

    public AudioStageListener mListener;

    public void setOnAudioStageListener(AudioStageListener listener) {
        mListener = listener;
    }

    //准备fangfa
    public void prepareAudio(String name) {
        try {
            isPrepared = false;

            File dir = new File(mDirString);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileNameString = name+".wav";
            File file = new File(dir, fileNameString);
            mCurrentFilePathString = file.getAbsolutePath();

            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

            mRecorder.prepare();
            mRecorder.start();
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名称
     *
     * @return
     */
    private String generalFileName() {
        return UUID.randomUUID().toString() + ".wav";
    }

    //获得声音的level
    public int getVoiceLevel(int maxLevel) {

        //mRecorder.getMaxAmplitude()这个是音频的振幅，（1，32767）
        if (isPrepared) {
            try {

                //
                return maxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    //释放资源
    public void release() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    //取消，删除文件
    public void cancel() {
        release();
        if (mCurrentFilePathString != null) {
            File file = new File(mCurrentFilePathString);
            file.delete();
            mCurrentFilePathString = null;
        }
    }

    public String getCurrentFilePathString() {
        return mCurrentFilePathString;
    }
}
