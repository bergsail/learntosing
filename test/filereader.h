#ifndef FILEREADER_H
#define FILEREADER_H

#include <QVector>
class QString;

class FileReader
{
public:
    FileReader();
    ~FileReader();

    bool readFile(QString str);

    int* _data;
    long _dataLength;
};

#endif // FILEREADER_H
