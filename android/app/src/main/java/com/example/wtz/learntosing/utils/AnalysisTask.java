package com.example.wtz.learntosing.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.example.wtz.learntosing.R;
import com.example.wtz.learntosing.activity.MainActivity;
import com.example.wtz.learntosing.data.SimpleData;
import com.example.wtz.learntosing.media.WaveFileReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtz on 15/10/26.
 */
public class AnalysisTask extends AsyncTask<String, Integer, String> {
    private String SDPathString = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/learntosing/exercise/";
    private Context mContext;
    private String position;
    private ProgressDialog progDialog;
    private int colorlist[];

    public AnalysisTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog = new ProgressDialog(mContext, R.style.dialog);

        progDialog.setMessage("努力评分中...");
        progDialog.setIndeterminate(true);
        progDialog.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.my_progess_dialog));
        progDialog.setCancelable(true);
        progDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {

        String name = params[0];
//        String position = params[1];

        position = params[1];
//        int numlen = MainActivity.srcStrings[Integer.valueOf(position)].length();
//        int[] colornum = new int[numlen];
        WaveFileReader waveFileReader = new WaveFileReader(SDPathString + name + ".wav");
        int m = waveFileReader.getNumChannels();
        int n = waveFileReader.getDataLen();
        int waveDataLength = n;
        int[] waveData = new int[waveDataLength];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                waveData[j] = waveFileReader.getData()[i][j];
            }
            break;
        }
       System.out.println("jniposition" + position);
        int lenOfWord=Integer.valueOf(params[2]);
        int score = 10;//得分
        SimpleData simpleData = new SimpleData();
        int len = simpleData.oscilators.length;
        NativeMethod nativeMethod=new NativeMethod();
        int result[];//返回结果
        int index = Integer.parseInt(position);
        result=nativeMethod.process(len,simpleData.oscilators[index],simpleData.powers[index],(int)(lenOfWord + 1),simpleData.onsets[index],waveDataLength,waveData);
        int temp = lenOfWord + 1;
        System.out.println("---analResult---->>>" + temp);
        score=result[0];
        colorlist=new int[result.length-1];
        for(int x=1;x<result.length;x++){
            colorlist[x-1]=result[x];
            System.out.println(result[x]+",");
        }
//        System.out.println("---jni--->>"+nativeMethod.displayHelloWorld()+"len:"+nativeMethod.inputData(simpleData.powers[0]));
//
//        for (int x = 0; x < len; x++) {
//
//            System.out.println("---simpledata-length--->>" + simpleData.oscilators[x].length);
//
//
//// myTest(simpleData.oscilators[x]);
//
//            for (int y = 0; y < simpleData.oscilators[x].length; y++) {
////                System.out.println("---simpledata-detail--->>" + simpleData.oscilators[x][y]);
//            }
//        }



//        for (int[] s : MatrixData.matrix) {
//            //你说的12行数据写入
//            System.out.println("---analy--->>" + s.length);
////            score = score * s.length;
//        }
//        for (int i = 0; i < numlen; i++) {
//
//            colornum[i] = 0;
//        }
        return String.valueOf(score);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progDialog.dismiss();
        ((MainActivity) mContext).refreshScore(Integer.valueOf(position), Integer.valueOf(s),colorlist);
    }
    private void myTest(double[] a){
        for(int i=0;i<a.length;i++){
            System.out.println("---test-data---->>"+a[i]);
        }
    }
}
