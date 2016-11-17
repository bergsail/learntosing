package com.example.wtz.learntosing.media;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.wtz.learntosing.activity.MainActivity;
import com.example.wtz.learntosing.api.Rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wtz on 15/10/23.
 */
public class LoadMp3Task extends AsyncTask<String, Integer, String> {
    private Context mContext;
    public LoadMp3Task(Context context) {
        mContext=context;
    }


    @Override
    protected String doInBackground(String... strings) {
        String path = "";
        retrofit.client.Response response = Rest.api().getMP3Service().getxml(strings[0]);
        try {
            byte[] bytes = getBytesFromStream(response.getBody().in());
            String name;
            File sdcardDir = Environment.getExternalStorageDirectory();
            path = sdcardDir.getPath() + "/learntosing/" + strings[1];
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }

            name = path1 + "/" + strings[2] + ".mp3";
            File file = new File(name);
            saveBytesToFile(bytes, file);
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    protected void onPostExecute(String result) {
        ((MainActivity) mContext).filepath = result;
        (( MainActivity) mContext).refresh();
        super.onPostExecute(result);

    }

    public static byte[] getBytesFromStream(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        buf = bos.toByteArray();

        return buf;
    }

    public static void saveBytesToFile(byte[] bytes, File file) {
        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(bytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


