package com.example.wtz.learntosing.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wtz on 15/10/26.
 */
public class RecordTask extends AsyncTask<String, Integer, String> {
    private Context mContext;

    private int frequence = 44100;//16000;// 8000;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private String SDPathString = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/learntosing/exercise/";
    private String filename = null;
    private File file = null;
    private boolean isRecording = true,
            isPlaying = false; // 标记

    public RecordTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        File path1 = new File(SDPathString);
        if (!path1.exists()) {
            //若不存在
            path1.mkdirs();
        }
        isRecording = true;
        filename = params[0];
        try {
            file = createFile(filename + ".wav");//增加一个文件接收硬件返回的数据流

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            // 开通输出流到指定的文件
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            // 根据定义好的几个配置，来获取合适的缓冲大小
            int bufferSize = AudioRecord.getMinBufferSize(frequence, channelConfig, audioEncoding);
            // 实例化AudioRecord
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence, channelConfig, audioEncoding, bufferSize);
            // 定义缓冲
            short[] buffer = new short[bufferSize];
            /****************************SET EQ****************************************************/
            CSetEQ setEQ = new CSetEQ();   //定义一个设置EQ的对象
            setEQ.init_equliazer(10);            //初始化EQ，个数为10个频点
            /***********************************************************************************************/
            //*********************************************************************************************
            PuJianFa puJianFa = new PuJianFa();
            int m0, i, j;
            int length = 0;
            int shift = 0, lhalf, lpow2, half_pow2;
            double smul = 0.0;
            double lambda = 0.0;
            double ar, ai, power;
            double kk;
            if (0 == length) length = puJianFa.DEFAULT_LENGTH;
            if (0 == shift) shift = puJianFa.DEFAULT_SHIFT;
            if (0.0 == smul) smul = puJianFa.DEFAULT_MULTIPLE;
            if (0.0 == lambda) lambda = puJianFa.DEFAULT_SMOOTHING;

            m0 = 0;
            lpow2 = 1;
            lhalf = ((length + 1) / 2);
            length = lhalf + lhalf;    /* rounding */
            while (lpow2 < length) {
                lpow2 += lpow2;
                ++m0;
            }
            half_pow2 = lpow2 / 2;
            double[] win = new double[length];
            double[] frame = new double[lpow2 + 2];
            double[] noise = new double[half_pow2 + 1];
            double[] pre = new double[shift];
            double[] rev_win = new double[shift + shift];
            short[] is = new short[length];
            short[] ix = new short[shift];
            for (i = 0; i < length; ++i)//win 从1-512递增，512-1024递减
            {
                win[i] = 0.5 - 0.5 * Math.cos(puJianFa.PI * 2.0 * i / length);
            }
            for (i = -shift; i < shift; ++i)
                rev_win[i + shift] = (0.5 + 0.5 * Math.cos(puJianFa.PI * i / shift)) / win[i + lhalf];

            // 开始录制
            record.startRecording();
            int bufferReadResult = record.read(buffer, 0, length);//先预读1024个short 进行预处理
            int index = 0;                                          //读取数据赋值索引
            for (j = 0; j < length; j++)                                  //将读出的数据放入is然后进行预处理
            {
                is[index++] = buffer[j];
            }
            puJianFa.multirr(length, is, win, frame);             //加窗处理行成一帧
            for (i = length; i < lpow2; ++i)
                frame[i] = 0.0;
            puJianFa.rfft(m0, frame);                              //傅里叶变换
            for (i = j = 0; j <= lpow2; ++i, j += 2)              //对窗帧进行
            {
                noise[i] = Math.sqrt(frame[j] * frame[j] + frame[j + 1] * frame[j + 1]);
                //System.out.println(i+"="+(double)noise[i]);
            }
            for (i = 0; i < shift; ++i)
                pre[i] = 0.0;


            byte temp[] = new byte[512];
            // 定义循环，根据isRecording的值来判断是否继续录制
            while (isRecording) {
                puJianFa.multirr(length, is, win, frame);
                for (i = length; i < lpow2; ++i)
                    frame[i] = 0.0;
                puJianFa.rfft(m0, frame);
                for (i = j = 0; i <= lpow2; i += 2, ++j) {
                    ar = frame[i];
                    ai = frame[i + 1];
                    power = Math.sqrt(ar * ar + ai * ai + 1.0e-30);
                    ar /= power;
                    ai /= power;

                    kk = Math.pow(power, 0.4) - 0.9 * Math.pow(noise[j], 0.4);
                    if (kk < 0)
                        kk = 0;
                    kk = Math.pow(kk, (1 / 0.4));
                    power = kk;
                    frame[i] = ar * power * puJianFa.DEFAULT_AGCKK;
                    frame[i + 1] = ai * power * puJianFa.DEFAULT_AGCKK;
                }
                puJianFa.irfft(m0, frame);
                for (i = 0; i < shift; ++i) {
                    ar = pre[i] + frame[i + lhalf - shift] * rev_win[i];
                    ix[i] = (short) ar;
                    pre[i] = frame[i + lhalf] * rev_win[i + shift];
                }
                setEQ.iir(ix);
                    /*
                    for (i = 0; i < shift; )
					{
						for(int div = 0;div < 16;div++)
						{
							dataToSet[div] = ix[i++];
						}
						setEQ.iir(dataToSet);
						for(int div = 0;div < 16;div++)
						{
							dos.writeShort(dataToSet[div]);
						}
					}
					*/
                bufferReadResult = record.read(buffer, 0, shift);
                int leneff = length - shift;                //1024 -256
                index = leneff;                            //将从文件读取新的一个帧存入
                for (i = 0; i < leneff; ++i)            //取前面的三个帧
                    is[i] = is[i + shift];
                for (j = 0, i = 0; j < shift; j++) {
                    temp[i++] = (byte) (ix[j] & 0x0ff);
                    temp[i++] = (byte) (ix[j] >> 8);
                    is[index++] = buffer[j];
                }
                dos.write(temp);
            }

            // 录制结束
            record.stop();

            dos.close();

            RandomAccessFile out = new RandomAccessFile(SDPathString + filename + ".wav", "rw");
            WriteWaveFileHeader2(out, out.length() - 44, out.length() + 36 - 44, 44100, 1, 16 * 44100 * 1 / 8);
            //copyWaveFile(SDPathString+filename+".pcm",SDPathString+filename+".wav",setEQ);
            //file.delete();
        } catch (Exception e) {
            // TODO: handle exception
        }

        return filename;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        WriteToFileTask writeToFileTask = new WriteToFileTask();
        writeToFileTask.execute(s);

    }

    public void stopRecord() {
        isRecording = false;
    }

    private void WriteWaveFileHeader2(RandomAccessFile out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.seek(0);
        out.write(header, 0, 44);
        out.close();

    }
    public File createFile(String filenameInput) throws IOException {
        File file = new File(SDPathString + filenameInput);

        file.createNewFile();
        return file;
    }
}
