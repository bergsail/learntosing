package com.example.wtz.learntosing.utils;

/**
 * Created by wtz on 15/10/30.
 */
public class NativeMethod {
    public native String displayHelloWorld();
    public native int inputData(int []a);
    public native  int[] process(int lengthStd,
                               double []oscilatorsStd,
                               int []powersStd,
                               int  onsetsLength,
                               double []onsets,
                               int lengthSample,
                               int []waveSample
                               );

    static {
        System.loadLibrary("hello-jni");
    }
}
