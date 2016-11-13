#ifndef AXES_H
#define AXES_H

class Feature;
class Axes
{
public:
    Axes();
    ~Axes();

    bool orthogonalize(Feature* featureStd,Feature* freatureSample,float stdMinusSample, float stdVsSample, int* result);

    int getX();
    int getY();
    int* getData();

private:

    int _axeStdX;
    int _axeSampleY;
    int* _axesData;
};

#endif // AXES_H
