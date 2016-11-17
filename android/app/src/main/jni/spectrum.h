#ifndef SPECTRUM_H
#define SPECTRUM_H


class Spectrum
{
public:
    Spectrum();
    ~Spectrum();

    bool spectrumlize(int* data, int dataLength);
    int getWindowSize();
    int getMaxAbsFft();

    int* _dataSpectrum;
    int _dataSpectrumLength;


private:
    float* _window;
    int _windowSize;

    int _maxAbsFft;

    int* _prCell;
    float* _prCellWindowed;
    float* _prCellFft;
    int* _prAsist1;
    float* _prAsist2;
};

#endif // SPECTRUM_H
