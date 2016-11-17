#include "filereader.h"
#include <QVector>
#include <QString>
#include <QDebug>
#include <QFile>
#include "config.h"
FileReader::FileReader()
{
    _data = NULL;
}

FileReader::~FileReader()
{
    if (_data) {

        free(_data);

        _data = NULL;
    }
}

bool FileReader::readFile(QString str) {

    qDebug()<<str;

    QFile *file=new QFile(str);

    file->open(QIODevice::ReadOnly|QIODevice::Text);

    QString data = QString(file->readAll());

    QStringList numbers = data.split(QRegExp("\t"));

    _dataLength = numbers.size();

    _data = (int*)malloc((sizeof(int) * numbers.size()));

    if (_data!= NULL) {

        for(int i = 0; i < numbers.size(); i++) {

            _data[i] = numbers.at(i).toInt();

        }
        return true;
    } else {
        return false;
    }
}
