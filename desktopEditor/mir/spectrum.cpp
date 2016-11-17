#include "spectrum.h"
#include "wfft.h"
#include "stdlib.h"
#include "stdio.h"
#include "string.h"
#include "config.h"

Spectrum::Spectrum()
{
    _windowSize = WINDOWSIZE;

    _maxAbsFft = 0;
    _window = (float*) malloc(sizeof(float) * _windowSize);

    float N = (float)_windowSize;

    float pi = (float)PI;

    for(int n = 0; n < _windowSize; n++)
    {
        _window[n] = (float) ( sin( (pi * (float) n) / (N - 1.0)) ); //cos
    }


     _prCell=(int*)malloc(sizeof(int) * _windowSize);

     _prCellWindowed = (float*)malloc(sizeof(float) * _windowSize);

     _prCellFft = (float*)malloc(sizeof(float) * _windowSize / 2);

     _prAsist1 = (int*)malloc(sizeof(int) * _windowSize / 2);

     _prAsist2 = (float*)malloc(sizeof(float) * _windowSize / 2);
}

Spectrum::~Spectrum()
{
    if (_dataSpectrum) {

        free(_dataSpectrum);

        _dataSpectrum = NULL;
    }

    if (_prCell) {

        free(_prCell);

        _prCell = NULL;
    }

    if (_prCellWindowed) {

        free(_prCellWindowed);

        _prCellWindowed = NULL;
    }

    if (_prCellFft) {

        free(_prCellFft);

        _prCellFft = NULL;
    }

    if (_prAsist1) {

        free(_prAsist1);

        _prAsist1 = NULL;
    }

    if (_prAsist2) {

        free(_prAsist2);

        _prAsist2 = NULL;
    }
}

bool Spectrum::spectrumlize(int *data, int dataLength) {

    int horizontalWindowCellSize = dataLength / _windowSize;

    int verticalCellSize = _windowSize / 2;

    _dataSpectrumLength = verticalCellSize * horizontalWindowCellSize;

    _dataSpectrum = (int*)malloc(sizeof(int)*_dataSpectrumLength);

    for (int i = 0; i < horizontalWindowCellSize; i++) {

        memcpy(_prCell,(int*)data + i * _windowSize,sizeof(int)*_windowSize);

        memset(_prCellFft,0,sizeof(float)*_windowSize / 2);

        memset(_prAsist1,0,sizeof(int)*(2+(int)sqrt((float)(_windowSize / 2))));

        memset(_prAsist2,0,sizeof(float)*(_windowSize / 2));

        _prAsist1[0] = 0;

        for (int j = 0; j < _windowSize; j++) {

            _prCellWindowed[j] = (float)_prCell[j] * _window[j];

        }

        rdft(_windowSize, 1, _prCellWindowed, _prAsist1, _prAsist2);

        float re = _prCellWindowed[1] ;

        float im = 0;

        float amp = (float)(sqrt(re * re + im * im) / sqrt(double(_windowSize)));

        _prCellFft[_windowSize / 2 - 1] = amp * amp ;

        _dataSpectrum[_windowSize / 2 * i + _windowSize / 2 - 1] = (int) _prCellFft[_windowSize / 2 - 1] ;

//         qDebug()<<"fft"<<_dataSpectrum[_windowSize / 2 * i + _windowSize / 2 - 1];

        if (_dataSpectrum[_windowSize / 2 * i + _windowSize / 2 - 1] > _maxAbsFft) {

            _maxAbsFft = _dataSpectrum[_windowSize / 2 * i + _windowSize / 2 - 1] ;
        }

        for(int j = 1; j < _windowSize / 2; j++)
        {
            re = _prCellWindowed[j * 2] ;

            im = _prCellWindowed[j * 2 + 1];

            amp = (float)(sqrt(re * re + im * im) / sqrt(double(_windowSize)));

            _prCellFft[j - 1] = amp * amp ;

            _dataSpectrum[_windowSize / 2 * i + j - 1] = (int)_prCellFft[j - 1] ;


//            qDebug()<<"fft"<<_dataSpectrum[_windowSize / 2 * i + j - 1];

            if(_dataSpectrum[_windowSize / 2 * i + j - 1]  > _maxAbsFft) {

                _maxAbsFft = (int)(_prCellFft[j - 1]);
            }
        }
    }
//    qDebug()<<"50,0"<<_dataSpectrum[_windowSize / 2 * 50];
//    for (int i = 0; i < _dataSpectrumLength; i++) {
//        int data =  _dataSpectrum[i];
//        qDebug()<<data;
//    }
//    qDebug()<<"maxFft"<<_maxAbsFft;
}

int Spectrum::getWindowSize() {

    return _windowSize;
}

int Spectrum::getMaxAbsFft() {

    return _maxAbsFft;
}
