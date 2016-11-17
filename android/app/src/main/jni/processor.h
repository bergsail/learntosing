#ifndef PROCESSOR_H
#define PROCESSOR_H

class Spectrum;
class Feature;
class Axes;
class Processor
{
public:
    Processor();
    ~Processor();

    int process(int lengthStd,
                float* oscilatorsStd,
                int* powersStd,
                int  onsetsLength,
                float* onsets,
                int lengthSample,
                int* waveSample,
                int* result);

    Spectrum* getSpectrumSample();
    Feature* getFeatureSample();
    Feature* getFeatureStd();
    Axes* getAxes();

private:
    void updateSampleOnsets(float* stdMinusSample, float* stdVsSample);

private:

    Feature* _featureStd;
    Spectrum* _spectrumSample;
    Feature* _featureSample;
    Axes* _axes;

    int _result;

};

#endif // PROCESSOR_H
