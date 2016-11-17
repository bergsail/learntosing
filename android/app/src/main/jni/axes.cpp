#include "axes.h"
#include "feature.h"
#include "stdlib.h"
#include "string.h"
#include "config.h"


#include <cmath>
#include <iostream>

#include <android/log.h>
#define LOG_TAG "wtzanaly"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

Axes::Axes()
{
    _axeStdX = 0;

    _axeSampleY = 0;

    _axesData = 0;
}

Axes::~Axes()
{
    if (_axesData) {

        free(_axesData);
    }

}

bool Axes::orthogonalize(Feature *featureStd, Feature *featureSample, float stdMinusSample, float stdVsSample,int* result) {

    if (stdVsSample == 0) {
        stdVsSample = 1.0;
    }
    LOGI("jnidataresultstdvsSample=======================:%3f",stdVsSample);
    std::cout<<"stdMinusSample:"<<stdMinusSample<<std::endl;

    std::cout<<"stdVsSample:"<<stdVsSample<<std::endl;

    std::cout<<"std"<<std::endl;

    float scoreNumber = 0;

    for (int i = 0; i < featureStd->getOnsetsLength() - 1; i++) {

        float* onsetsStd = featureStd->getOnsets();

        float onsetsStartStd = onsetsStd[i];

        float onsetsEndStd = onsetsStd[i + 1];

//        std::cout<<i<<":"<<" start=>"<<onsetsStartStd
//                <<" end=>"<<onsetsEndStd
//               <<" duration"<<onsetsEndStd - onsetsStartStd
//              <<std::endl;

        int onsetsStartStdAbs = onsetsStartStd * featureStd->getLength();

        int onsetsEndStdAbs = onsetsEndStd * featureStd->getLength();

        float pitchAve = 0;

        float pitchSum = 0;

        int count = 0;

        for (int j = onsetsStartStdAbs; j < onsetsEndStdAbs; j++) {

            float* oscilatorStd = featureStd->getBaseOscilators();

            std::cout<<oscilatorStd[j]<<std::endl;

            LOGI("jnidataresultstdoscilator:%3f",oscilatorStd[j]);

            if (oscilatorStd[j] > 0.05) {

                count++;

                pitchSum += oscilatorStd[j];
            }
        }

//        LOGI("jnidataresultspitchSum:%3f",pitchSum);

        float diff = (float) count;

        pitchAve = pitchSum / diff;

//        LOGI("jnidataresultsdiff:%3f",diff);

        std::cout<<"diff:"<<diff<<" stdAve:"<<pitchAve<<std::endl;
        LOGI("jnidataresultstdAve------------------------:%3f",pitchAve);

        float onsetsStartSample = (onsetsStd[0] - stdMinusSample) + (onsetsStd[i] - onsetsStd[0]) / stdVsSample;

        float onsetsEndSample = (onsetsStd[0] - stdMinusSample) + (onsetsStd[i + 1] - onsetsStd[0]) / stdVsSample;

        int onsetsStartSampleAbs = onsetsStartSample * featureSample->getLength();

        int onsetsEndSampleAbs = onsetsEndSample * featureSample->getLength();

        float* oscilatorSample = featureSample->getBaseOscilators();


        if (onsetsStartSampleAbs - 4 < 0) {

            onsetsStartSampleAbs = 4;
        }
        if (onsetsEndSampleAbs + 5 > featureSample->getLength()) {

            onsetsEndSample = featureSample->getLength() - 5;
        }

        float upLine = pitchAve * 1.5;

        float downLine = pitchAve * 2.0 / 3.0;

        float sumSample = 0;

        float aveSample = 0;

        int countSample = 0;

        for (int j = onsetsStartSampleAbs - 0; j < onsetsEndSampleAbs + 1; j++) {

            if (oscilatorSample[j] > downLine && oscilatorSample[j] < upLine ) {

                std::cout<<oscilatorSample[j]<<std::endl;

                LOGI("jnidataresultsampleoscilator:%3f",oscilatorSample[j]);

                sumSample += oscilatorSample[j];

                countSample ++;
            }
        }
        if (countSample == 0) {
            countSample = 1;
        }
        aveSample = sumSample /  (float)countSample;

        std::cout<<"diff:"<<countSample<<" sampleAve:"<<aveSample<<std::endl;


        LOGI("jnidataresultsampleAve=======================:%3f",aveSample);

        std::cout<<"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"<<std::endl;
//
//        LOGI("testreturn<<<<<<<<<<<<<<<<<<<:%3f",aveSample);

        float validScoreRange = VALIDSCORERANGE;

        if ( fabs (pitchAve - aveSample) < validScoreRange) {
            //
            float score = cos (fabs(pitchAve - aveSample) * PI / validScoreRange / 2);

            std::cout<<"score:"<<score<<std::endl;

//            LOGI("resultdata = %3f",score);

            scoreNumber += score;

            result[i + 1] = 0;

        } else {
            result[i + 1] = 1;
        }

    }

    scoreNumber /= (float)(featureStd->getOnsetsLength() - 1);

    result[0] = scoreNumber * 100;

//    std::cout<<"sample"<<std::endl;

//    for (int i = 0; i < featureSample->getOnsetsLength() - 1; i++) {

//        float* onsets = featureSample->getOnsets();

//        float onsetsStart = onsets[0] + ;

//        float onsetsEnd = onsets[i + 1];

//        std::cout<<i<<":"<<" start=>"<<onsetsStart<<" end=>"<<onsetsEnd<<" duration"<<onsetsEnd - onsetsStart<<std::endl;

//    }

}

int Axes::getX() {

    return _axeStdX;
}

int Axes::getY() {

    return _axeSampleY;
}

int* Axes::getData() {

    return _axesData;
}
