package com.example.wtz.learntosing.media;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wtz on 15/10/26.
 */
public class WriteToFileTask extends AsyncTask<String, Integer, String> {
    private String SDPathString = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/learntosing/exercise/";

    @Override
    protected String doInBackground(String... params) {

        logWaveData(params[0]);
        return null;
    }

    //将录音文件转成数据流并写到sd卡
    public void logWaveData(String name) {
        WaveFileReader waveFileReader = new WaveFileReader(SDPathString + name + ".wav");
//        Log.i("---recordpath-->",SDPathString + name + ".wav");
        File file = new File(SDPathString, name + ".txt");
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            int m = waveFileReader.getNumChannels();
            int n = waveFileReader.getDataLen();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    //  System.out.println("----datanum--->>" + waveFileReader.getData()[i][j]);

                    fileWriter.write(waveFileReader.getData()[i][j] + "\t");

                }
                Log.i("---newchannel-->", SDPathString + name + ".wav");
                fileWriter.write("\r\n");

            }
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        int m = waveFileReader.getNumChannels();
//        int n = waveFileReader.getDataLen();
//        for (int i = 0; i < m; i++) {
//            for (int j = 0; j < n; j++) {
//                System.out.println("----datanum--->>" + waveFileReader.getData()[i][j]);
//            }
//        }

    }
}
