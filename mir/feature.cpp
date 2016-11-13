#include "feature.h"
#include "stdlib.h"
#include <iostream>
#include "config.h"
#include <vector>
#include <algorithm>

Feature::Feature()
{
    _baseOscilators = 0; // from 0 to 1 which maxoscilation is MAXINTERESTOSCILATIONPIXEL
    _basePowers = 0;
    _length = 0;
    _onsets = 0;
    _onsetsLength = 0;
    _maxoscilation = MAXINTERESTOSCILATIONPIXEL;

    _featuredModePrepared = false;
}

Feature::~Feature()
{
    if (!_featuredModePrepared) {

        if (_baseOscilators) {

            free(_baseOscilators);
        }
        if (_basePowers) {

            free(_basePowers);
        }
        if (_onsets) {

            free(_onsets);
        }
    }
}

bool Feature::featurelize(int lengthStd, float *oscilators, int *powers, int onsetsLength, float *onsets){

    _featuredModePrepared = true;
    _length = lengthStd;
    _baseOscilators = oscilators;
    _basePowers = powers;
    _onsetsLength = onsetsLength;
    _onsets = onsets;
}

bool Feature::featurelize(int *dataSpectrum, int dataSpectrumLength,int maxFFt) {

    _featuredModePrepared = false;

   int windowSize = WINDOWSIZE;

   int halfwindow = windowSize / 2;

   int powerThredholdOfFrequency = SPECTRUMPOWERTHREDHOLD;

   int powerThredholdOfDoubleFrequency = SPECTRUMPOWERTHREDHOLDDOUBLE;

   int powerDiv = POWERDIV;

   int horizontalSize= dataSpectrumLength / halfwindow;

   int verticalSize = _maxoscilation;

   _length = horizontalSize;

   _baseOscilators = (float*)malloc(sizeof(float) * horizontalSize);

   _basePowers = (int*)malloc(sizeof(int) * horizontalSize);

   _sumPowers = (int*)malloc(sizeof(int) * horizontalSize);

   _diffPowers = (int*)malloc(sizeof(int) * horizontalSize);

   for (int i = 0; i < horizontalSize ; i++) {

       // first find interest point
       int roughInterestY = 0;

       for (int j = 1; j < verticalSize; j++) {

           int currentYPower = dataSpectrum[i * halfwindow + j] /  (float)(maxFFt/powerDiv/powerDiv);

           int nextYPower = dataSpectrum[i * halfwindow + j + 1] / (float)(maxFFt/powerDiv/powerDiv);

            currentYPower= currentYPower<0?255:currentYPower;currentYPower= currentYPower>255?255:currentYPower;

             nextYPower= nextYPower<0?255:nextYPower;nextYPower= nextYPower>255?255:nextYPower;

           if ((currentYPower > powerThredholdOfFrequency
                   &&nextYPower > powerThredholdOfFrequency
                   &&(currentYPower + nextYPower) > powerThredholdOfDoubleFrequency)
                   ||(currentYPower > powerThredholdOfDoubleFrequency
                      ||nextYPower > powerThredholdOfDoubleFrequency)) {

               roughInterestY = j;

               break;
           }
       }

       //second find the precise oscilator
       float preciseOscilator = 0.0;

       float power = 0.0;


       if (roughInterestY != 0) {

           int y1Power = dataSpectrum[i * halfwindow + roughInterestY + 0] / (float)(maxFFt/powerDiv/powerDiv);

           int y2Power = dataSpectrum[i * halfwindow + roughInterestY + 1] / (float)(maxFFt/powerDiv/powerDiv);

           int y3Power = dataSpectrum[i * halfwindow + roughInterestY + 2] / (float)(maxFFt/powerDiv/powerDiv);

           int y4Power = dataSpectrum[i * halfwindow + roughInterestY + 3] / (float)(maxFFt/powerDiv/powerDiv);

           y1Power= y1Power<0?y1Power*(-1):y1Power;//y1Power= y1Power>255?255:y1Power;

           y2Power= y2Power<0?y2Power*(-1):y2Power;//y2Power= y2Power>255?255:y2Power;

           y3Power= y3Power<0?y3Power*(-1):y3Power;//y3Power= y3Power>255?255:y3Power;

           y4Power= y4Power<0?y4Power*(-1):y4Power;//y4Power= y4Power>255?255:y4Power;

           if (y1Power + y2Power  == 0) {

               y1Power = 1;
           }

           if (y3Power + y4Power == 0) {

               y3Power = 1;
           }

           float interestY12 = (float)roughInterestY - (float)(y1Power) / (float)(y1Power + y2Power);

           int y12Power = y1Power + y2Power;

           float interestY34 = (float)(roughInterestY + 1) + (float)(y4Power) / (float)(y3Power + y4Power);

           float y34Power = y3Power + y4Power;

           preciseOscilator = interestY12 + y34Power * (interestY34 - interestY12) / (y12Power + y34Power);

           preciseOscilator /= (float)_maxoscilation;

           power = (y12Power + y34Power) / 4;
       }

       // third find the onsets feature

       int sumPowers = 0;

       for (int j =  1; j < verticalSize * 4; j++) {

           int powerHigh = dataSpectrum[i * halfwindow + j] /  (float)(maxFFt/powerDiv/powerDiv);

           if (powerHigh < 0) powerHigh = 0 ;if (powerHigh > 255) powerHigh = 255;

           sumPowers += powerHigh;
       }

       _baseOscilators[i] = preciseOscilator;

       _basePowers[i] = power;



       _sumPowers[i] = sumPowers;

       if (i == 0) {
           _diffPowers[i] =  0;
       } else {
           _diffPowers[i] = _sumPowers[i] - _sumPowers[i-1];
       }
//       if (i >= 2) {
//           _diffPowers[i-1] = (_diffPowers[i-2] + _diffPowers[i-1] + _diffPowers[i]) / 3;
//       }
   }

   int threadholdStart = INTERESTSTARTTHREDHOLD;

   for (int i = 0; i < horizontalSize; i++) {

       if (_sumPowers[i] > threadholdStart ) {

           _interestStart = i;

           break;
       }
   }

   int threadholdEnd = INTERESTENDTHREDHOLD;

   for(int i = horizontalSize - 1; i > 0; i--) {

       if (_sumPowers[i] >threadholdEnd) {

           _interestEnd = i;

           break;
       }
   }

   int onsetThreadHold = ONSETTHREADHOLD;

   std::vector<int> onsetsVector;

   for (int i = 1; i <horizontalSize - 1; i++) {

       if (_diffPowers[i] > onsetThreadHold
               || (_diffPowers[i] + _diffPowers[i -1] > onsetThreadHold
                   &&_diffPowers[i] > 0&&_diffPowers[i-1]>0)
               ) {

//               || _diffPowers[i] + _diffPowers[i -1] + _diffPowers[i + 1] > onsetThreadHold


           std::vector<int>::iterator findPreIterResult1 = std::find(onsetsVector.begin(),onsetsVector.end(),i-1);

           std::vector<int>::iterator findPreIterResult2 = std::find(onsetsVector.begin(),onsetsVector.end(),i-2);

           if (findPreIterResult1 == onsetsVector.end()&&findPreIterResult2 == onsetsVector.end()){

               onsetsVector.push_back(i);
           }
       }

   }

   _onsetsLength = onsetsVector.size() + 1;

   _onsets = (float*)malloc(sizeof(float)*_onsetsLength);

   for (int i = 0; i< _onsetsLength - 1; i++) {

       _onsets[i] = (float)onsetsVector.at(i) / (float)horizontalSize;

       std::cout<<"onsett:"<<i<<" data:" << _onsets[i]<<std::endl;
   }

   _onsets[_onsetsLength - 1] = (float)_interestEnd / (float)horizontalSize;

   std::cout<<"onsett:"<<_onsetsLength - 1<<" data:" << _onsets[_onsetsLength - 1]<<std::endl;
   return true;
}

float* Feature::getBaseOscilators(){

    return _baseOscilators;
}

int* Feature::getBasePowers() {

    return _basePowers;
}

int Feature::getLength() {

    return _length;
}

int Feature::getOnsetsLength() {

    return _onsetsLength;
}

float* Feature::getOnsets() {

    return _onsets;
}

int* Feature::getSumPowers() {

    return _sumPowers;
}

int* Feature::getDiffPowers() {

    return _diffPowers;
}

int Feature::getInterestStart() {

    return _interestStart;
}

int Feature::getInterestEnd() {

    return _interestEnd;
}
