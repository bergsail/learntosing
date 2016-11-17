#include "processor.h"
#include "spectrum.h"
#include "feature.h"
#include "axes.h"
#include "processor.h"
#include "config.h"

#include <iostream>
#include <vector>
#include <math.h>

#include <android/log.h>
#define LOG_TAG "wtzanaly"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

Processor::Processor()
{
    _spectrumSample = 0;

    _result = 0;
}

Processor::~Processor()
{
    if (_spectrumSample) {

        delete _spectrumSample;
    }

    if (_featureStd) {

        delete _featureStd;
    }

    if (_featureSample) {

        delete _featureSample;
    }

    if (_axes) {

        delete _axes;
    }
}
 int Processor::process(int lengthStd,
                       float *oscilatorsStd,
                       int *powersStd,
                       int onsetsLength,
                       float *onsets,
                       int lengthSample,
                       int *waveSample,
                       int *result
                       ) {

     std::cout<<"jniprocess"<<lengthStd<<std::endl;

    _spectrumSample = new Spectrum();

    _spectrumSample->spectrumlize(waveSample, lengthSample);

    _featureSample = new Feature();

    _featureSample->featurelize(_spectrumSample->_dataSpectrum,_spectrumSample->_dataSpectrumLength,_spectrumSample->getMaxAbsFft());

    _featureStd = new Feature();

    _featureStd->featurelize(lengthStd,oscilatorsStd,powersStd,onsetsLength,onsets);

    float stdMinusSample = 0;

    float stdVsSample = 0;

    updateSampleOnsets(&stdMinusSample, &stdVsSample);

    _axes = new Axes();

    _axes->orthogonalize(_featureStd,_featureSample,stdMinusSample, stdVsSample,result);

//     LOGI("jnidataresult:%d",result[0]);

    return _result;
}

void Processor::updateSampleOnsets(float* stdMinusSample, float* stdVsSample) {

    //std
    int onsetsLengthStd = _featureStd->getOnsetsLength();

    float* onsetsStd = _featureStd->getOnsets();

    for (int i = 0; i < onsetsLengthStd; i++) {

        float data = onsetsStd[i];

//        std::cout<<"<"<<i<<","<<data<<">"<<std::endl;
    }
    std::cout<<std::endl;

//    std::cout<<"<<<<<<<<<<<<<<<<<<<<<"<<std::endl;


    // get diff data
    std::vector<float > diffsOnsetStdEnd;

    diffsOnsetStdEnd.push_back(0);

    float diffData = 0;

    float onsetsStdEndLarger = ONSETENDLARGERTHREDHOLD;

    float onsetsStdEndSmaller = ONSETENDSMALLTHREDHOLD;

    float onsetEnd = onsetsStd[onsetsLengthStd - 1];

    float onsetSecondEnd = onsetsStd[onsetsLengthStd - 2];

    if (onsetEnd - onsetSecondEnd> onsetsStdEndLarger) {

        diffData =  DIFFONSETENDLARGER;
    }
    else if (onsetEnd - onsetSecondEnd > onsetsStdEndSmaller) {

        diffData =  DIFFONSETENDSMALL;
    }
    if (diffData != 0) {

        for (int i = 1; i < 7; i++) {

            diffsOnsetStdEnd.push_back(i * diffData);

            diffsOnsetStdEnd.push_back((-1) * i * diffData);
        }
    }

    //make recuit sample

    int maxData = 0;

    int onsetsLengthSample = _featureSample->getOnsetsLength();

    if (onsetsLengthSample > onsetsLengthStd * 1.5) {

        _result = 0;

        return;
    }


    int diffRecord = 0;

    for (int diffi = 0; diffi < diffsOnsetStdEnd.size(); diffi++) {


        float* onsetsSample = _featureSample->getOnsets();

        float stdMinusSampleOnset = onsetsStd[0]  - onsetsSample[0];

        float stdVsSampleOnset = fabs(onsetsStd[onsetsLengthStd - 1] + diffsOnsetStdEnd.at(diffi)- onsetsStd[0]) / fabs(onsetsSample[onsetsLengthSample -1] - onsetsSample[0]);


        std::cout<<"diff:"<<diffsOnsetStdEnd.at(diffi)<<std::endl;
        std::cout<<"std"<<std::endl;

        for (int i = 0; i < onsetsLengthStd; i++) {

            float data = onsetsStd[i];

            if (i == onsetsLengthStd - 1) {

                data += diffsOnsetStdEnd.at(diffi);
            }
//            std::cout<<"<"<<i<<","<<data<<">"<<std::endl;
        }

        std::cout<<"sample"<<std::endl;

        for (int i = 0; i < onsetsLengthSample; i++) {
//            onsetsSample[i] = (onsetsSample[i] - onsetsSample[0]) * stdVsSample + onsetsSample[0] + stdMinusSampleOnset;
//            float data = onsetsSample[i];
              float data =  (onsetsSample[i] - onsetsSample[0]) * stdVsSampleOnset + onsetsSample[0] + stdMinusSampleOnset;
            std::cout<<"<"<<i<<","<<data<<">"<<std::endl;
        }
        std::cout<<std::endl;

        int featuredata = 0;

        for (int i = 1; i < onsetsLengthStd - 1; i++) {

            float dataStd = onsetsStd[i];

            for (int j = 1; j < onsetsLengthSample - 1; j++) {

                float dataSample = (onsetsSample[j] - onsetsSample[0]) * stdVsSampleOnset + onsetsSample[0] + stdMinusSampleOnset;

                if (fabs(dataStd - dataSample) < 0.006) {

                    featuredata += 10;

                } else if (fabs(dataStd - dataSample) < 0.009) {

                    featuredata += 8;

                } else if (fabs(dataStd - dataSample) < 0.012) {

                    featuredata += 6;

                } else if (fabs(dataStd - dataSample) < 0.015) {

                    featuredata += 4;
                }
            }
        }


        std::cout<<featuredata<<std::endl;

        std::cout<<"-------------"<<std::endl;

        if (featuredata > maxData) {

            maxData = featuredata;

            *stdMinusSample = stdMinusSampleOnset;

            *stdVsSample = stdVsSampleOnset;

            diffRecord = diffi;

        }
    }


    if (maxData > (onsetsLengthStd - 1) * 10 * 4 / 10) {
        _result = 60;
    } else {
        _result = 0;
    }
    return ;

}

Spectrum* Processor::getSpectrumSample() {

    return _spectrumSample;
}

Feature* Processor::getFeatureSample() {

    return _featureSample;
}

Feature* Processor::getFeatureStd() {

    return _featureStd;
}

Axes* Processor::getAxes() {

    return _axes;
}
