package com.example.wtz.learntosing.media;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class CSetEQ {

	/**
	 * @param args
	 */
	public String readFileName        =  "D:/a.pcm";							//读取文件资源的路径
	public String outPutFileName      =  "D:/hu2.pcm";							//写入保存文件资源的路径
	public int EQ_MAX_BANDS           = 10;									 //最多设置的频点个数
	public int EQ_COUNT_BANDS         = 10;									 //要设置的频点个数
	public int EQ_CHANNELS            = 1;										//通道数量
	public double M_SQRT2             = 1.41421356237309504880;				//根号2的值
	public double GAIN_F0             = 1.0;				                    //f0增益
	public double GAIN_F1             = GAIN_F0 / M_SQRT2;				        //f1增益
	float gain[][] 					   = new float[EQ_MAX_BANDS][EQ_CHANNELS];  //EQ的设置
	float gain_raw_value[][] 	       = new float[EQ_MAX_BANDS][EQ_CHANNELS];  //记录声音增益值
	float g_preamp[] 				   = new float[EQ_CHANNELS];				//增益的设置
	public double  beta []            = new double[10];						//存储各个频点的beta值
	public double  alpha []           = new double[10];						//存储各个频点的alpha值
	public double  gamma []           = new double[10];						//存储各个频点的gamma值
	public double dither[]            = new double[256];						//随机噪声添加
	public double x[][]               = new double[10][3];						//记录当前data值
	public double y[][]               = new double[10][3];						//记录当前处理过的data值
	public double x1[][]              = new double[10][3];						//记录二次处理当前data
	public double y1[][]              = new double[10][3];						//记录二次处理过的data值
	public int di                     = 0;										//随机噪声因子
	static int i = 2, j = 1, k = 0;												//处理过程循环因子
	/************************************************************************
	 * @BRIEF :  设置增益(数据的放大倍数)
	 * @param :  chn:通道数量
	val:增益值
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public void set_preamp(int chn, double val)
	{
		g_preamp[chn] = (float)val;// 0   4
	}

	/************************************************************************
	 * @BRIEF :  设置EQ
	 * @param :  index:第几个频点
	chn:通道
	val:EQ经过转换后的值
	rawValue:EQ设的实际的值
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public void set_gain(int index, int chn, double val, float rawValue)
	{
		gain[index][chn] = (float)val;//目前存储的为全0
		gain_raw_value[index][chn] = rawValue;//rawValue 是设置的值20
		System.out.println("val="+val+"rawValue="+rawValue);
	}
	/************************************************************************
	 * @BRIEF :  设置EQ值和增益值的转换
	 * @param :  nBand:可设置EQ的频点数量
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public void set_eq_value(float value, int index, int chn)
	{
	    /* Map the gain and preamp values */
		if (index >= 0)
		{
			/*
			 * EQ的设置
			*/
			set_gain(index, chn, 2.5220207857061455181125E-01 * Math.exp(8.0178361802353992349168E-02 * value) - 2.5220207852836562523180E-01 , value);
		}
		else
		{
	        /*
			 *增益的设置
			*/
			set_preamp(chn, 9.9999946497217584440165E-01 * Math.exp(6.9314738656671842642609E-02 * value) + 3.7119444716771825623636E-07);
		}
	}
	/************************************************************************
	 * @BRIEF :  改动一个频点的EQ是使左右两边的频点EQ跟着变换，该函数是查找影
	响频点的范围
	 * @param :  f0：设置的频点值  
	octave：音阶值(计算音响f1~f2的带宽)
	f1：左界限
	f2:右界限
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	static void find_f1_and_f2(double f0, double octave_percent, double f1[], double f2[])
	{
		double octave_factor = Math.pow(2.0, octave_percent / 2.0);
		f1[0] = f0/octave_factor;
		f2[0] = f0*octave_factor;
	}
	public double TETA(double f)
	{
		return (2* Math.PI*f/44100.0);
	}
	public double TWOPOWER(double value)
	{
		return value*value;
	}
	public double BETA2(double tf0, double tf)
	{
		return (TWOPOWER(GAIN_F1)*TWOPOWER(Math.cos(tf0))- 2.0 * TWOPOWER(GAIN_F1) * Math.cos(tf) * Math.cos(tf0)+ TWOPOWER(GAIN_F1)- TWOPOWER(GAIN_F0) * TWOPOWER(Math.sin(tf)));
	}
	public double BETA1(double tf0, double tf)
	{
		return (2.0 * TWOPOWER(GAIN_F1) * TWOPOWER(Math.cos(tf))+ TWOPOWER(GAIN_F1) * TWOPOWER(Math.cos(tf0))  - 2.0 * TWOPOWER(GAIN_F1) * Math.cos(tf) * Math.cos(tf0) - TWOPOWER(GAIN_F1) + TWOPOWER(GAIN_F0) * TWOPOWER(Math.sin(tf)));
	}
	public double BETA0(double tf0, double tf)
	{
		return  (0.25 * TWOPOWER(GAIN_F1) * TWOPOWER(Math.cos(tf0))  - 0.5 * TWOPOWER(GAIN_F1) * Math.cos(tf) * Math.cos(tf0) + 0.25 * TWOPOWER(GAIN_F1) - 0.25 * TWOPOWER(GAIN_F0) * TWOPOWER(Math.sin(tf)));
	}


	/************************************************************************
	 * @BRIEF :  求二次多项式的值，求值公式(-b +-  根号下(b平方-4ac) )  /2a
	 * @param :  a：二次多项式a值
	b：二次多项式b值
	c: 二次多项式c值
	x0：存储较小那个根
	 * @Return:  返回是否找到根，没有return -1,有return 0
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public int find_root(double a, double b, double c, double x0[]) {
		double k = c-((b*b)/(4.*a));
		double h = -(b/(2.*a));
		double x1 = 0.;
		if (-(k/a) < 0.)
			return -1;
		x0[0] = h - Math.sqrt(-(k / a));
		x1 = h + Math.sqrt(-(k / a));
		if (x1 < x0[0])
			x0[0] = x1;
		return 0;
	}
	/************************************************************************
	 * @BRIEF :  增加噪音
	 * @param :  none
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public void clean_history()
	{
		int n;
		Random rand=new Random();
		for (n = 0; n < 256; n++)
		{
			dither[n] = 0;//(rand.nextInt() % 4) - 2;
		}
		di = 0;
	}
	/************************************************************************
	 * @BRIEF :  计算所有采样率列表的系数
	 * @param :  none
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public void calc_coeffs()
	{
		int i;
		double f1[]     = new double[1];
		double f2[]     = new double[1];
		double x0[]     = new double[1];
		//不需要设置的频点一定要删掉
		double freqs[]  = new double[]{ 200, 1000, 4000, 7000,12000,13000,14000,15000,16000,17000};//设置EQ的频点值
		double octave[] = new double[]{ 1.0, 1.0, 1.0, 0.4, 1.0, 1.0, 1.0, 1.0, 1.0 ,1.0};		   //设置EQ的Q值，即影响范围的系数
		EQ_COUNT_BANDS = freqs.length;
		for (i=0; i< EQ_COUNT_BANDS; i++)
		{
	      /* Find -3dB frequencies for the center freq 调整中心频率f0的EQ会影响到f0左右两边， *f1为左边(低),*f2为右边(高)*/
			find_f1_and_f2(freqs[i], octave[i], f1, f2);
			System.out.println("第"+(i+1)+"个频点f0="+freqs[i]+"\tL="+f1[0]+"\tR="+f2[0]);
	      /* Find Beta 找到二次根始终返回最小的根*/
			if ( find_root(BETA2(TETA(freqs[i]), TETA(f1[0])), BETA1(TETA(freqs[i]), TETA(f1[0])), BETA0(TETA(freqs[i]), TETA(f1[0])),
					x0) == 0)
			{
				/** Got a solution, now calculate the rest of the factors
				 *Take the smallest root always (find_root returns the smallest one)
				 *
				 * NOTE: The IIR equation is
				 *	y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
				 *  Now the 2 factor has been distributed in the coefficients
				 */
	        /* Now store the coefficients */
				beta[i]  = 2.0 * x0[0];
				alpha[i] = 2.0 * ((0.5 - x0[0])/2.0);
				gamma[i] = 2.0 * ((0.5 + x0[0]) * Math.cos(TETA(freqs[i])));
				//System.out.println("beta="+beta[i]+"  alpha="+alpha[i]+"  gamma="+gamma[i]);
			} else {
	        /* Shouldn't happen */
				beta[i]  = 0.;
				alpha[i] = 0.;
				gamma[i] = 0.;
			}
		}// for i
	}
	/************************************************************************
	 * @BRIEF :  IIR滤波器，这里用了IIR的级联
	 * @param :  data:待处理的数据
	 * @Return:  none
	 * @Author:  huxiaohui
	 * @Date:    2015-07-30
	 * @Company：http://www.zonyek.cn./
	 **************************************************************************/
	public  void iir(short data[])
	{
		
		  /* Indexes for the history arrays
		   * These have to be kept between calls to this function
		   * hence they are static */

		int index, band;
		int tempgint;
		double out[] = new double[EQ_CHANNELS];
		double pcm[] = new double[EQ_CHANNELS];
		/**
		 * IIR filter equation is
		 * y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
		 *
		 * NOTE: The 2 factor was introduced in the coefficients to save
		 * 			a multiplication
		 *
		 * This algorithm cascades two filters to get nice filtering
		 * at the expense of extra CPU cycles
		 *
		 * 16bit, 2 bytes per sample, so divide by two the length of
		 * the buffer (length is in bytes)
		 */
		for (index = 0; index < 256; index++)
		{
			pcm[0] = data[index];
			pcm[0] *= g_preamp[0];
			//pcm[0] += dither[di];
			out[0] = 0.;
		      /* For each band */
			for (band = 0; band < EQ_COUNT_BANDS; band++)
			{
				x[band][i] = pcm[0];
				y[band][i] = (alpha[band] * ( x[band][i]   -  x[band][k])+ gamma[band] * y[band][j]   - beta[band] * y[band][k]);
				out[0] +=  y[band][i]*gain[band][0];
			} /* For each band */
			  
		     
		        /* Filter the sample again */
			for (band = 0; band < EQ_COUNT_BANDS; band++)
			{
				x1[band][i] = out[0];
				y1[band][i] =(alpha[band] * (x1[band][i]  -  x1[band][k])+ gamma[band] * y1[band][j] - beta[band] * y1[band][k]);
				out[0] +=  y1[band][i]*gain[band][0];
			} /* For each band */
			out[0] += pcm[0]*0.25;
		      /* remove random noise */
			//out[0] -= dither[di]*0.25;
		      /* Round and convert to integer */
			tempgint = (int)out[0];
		      /* Limit the output */
			if (tempgint < -32768)
				data[index] = -32768;
			else if (tempgint > 32767)
				data[index] = 32767;
			else
				data[index] = (short) tempgint;
		   
		    
		    /* Wrap around the indexes 环绕索引*/
			i = (i+1)%3;
			j = (j+1)%3;
			k = (k+1)%3;
		    /* random noise index */
			//di = (di + 1) % 256;

		}/* For each pair of samples */

	}


	public void init_equliazer(int nBand)
	{ 
		/*
		 *设置增益倍数 20是原来声音的大小，这里一定要设置，否则文件全为0
		*/
		set_eq_value(20.0f , -3 , 0) ;


		/**
		 *
		 * 设置EQ 第一个参数为 dB,第二个参数为 频率索引(请参照索引列表),第三个参数为索引列表
		 Q值 = 1.0；
		 1——1dB, 2——2dB,3——3dB,4——3dB,
		 5——4dB, 6——5dB,7——5dB,8——6dB,
		 9——7dB,10——8dB,11——9dB,12——10dB,
		 13——11dB,14——12dB

		 -1 ——0dB,-2 —— -1dB,-3—— -2dB,-4—— -2dB,
		 -5 —— -3dB,-6 —— -3dB,-7 —— -4dB,-8 —— -4dB,-9 —— -5dB,
		 -10 —— -5dB,-11 —— -6dB,-12 —— -6dB,-13 —— -7dB,-14 —— -7dB,
		 -15 —— -7dB,-16 —— -7dB,-17—— -8dB,-18—— -8dB,-19—— -8dB,
		 -20—— -8dB,-21—— -9dB,-22—— -9dB,-23—— -9dB,-24—— -9dB,-25—— -9dB,
		 -26—— -10dB,-27—— -10dB,-28—— -10dB,-29—— -10dB,-30—— -10dB,-31—— -10dB,
		 -32—— -10dB,-33—— -10dB,-34—— -10dB,-35—— -11dB,-36—— -11dB,-37—— -11dB,
		 -38—— -11dB,-39—— -11dB,-40—— -11dB,-41—— -11dB,-42—— -11dB,-43—— -11dB,
		 double freqs[]  = new double[]{200, 1000, 4000, 7000};//设置EQ的频点值
		 double octave[] = new double[]{ 3.0,1.0, 1.0, 1.0, 0.4, 1.0, 1.0, 1.0, 1.0, 1.0 };		   //设置EQ的Q值，即影响范围的系数
		 */
		//注意设置的个数要与freqs个数一样
		set_eq_value(6.0f , 0, 0) ;
		set_eq_value(9.0f , 1, 0) ;
		set_eq_value(2.0f , 2, 0) ;
		set_eq_value(3.0f , 3, 0) ;

		calc_coeffs();			//计算系数表
		clean_history();
	}


	public static void main(String[] args) throws IOException {
		CSetEQ setEQ = new CSetEQ();   //定义一个设置EQ的对象
		setEQ.init_equliazer(10);            //初始化EQ，个数为10个频点
		InputStream in = new FileInputStream(setEQ.readFileName);;
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(setEQ.outPutFileName)));
		int byteread = 0;                //每次读取个数
		byte[] tempbytes = new byte[32]; // 每次读取32个字节
		short[] dataToSet = new short[16];

		System.out.println("=====================开始处理PCM==========================");
		try {
			while((byteread = in.read(tempbytes))!= -1)
			{

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dos.close();
		in.close();
		System.out.println("=====================处理完成==========================");

	}

}

