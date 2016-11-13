#ifndef FEATURE_H
#define FEATURE_H


class Feature
{
public:
    Feature();
    ~Feature();

    bool featurelize(int* dataSpectrum, int dataSpectrumLength, int maxFFt);
    bool featurelize(int lengthStd,
                     float *oscilators,
                     int *powers,
                     int onsetsLength,
                     float *onsets);

    int getLength();
    float* getBaseOscilators();
    int* getBasePowers();
    int* getSumPowers();
    int* getDiffPowers();

    int getOnsetsLength();
    float* getOnsets();

    int getInterestStart();
    int getInterestEnd();

private:
    float* _baseOscilators; // from 0 to 1 which maxoscilation is MAXINTERESTOSCILATIONPIXEL
    int* _basePowers;
    int* _sumPowers;
    int* _diffPowers;
    int _length;

    float* _onsets;
    int _onsetsLength;

    int _interestStart;
    int _interestEnd;

    int _maxoscilation;

    bool _featuredModePrepared;
};


#endif // FEATURE_H
