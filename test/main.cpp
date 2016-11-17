/*#include <QCoreApplication>

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    
    return a.exec();
}
*/

 /*
         scan: Estimate length (sample count) of a mpeg file and compare to length from exact scan.

         copyright 2007 by the mpg123 project - free software under the terms of the LGPL 2.1
         see COPYING and AUTHORS files in distribution or http://mpg123.org
         initially written by Thomas Orgis
 */

 /* Note the lack of error checking here.
    While it would be nicer to inform the user about troubles, libmpg123 is designed _not_ to bite you on operations with invalid handles , etc.
   You just jet invalid results on invalid operations... */
#include <QApplication>
//#include <mpg123.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <QGraphicsScene>
#include <QGraphicsScene>
#include <QGraphicsView>
#include <QDebug>
#include <math.h>
#include "filereader.h"
#include "../mir/spectrum.h"
#include "../mir/processor.h"
#include "../mir/config.h"
#include "../mir/feature.h"
#include "../mir/axes.h"

 int main(int argc, char **argv)
 {
     QApplication a(argc, argv);

     int indexdirectory =  2;
     int index = 1;
     QString indexStr = QString::number(index,10);
     QString indexDirectoryString = QString::number(indexdirectory,10);
//     QString s = QString::number(a, 10);
      QString str="/Users/yangfan/Projects/nightingle/data_yangfan_"+indexDirectoryString +"/"+indexStr+"lianxi.txt";

      float onsets0[11] = {0.124137931, 0.1586206896,0.1931034482, 0.25517224137,
                           0.3172413793, 0.3827586206, 0.4448275862, 0.4689655172,
                           0.5103448275, 0.5448275862,0.5862};
      float onsets1[9] = {0.0068359, 0.0390625, 0.0859375, 0.20214843,
                          0.2490234, 0.327148, 0.3662109, 0.392578,
                          0.455};
      float onsets2[6] = {0.01953125, 0.0716145833, 0.1328125, 0.1861979, 0.2486979, 0.52};
      float onsets3[7] = {0.09765625, 0.1396484375, 0.1708984375, 0.224609375, 0.258789, 0.3046875, 0.45};
      float onsets4[9] = {0.09765625, 0.146484375, 0.2197265625, 0.2587890625, 0.30761718, 0.3759765625, 0.419921875, 0.498, 0.5859375};
      float onsets5[9] = {0.0611979166, 0.104166667, 0.1627604166, 0.208333333, 0.2604166667, 0.3125, 0.37109375, 0.4296875, 0.5859375};
      float onsets6[9] = {0.05208333, 0.078125, 0.104166666, 0.1334635416, 0.3255208333, 0.416666, 0.5078125, 0.546875, 0.7486979166};
      float onsets7[10] = {0.068359375, 0.1025390625, 0.154296875, 0.234375, 0.359375, 0.3955078125, 0.4296875, 0.46875, 0.537109375, 0.615234375};
      float onsets8[6] = {0.0104166666, 0.1432291666, 0.2083333333, 0.2526, 0.2968749, 0.3854166};
      float onsets9[7] = {0.0390625, 0.130208333, 0.1953125, 0.26041666, 0.325520833, 0.390625, 0.48};
      float onsets10[10] = {0.0390625, 0.078125, 0.1123046875, 0.15625, 0.2294921875, 0.2734375, 0.3125, 0.390625, 0.4296875, 0.5371};
      float onsets11[10] = {0.068359375, 0.107421875, 0.146484375, 0.185546875, 0.29296875, 0.341796875, 0.41015625, 0.458984375, 0.537109375, 0.5859375};

      int dataReturn0[11] ={0};
      int dataReturn1[9] ={0};
      int dataReturn2[6] ={0};
      int dataReturn3[7] ={0};
      int dataReturn4[9] ={0};
      int dataReturn5[9] ={0};
      int dataReturn6[9] ={0};
      int dataReturn7[10] ={0};
      int dataReturn8[6] ={0};
      int dataReturn9[7] ={0};
      int dataReturn10[10] ={0};
      int dataReturn11[10] ={0};

      float oscilators0[160] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.264956, 0.317299, 0.373624, 0.388943, 0.3147, 0.428369, 0.435702, 0.439242, 0.43945, 0.436418, 0.439735, 0.466154, 0.462834, 0.474089, 0.509002, 0.520797, 0.507067, 0.507673, 0.512789, 0.509791, 0.510663, 0.518874, 0.506175, 0.502169, 0.512679, 0.501558, 0.499182, 0.500451, 0.492937, 0, 0, 0.417374, 0.443052, 0.43606, 0.435343, 0.449442, 0.446794, 0.440211, 0, 0, 0, 0.508458, 0.508838, 0.490868, 0.500984, 0.509091, 0.496131, 0.497667, 0.505129, 0.485539, 0.438143, 0.428869, 0.445804, 0.436734, 0.430731, 0.408824, 0.370385, 0.382002, 0.383368, 0.36478, 0, 0, 0.333995, 0.346911, 0.340953, 0.329229, 0.2429, 0.268009, 0.305328, 0.298083, 0.266296, 0.305272, 0.307466, 0.28498, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.198077, 0.388679, 0.347402, 0.339925, 0.326795, 0.23566, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators1[128] = {0, 0.216875, 0.223745, 0.22495, 0, 0.345503, 0.368312, 0.359858, 0.322857, 0, 0, 0.274338, 0.307863, 0.274125, 0.273462, 0.318036, 0.279621, 0.274319, 0.30825, 0.319217, 0.279236, 0.307007, 0.321136, 0.311354, 0, 0, 0.361514, 0.325628, 0.324645, 0.324755, 0, 0, 0.374416, 0.372075, 0.374078, 0.417291, 0.420501, 0.375699, 0.416901, 0.416491, 0.328881, 0, 0.366431, 0.361129, 0.327107, 0.322233, 0.274707, 0.274327, 0.31064, 0.318133, 0.292379, 0, 0, 0.374128, 0.324319, 0.323462, 0.370258, 0.322857, 0.353565, 0.374175, 0.352622, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.318093, 0, 0, 0, 0.372296, 0.32151, 0.323777, 0.371362, 0.322624, 0.359729, 0.374031, 0.34872, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators2[96] = {0, 0, 0.230567, 0.263529, 0.333621, 0.385849, 0.379162, 0.355527, 0.352958, 0.349968, 0, 0, 0, 0.312126, 0.306437, 0.287422, 0, 0, 0.378261, 0.362327, 0.360105, 0, 0, 0, 0.313062, 0.312844, 0.304617, 0.300628, 0.310087, 0.303849, 0.298445, 0.306359, 0.308468, 0.305575, 0.297435, 0.302548, 0.306957, 0.301028, 0.304709, 0.314852, 0.308239, 0.305505, 0.315315, 0.308217, 0.29802, 0.314386, 0.316695, 0.302132, 0.313483, 0.310852, 0.303879, 0.311173, 0.304788, 0, 0.322892, 0.305483, 0.296962, 0.303003, 0.306974, 0.300804, 0.306582, 0.315118, 0.308333, 0.305997, 0.315682, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators3[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.207143, 0.213957, 0.21696, 0, 0, 0.363529, 0.423842, 0.423985, 0, 0.418182, 0.410047, 0.418902, 0.403255, 0, 0, 0, 0.372391, 0.374707, 0.3308, 0, 0.409744, 0.380292, 0.403254, 0.376688, 0, 0, 0.354174, 0.36693, 0.360531, 0.351401, 0.371698, 0.372581, 0.361837, 0.354342, 0.372233, 0.37318, 0.360061, 0.367005, 0.370674, 0.356771, 0.361055, 0.370771, 0.373416, 0.374416, 0.360732, 0.364933, 0.373996, 0.361501, 0.341624, 0.374755, 0.374853, 0, 0, 0.374755, 0, 0, 0, 0, 0.359062, 0.367825, 0.370067, 0.356477, 0.361975, 0.370682, 0.373558, 0.374175, 0.360742, 0.365576, 0.37426, 0.359701, 0.342063, 0.374755, 0.374707, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators4[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.309225, 0.274128, 0.272064, 0.273842, 0.246512, 0.275261, 0.408403, 0.522937, 0.523225, 0.565609, 0.572996, 0.52616, 0.574853, 0.512314, 0, 0.479714, 0.513206, 0.520113, 0.509098, 0.421916, 0.421281, 0.423643, 0.423791, 0.424609, 0.422996, 0, 0.551964, 0.516202, 0.472053, 0.471495, 0.51046, 0.470092, 0.509487, 0.47542, 0.469669, 0.513356, 0.562337, 0.570333, 0.572148, 0.520356, 0, 0.509147, 0.474839, 0.474755, 0.473462, 0.510993, 0.500172, 0.472201, 0.418147, 0.316065, 0, 0.272762, 0.323842, 0.302326, 0.310707, 0.323558, 0.319455, 0.280119, 0.324128, 0.307583, 0.319835, 0.324861, 0, 0.50642, 0.475213, 0.474755, 0.473136, 0.51295, 0.476949, 0.472486, 0.415889, 0.305846, 0, 0.271899, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators5[96] = {0, 0, 0, 0, 0, 0, 0.424853, 0.424755, 0.416289, 0, 0.328041, 0.324561, 0.358218, 0.360191, 0, 0.373608, 0.414576, 0.42026, 0.420802, 0.420539, 0.40818, 0.423136, 0.424755, 0.37935, 0, 0.566457, 0.663689, 0.723319, 0.769593, 0.763875, 0.621831, 0.667349, 0.67026, 0.572597, 0, 0, 0.4112, 0.424561, 0.418426, 0.422745, 0, 0.523649, 0.566261, 0.548205, 0.574561, 0.574755, 0.569617, 0.571495, 0.573321, 0.573225, 0.573276, 0.573225, 0.571764, 0.560743, 0.527727, 0.574853, 0.568247, 0, 0, 0, 0.416463, 0.421402, 0, 0.523837, 0.566522, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators6[192] ={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.564505, 0.574272, 0.561951, 0.519112, 0, 0.473507, 0.506131, 0.517496, 0.458104, 0, 0.560748, 0.572981, 0.572034, 0.517294, 0, 0, 0.561006, 0.558168, 0.569048, 0.573417, 0.580825, 0.575299, 0.578947, 0.600798, 0.594556, 0.59657, 0.610609, 0.575, 0.579762, 0.575153, 0.610332, 0.61819, 0.599896, 0.624416, 0.613561, 0.608409, 0.608052, 0.609874, 0.607523, 0.573321, 0.601116, 0.580556, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.284507, 0.368068, 0.419273, 0.472117, 0.469455, 0.474853, 0.499383, 0.500355, 0.488497, 0.510664, 0.524074, 0.524707, 0.518699, 0.52048, 0.504246, 0.410565, 0, 0.568875, 0.548276, 0.530263, 0.570268, 0.572306, 0.573218, 0.573791, 0.574272, 0.574031, 0.573558, 0.574464, 0.572719, 0.572117, 0.574755, 0.574561, 0.573321, 0.568616, 0.574755, 0.619495, 0.624078, 0.617921, 0.622624, 0.620779, 0.572762, 0.460589, 0.460447, 0.516507, 0.524755, 0.523992, 0.543862, 0.568087, 0.566332, 0.565365, 0.572175, 0.572899, 0.570273, 0.574755, 0.585279, 0.573124, 0.571814, 0.574853, 0.574252, 0.574755, 0.574853, 0.574755, 0.574755, 0.574416, 0.574853, 0.574755, 0.574462, 0.574031, 0.574853, 0.574755, 0.574853, 0.574464, 0.574272, 0.574755, 0.574755, 0.574755, 0.574755, 0.574853, 0.574853, 0.574707, 0.574853, 0.572306, 0.574272, 0.574272, 0.573558, 0.550448, 0, 0.574755, 0.574012, 0.574464, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators7[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0.397448, 0.423417, 0.424272, 0.424755, 0.428984, 0.474853, 0.504732, 0.521188, 0.497566, 0, 0, 0.670779, 0.668592, 0.671751, 0.670313, 0.611246, 0.572571, 0.574272, 0.573699, 0.486392, 0, 0.525214, 0.573558, 0.569171, 0.569726, 0.574707, 0.569419, 0.574464, 0.574853, 0.574755, 0, 0, 0, 0, 0, 0, 0, 0.514322, 0.504896, 0.512325, 0.566067, 0.574561, 0.555746, 0.55903, 0.57318, 0, 0.616233, 0.622528, 0.62178, 0.623603, 0.617105, 0.668247, 0.672658, 0.673084, 0.632476, 0, 0, 0.719414, 0.727304, 0.660193, 0.511084, 0.472306, 0.520353, 0.518182, 0.487639, 0.524319, 0.524416, 0.515201, 0.521495, 0.524031, 0.521136, 0, 0.609789, 0.669329, 0.673786, 0.673225, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators8[96] = {0, 0.476451, 0.472486, 0.474755, 0.472433, 0.477009, 0.473791, 0.518339, 0.521442, 0.514897, 0.510589, 0.523985, 0.464261, 0, 0.524078, 0.56828, 0.613689, 0.556955, 0, 0, 0.574027, 0.573558, 0.57318, 0.526968, 0.507489, 0.510047, 0.519252, 0.477056, 0.417108, 0.371097, 0.42672, 0.42582, 0.424853, 0.468221, 0.426737, 0.446123, 0.473077, 0.628655, 0.611731, 0.553198, 0, 0, 0.572841, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators9[96] = {0, 0, 0, 0, 0.300636, 0.275328, 0.274853, 0.274269, 0.271079, 0.271949, 0.270221, 0.271495, 0.271899, 0.271792, 0.36708, 0.467491, 0.512164, 0, 0, 0.428604, 0.469238, 0.46231, 0.40336, 0, 0, 0.42304, 0.423314, 0.423603, 0.422306, 0.422667, 0.45894, 0.423888, 0.424853, 0.422805, 0.367229, 0.32304, 0.373552, 0.372212, 0.32217, 0.322667, 0.373136, 0.351166, 0.37024, 0.374853, 0.324416, 0.371136, 0.373699, 0, 0, 0, 0.377241, 0.422667, 0.458451, 0.424416, 0.424755, 0.421846, 0.362855, 0.322805, 0.374222, 0.37167, 0.322581, 0.322893, 0.373462, 0.345316, 0.371215, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators10[128] = {0, 0, 0, 0, 0, 0.407981, 0.44217, 0.471402, 0.37035, 0.31688, 0.400445, 0.416381, 0.405344, 0.414059, 0.374128, 0.364711, 0.374853, 0.374755, 0.339056, 0, 0.406391, 0.402391, 0.389444, 0.424416, 0.424416, 0.412588, 0.424175, 0.423558, 0.424707, 0.456607, 0.546471, 0.598237, 0.600566, 0.570896, 0, 0, 0.572243, 0.573694, 0.573507, 0.521751, 0.524755, 0.524175, 0.503212, 0.478226, 0.46088, 0.374561, 0.406532, 0.423417, 0.379371, 0, 0.416174, 0.418009, 0.413103, 0.412293, 0.420092, 0.424128, 0.43601, 0.427925, 0.424755, 0.426299, 0.442255, 0.426915, 0.426569, 0.435332, 0.43951, 0.423985, 0.437469, 0.444647, 0.424755, 0.426078, 0.439309, 0.426046, 0, 0.421942, 0.412297, 0.411456, 0.420741, 0.423985, 0.436104, 0.42703, 0.424755, 0.426972, 0.440544, 0.426082, 0.426354, 0.437398, 0.436432, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      float oscilators11[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0.457319, 0.431051, 0.425402, 0.415351, 0, 0.510656, 0.493432, 0.484557, 0.442568, 0, 0.574416, 0.624464, 0.615558, 0.571811, 0, 0.622762, 0.670203, 0.673365, 0.687909, 0.742099, 0.774128, 0.769945, 0.772832, 0.772457, 0.764118, 0, 0, 0, 0, 0.623546, 0.623842, 0.623985, 0.617742, 0, 0, 0.615511, 0.620295, 0.603869, 0.624561, 0.623462, 0.623985, 0.622117, 0.624853, 0, 0.668739, 0.672106, 0.673136, 0.674561, 0, 0, 0.52419, 0.502861, 0.515365, 0.579502, 0.581005, 0.577681, 0.584496, 0.567355, 0, 0, 0.524755, 0.513067, 0.497664, 0.51276, 0.486806, 0.495833, 0, 0, 0, 0, 0, 0, 0.522788, 0.499564, 0.517967, 0.583499, 0.580602, 0.577489, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

      int powers0[160] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            137, 136, 129, 133, 104, 160, 130, 132, 130, 131,
                            130, 135, 135, 129, 143, 140, 155, 103, 135, 160,
                            104, 136, 167, 134, 134, 117, 131, 128, 130, 0,
                            0, 129, 132, 135, 132, 132, 129, 136, 0, 0,
                            0, 109, 159, 135, 134, 103, 131, 132, 108, 136,
                            131, 141, 132, 130, 128, 162, 128, 128, 129, 72,
                            0, 0, 127, 129, 131, 136, 109, 154, 130, 114,
                            131, 128, 151, 64, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 111, 103, 130, 132, 136, 114, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      int powers1[128] = {0, 140, 129, 126, 0, 94, 139, 158, 131, 0, 0, 122, 163, 128, 130, 140, 105, 128, 150, 137, 104, 144, 134, 114, 0, 0, 155, 119, 123, 127, 0, 0, 128, 132, 128, 140, 134, 116, 142, 142, 109, 0, 141, 155, 109, 131, 128, 120, 156, 139, 67, 0, 0, 129, 119, 130, 135, 131, 140, 128, 138, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 140, 0, 0, 0, 131, 130, 127, 134, 131, 147, 129, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers2[96] = {0, 0, 138, 150, 134, 141, 131, 168, 111, 132, 0, 0, 0, 149, 162, 131, 0, 0, 136, 152, 155, 0, 0, 0, 144, 134, 110, 112, 137, 104, 111, 132, 144, 149, 103, 114, 143, 128, 100, 133, 146, 151, 131, 140, 114, 133, 129, 102, 130, 123, 102, 128, 85, 0, 158, 148, 104, 116, 143, 128, 106, 133, 146, 150, 131, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers3[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 78, 81, 99, 0, 0, 148, 129, 129, 0, 140, 158, 123, 149, 0, 0, 0, 131, 128, 62, 0, 156, 102, 115, 118, 0, 0, 143, 142, 146, 116, 132, 131, 122, 95, 128, 130, 123, 111, 111, 96, 99, 116, 130, 128, 99, 93, 124, 103, 49, 127, 127, 0, 0, 127, 0, 0, 0, 0, 120, 111, 111, 96, 101, 117, 130, 128, 97, 95, 126, 100, 47, 127, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers4[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 129, 132, 129, 96, 119, 150, 130, 130, 145, 131, 118, 127, 151, 0, 104, 150, 133, 158, 131, 132, 129, 129, 128, 131, 0, 165, 143, 131, 133, 157, 136, 160, 119, 136, 149, 153, 135, 131, 133, 0, 140, 116, 127, 130, 141, 145, 131, 129, 131, 0, 131, 129, 118, 120, 130, 137, 105, 129, 105, 136, 90, 0, 165, 117, 127, 130, 145, 112, 131, 144, 119, 0, 133, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
      int powers5[96] = {0, 0, 0, 0, 0, 0, 127, 127, 143, 0, 111, 128, 162, 157, 0, 103, 147, 134, 134, 134, 163, 130, 127, 107, 0, 139, 149, 115, 135, 147, 124, 140, 134, 122, 0, 0, 156, 128, 139, 130, 0, 129, 143, 139, 128, 127, 137, 133, 130, 130, 130, 130, 133, 154, 110, 127, 139, 0, 0, 0, 143, 132, 0, 129, 143, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers6[192] ={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 146, 128, 153, 138, 0, 129, 118, 140, 81, 0, 157, 130, 132, 139, 0, 0, 131, 101, 110, 130, 103, 125, 109, 125, 112, 120, 110, 124, 105, 122, 135, 139, 120, 128, 150, 138, 163, 158, 164, 130, 123, 103, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 106, 139, 137, 132, 137, 127, 121, 105, 109, 124, 128, 128, 123, 135, 117, 84, 0, 137, 123, 104, 130, 132, 129, 129, 128, 129, 130, 128, 131, 132, 127, 128, 130, 104, 127, 123, 128, 139, 131, 134, 131, 157, 156, 130, 127, 124, 83, 120, 99, 99, 117, 119, 109, 127, 94, 123, 115, 127, 125, 127, 127, 127, 127, 128, 127, 127, 127, 129, 127, 127, 127, 128, 128, 127, 127, 127, 127, 127, 127, 128, 127, 132, 128, 128, 130, 167, 0, 127, 126, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers7[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 132, 130, 128, 127, 108, 127, 121, 126, 133, 0, 0, 134, 138, 132, 136, 154, 131, 128, 129, 79, 0, 117, 130, 129, 118, 128, 137, 128, 127, 127, 0, 0, 0, 0, 0, 0, 0, 147, 84, 89, 104, 128, 102, 92, 130, 0, 144, 131, 132, 129, 142, 139, 130, 130, 77, 0, 0, 136, 108, 155, 152, 132, 134, 140, 112, 128, 128, 99, 133, 129, 134, 0, 118, 137, 128, 130, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers8[96] = {0, 112, 131, 127, 131, 112, 129, 137, 133, 146, 157, 129, 147, 0, 128, 139, 149, 161, 0, 0, 128, 130, 130, 108, 165, 159, 137, 115, 141, 134, 109, 122, 127, 115, 118, 125, 130, 85, 154, 172, 0, 0, 130, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers9[96] = {0, 0, 0, 0, 137, 114, 127, 128, 122, 127, 136, 133, 133, 132, 141, 141, 149, 0, 0, 111, 137, 148, 126, 0, 0, 130, 129, 129, 132, 131, 158, 129, 127, 131, 140, 130, 129, 132, 132, 131, 130, 107, 135, 127, 128, 134, 129, 0, 0, 0, 108, 131, 159, 128, 127, 132, 149, 131, 128, 133, 131, 130, 130, 106, 133, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers10[128] = {0, 0, 0, 0, 0, 106, 91, 133, 135, 58, 112, 102, 98, 118, 129, 147, 127, 127, 116, 0, 166, 120, 90, 128, 128, 106, 128, 130, 128, 166, 127, 120, 132, 134, 0, 0, 131, 129, 129, 132, 127, 128, 89, 108, 156, 128, 111, 130, 107, 0, 143, 111, 108, 105, 136, 129, 96, 113, 127, 120, 92, 117, 119, 98, 91, 129, 100, 95, 127, 121, 115, 119, 0, 128, 107, 104, 135, 129, 96, 117, 127, 117, 96, 121, 120, 92, 99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int powers11[128] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 131, 102, 124, 114, 0, 137, 93, 98, 74, 0, 117, 128, 127, 121, 0, 131, 135, 130, 99, 101, 129, 136, 129, 130, 148, 0, 0, 0, 0, 129, 129, 129, 139, 0, 0, 144, 135, 126, 128, 130, 129, 132, 127, 0, 138, 131, 130, 128, 0, 0, 123, 91, 99, 105, 102, 114, 96, 121, 0, 0, 127, 93, 80, 96, 90, 93, 0, 0, 0, 0, 0, 0, 130, 86, 105, 100, 103, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

      int onsetSize[12] = {11,9,6,7,
                           9,9,9,10,
                           6,7,10,10};

      float *onsets[12];
      onsets[0] = onsets0;
      onsets[1] = onsets1;
      onsets[2] = onsets2;
      onsets[3] = onsets3;
      onsets[4] = onsets4;
      onsets[5] = onsets5;
      onsets[6] = onsets6;
      onsets[7] = onsets7;
      onsets[8] = onsets8;
      onsets[9] = onsets9;
      onsets[10] = onsets10;
      onsets[11] = onsets11;

      int featureSize[12] = {160,128,96,128,
                            128,96,192,128,
                            96,96,128,128};

      float *oscilators[12];
      oscilators[0] = oscilators0;
      oscilators[1] = oscilators1;
      oscilators[2] = oscilators2;
      oscilators[3] = oscilators3;
      oscilators[4] = oscilators4;
      oscilators[5] = oscilators5;
      oscilators[6] = oscilators6;
      oscilators[7] = oscilators7;
      oscilators[8] = oscilators8;
      oscilators[9] = oscilators9;
      oscilators[10] = oscilators10;
      oscilators[11] = oscilators11;


      int *powers[12];
      powers[0] = powers0;
      powers[1] = powers1;
      powers[2] = powers2;
      powers[3] = powers3;
      powers[4] = powers4;
      powers[5] = powers5;
      powers[6] = powers6;
      powers[7] = powers7;
      powers[8] = powers8;
      powers[9] = powers9;
      powers[10] = powers10;
      powers[11] = powers11;

      int *dataResult[12];
      dataResult[0] = dataReturn0;
      dataResult[1] = dataReturn1;
      dataResult[2] = dataReturn2;
      dataResult[3] = dataReturn3;
      dataResult[4] = dataReturn4;
      dataResult[5] = dataReturn5;
      dataResult[6] = dataReturn6;
      dataResult[7] = dataReturn7;
      dataResult[8] = dataReturn8;
      dataResult[9] = dataReturn9;
      dataResult[10] = dataReturn10;
      dataResult[11] = dataReturn11;


      FileReader* fileReader = new FileReader();

      fileReader->readFile(str);

//      int maxdata = 0;

//      for (int i= 0; i < fileReader->_dataLength; i++) {
//          qDebug()<<"wavedata->i"<<i<<" "<<fileReader->_data[i];
//          if (fileReader->_data[i] > maxdata) {
//              maxdata = fileReader->_data[i];
//          }
//      }
//      qDebug()<<"wavemax"<<maxdata;

      Processor* processor = new Processor();

      int* result = NULL;
      result = (int*)malloc(sizeof(int) * onsetSize[index]);
      memset(result, 0,sizeof(int)* onsetSize[index]);
//      processor->process(1)
      processor->process(featureSize[index],oscilators[index],powers[index],onsetSize[index],onsets[index],fileReader->_dataLength,fileReader->_data,result);

      for (int i = 0; i < onsetSize[index]; i++) {
          std::cout<<i<<"=>value"<<result[i]<<std::endl;
      }
//     spectrum->spectrumlize(fileReader->_data, fileReader->_dataLength);

      Spectrum* spectrum = processor->getSpectrumSample();

      Feature* feature = processor->getFeatureSample();

//      Feature* featureStd =processor->getFeatureStd();

      int sceneWidth = spectrum->_dataSpectrumLength / (spectrum->getWindowSize() / 2);

      QGraphicsScene *scene = new QGraphicsScene;

      scene->setSceneRect(0,0,sceneWidth * 8,spectrum->getWindowSize() / 2 * 4);

      int powerDiv = POWERDIV;
      for(int i = 0; i < sceneWidth; i++) {
          for (int j = 0; j < spectrum->getWindowSize() / 2; j ++) {
              int halfwindow =   spectrum->getWindowSize() / 2;
              int power = spectrum->_dataSpectrum[i * halfwindow + j] / (float)(spectrum->getMaxAbsFft()/powerDiv/powerDiv);

              if (power> 254)
                  power = 254;
              if (power < 0)
                  power = 254;
//              qDebug()<<"power"<<spectrum->_dataSpectrum[i * halfwindow + j];
              QColor color = QColor(power,power,power);
              scene->addLine(i * 8,j * 4,8 * i+7,j * 4,QPen(color));
              scene->addLine(i * 8,j * 4 + 1,8 * i+7,j * 4 + 1,QPen(color));
              scene->addLine(i * 8,j * 4 + 2,8 * i+7,j * 4 + 2,QPen(color));
              scene->addLine(i * 8,j * 4 + 3,8 * i+7,j * 4 + 3,QPen(color));
          }
      }
//      scene->addLine(0,0,500,750);
      for(int i = 0; i < feature->getLength();i++) {

          int maxy = MAXINTERESTOSCILATIONPIXEL;

          float* oscilators = feature->getBaseOscilators();

//          std::cout<<" "<<oscilators[i];

          int y = (int) (oscilators[i] * (float)maxy * (float)4  + 2);

          int* powers = feature->getBasePowers();

          int redPower = powers[i];

          if (redPower > 254)
              redPower = 254;

          scene->addLine(i * 8,y,8 * i+7,y,QPen(QColor(redPower,0,0)));

          int* powerHigh = feature->getSumPowers();

//          std::cout<<"("<<i<<"->"<<powerHigh[i]<<")"<<std::endl;

//          float* oscilatorsStd = featureStd->getBaseOscilators();

//          int yStd = (int)(oscilatorsStd[i] * (float)maxy * (float)4 + 2);

//          int* powersStd = featureStd->getBasePowers();

//          int redPowerStd = powersStd[i];

//          scene->addLine(yStd,i*8,yStd,i*8+7,QPen(QColor(redPowerStd,0,0)));
      }

      std::cout<<std::endl;

      for(int i = 0; i < feature->getLength(); i++) {

          int* diffPower = feature->getDiffPowers();

//          std::cout<<" "<<diffPower[i];
//          std::cout<<"("<<i<<"->"<<diffPower[i]<<")"<<std::endl;
      }

      std::cout<<std::endl;

//      Axes* axes = processor->getAxes();

//      for (int i = 0; i < axes->getX(); i++) {

//          for (int j =0; j < axes->getY(); j++) {

//              int* data = axes->getData();

//              int power = data[i * axes->getY() + j];

////              std::cout<<power<<" ";

//              QColor color = QColor(power,power,power);

//              scene->addLine(i * 4,j * 4,    4 * i+3,j * 4,QPen(color));
//              scene->addLine(i * 4,j * 4 + 1,4 * i+3,j * 4 + 1,QPen(color));
//              scene->addLine(i * 4,j * 4 + 2,4 * i+3,j * 4 + 2,QPen(color));
//              scene->addLine(i * 4,j * 4 + 3,4 * i+3,j * 4 + 3,QPen(color));
//          }
////          std::cout<<std::endl;
//      }

//      Feature* featureStd = processor->getFeatureStd();
//      int onsetsLengthStd = featureStd->getOnsetsLength();
//      int stdDataLength = featureStd->getLength();
//      float* onsetsStd = featureStd->getOnsets();
//      for (int i = 0; i < onsetsLengthStd; i++) {
//          float data = onsetsStd[i];
//          std::cout<<"<"<<i<<","<<data<<">"<<std::endl;
//      }

//      std::cout<<std::endl;

//      Feature* featureSample = processor->getFeatureSample();
//      int dataLength = featureSample->getLength();
////      float stdVsSample = fabs(onsetsStd[onsetsLengthStd - 1]*stdDataLength - onsetsStd[0]*stdDataLength) / fabs(featureSample->getInterestStart()-featureSample->getInterestEnd());



//      int onsetsLengthSample = featureSample->getOnsetsLength();
//      float* onsetsSample = featureSample->getOnsets();
//      float stdMinusSampleOnset = onsetsStd[0] - onsetsSample[0];
//      float stdVsSample = fabs(onsetsStd[onsetsLengthStd - 1] - onsetsStd[0]) / fabs(onsetsSample[onsetsLengthSample -1] - onsetsSample[0]);
//      for (int i = 0; i < onsetsLengthSample; i++) {
////          float data = float(onsetsSample[i]);
////          float data = onsetsSample[i];
//          float data = (onsetsSample[i] - onsetsSample[0]) * stdVsSample + onsetsSample[0] + stdMinusSampleOnset;
//          std::cout<<"<"<<i<<","<<data<<">"<<std::endl;
//      }

      free(result);
      QGraphicsView *view =  new QGraphicsView(scene);

//      view->resize(100, 100);

      view->setWindowTitle("Graphics View");

      view->show();

      return a.exec();
 }

