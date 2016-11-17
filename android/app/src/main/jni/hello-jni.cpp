/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <stdlib.h>
#include <iostream>
#include <jni.h>
#include <android/log.h>
#include "processor.h"

#ifdef __cplusplus
extern "C" {
#endif

#define LOG_TAG "wtzanaly"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
//#include <sstream>
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_example_wtz_learntosing_utils_NativeMethod_displayHelloWorld(JNIEnv *env,
                                                                      jobject thiz) {
#if defined(__arm__)
#if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #define ABI "armeabi-v7a/NEON"
    #else
      #define ABI "armeabi-v7a"
    #endif
#else
#define ABI "armeabi"
#endif
#elif defined(__i386__)
    #define ABI "x86"
#elif defined(__mips__)
    #define ABI "mips"
#else
#define ABI "unknown"
#endif
    LOGI("hello world=%1$s", "zaihello"); //其中1$为java的String类的format需要加上的.其他的写法和C语言一致
    return env->NewStringUTF("Hello from JNI !  Compiled with ABI " ABI ".");
}

jint
Java_com_example_wtz_learntosing_utils_NativeMethod_inputData(JNIEnv *env, jobject thiz,
                                                              jintArray Attr) {
    jint *arr;
    jint length;
    arr = env->GetIntArrayElements(Attr, NULL);
    length = env->GetArrayLength(Attr);


    return length;
}

jintArray
Java_com_example_wtz_learntosing_utils_NativeMethod_process
        (JNIEnv *env, jobject thiz,
         jint lengthStd,
         jdoubleArray oscilatorsStd,
         jintArray powersStd,
         jint onsetsLength,
         jdoubleArray onsets,
         jint lengthSample,
         jintArray waveSample) {

    jint tmp[onsetsLength];
    tmp[0] = 68;
    int i;
    for (i = 1; i < onsetsLength; i++) {
        if (i % 2 == 0) {
            tmp[i] = 0;
        } else {
            tmp[i] = 1;
        }
    }
    jintArray iarr = env->NewIntArray(lengthStd);
    env->SetIntArrayRegion(iarr, 0, lengthStd, tmp);     //将tmp复制到iarr中

    LOGI("hello world=%1$s", "dfsd"); //其中1$为java的String类的format需要加上的.其他的写法和C语言一致

    int clengthStd = env->GetArrayLength(oscilatorsStd);
    int clengthStdCheck = env->GetArrayLength(powersStd);
    float* coscilatorsStd = (float* )malloc(sizeof(float)*clengthStd);
    int* cpowerStd = (int*)malloc(sizeof(int)*clengthStd);
    int consetsLength = onsetsLength;
    float* consets = (float*)malloc(sizeof(float)*consetsLength);
    int cwaveSampleLength = lengthSample;
    int* cwaveSample = (int*)malloc(sizeof(int)*cwaveSampleLength);
    int* result = (int*)malloc(sizeof(int)*consetsLength);
    memset(result,0,sizeof(int)*consetsLength);


    jdouble* oscilatorArr = NULL;
    oscilatorArr = env->GetDoubleArrayElements(oscilatorsStd, NULL); //推荐使用
    if (oscilatorArr == NULL) {
        return iarr; /* exception occurred */
    }
    for (int i=0; i< clengthStd; i++) {
        coscilatorsStd[i]= oscilatorArr[i];
//        LOGI("jnidoublearrayOscilator=%3f",coscilatorsStd[i]);
    }
    env->ReleaseDoubleArrayElements(oscilatorsStd,oscilatorArr, 0);


    jint* powerArr = NULL;
    powerArr = env->GetIntArrayElements(powersStd,NULL);
    if(powerArr == NULL) {
        return iarr;
    }
    if (clengthStd != clengthStdCheck) {
        LOGI("jniError oscilatorLength not equal with clengthStdCheck");
        return iarr;
    }


    for (int i = 0; i < clengthStd;i++) {
        cpowerStd[i] = powerArr[i];
//        LOGI("jniintarrayPower=%d",cpowerStd[i]);
    }
    env->ReleaseIntArrayElements(powersStd,powerArr,0);


    jdouble* onsetsArr = NULL;
    onsetsArr = env->GetDoubleArrayElements(onsets,NULL);
    if(onsetsArr == NULL) {
        return iarr;
    }
    for (int i = 0; i < consetsLength;i++) {
        consets[i] = onsetsArr[i];
//        LOGI("jnifloatarrayOnsets = %3f",consets[i]);
    }
    env->ReleaseDoubleArrayElements(onsets,onsetsArr,0);


    jint* waveArr = NULL;
    waveArr = env->GetIntArrayElements(waveSample,NULL);
    if (waveArr == NULL) {
        return iarr;
    }
    for (int i = 0; i < cwaveSampleLength; i++) {
        cwaveSample[i] = waveArr[i];
//        LOGI("jniintarrayWavesample = %d",cwaveSample[i]);
    }
    env->ReleaseIntArrayElements(waveSample,waveArr,0);


    Processor* processor = new Processor;
    int dataProcessor = processor->process(clengthStd,coscilatorsStd,cpowerStd,consetsLength,consets,cwaveSampleLength,cwaveSample,result);
//    processor->process(lengStd,oscilatorsStd,powersStd,onsetsLength,onsets,lengthSample,waveSample);


    // if allright bonus
    bool allRightOK = true;
    for (int i = 1; i < clengthStd; i++) {
        if (result[i] == 0) {
            allRightOK = false;
            break;
        }
    }
    if (allRightOK) {
        result[0] += 40;
        if (result[0] > 100)
            result[0] =100;
    }

    for (int i = 0; i < onsetsLength; i++) {
        tmp[i] = result[i];
//        LOGI("resultdata = %d",result[i]);
    }

//    env->SetObjectArrayElement(texts, i, jstr);
//    env->SetIntArrayRegion(iarr,0,1,&dataProcessor);
//    env->SetIntArrayElement(iarr,0,dataProcessor);


    free(coscilatorsStd);
    free(cpowerStd);
    free(consets);
    free(cwaveSample);
    free(result);

    env->SetIntArrayRegion(iarr, 0, lengthStd, tmp);

    return iarr;
};

}
/** int process(int lengthStd,
float* oscilatorsStd,
int* powersStd,
int  onsetsLength,
float* onsets,
int lengthSample,
int* waveSample,
int* onsetsResults);
*/