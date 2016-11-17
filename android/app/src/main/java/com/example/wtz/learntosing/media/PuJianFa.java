package com.example.wtz.learntosing.media;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class PuJianFa {


	public int DEFAULT_LENGTH         =  1024;
	public int DEFAULT_SHIFT    	  =  256;
	public double DEFAULT_MULTIPLE    =  1.0;
	public double DEFAULT_SMOOTHING   =  0.95;
	public double PI                  =  3.1415926535897932384626434;
	public String readFileName        =  "D:/zzz.pcm";
	public String outPutFileName      =  "D:/outputfile2.pcm";
	public double DEFAULT_AGCKK       =  1.0;
	/**
	 * @param
	 */
	public  void readFileByBytes(String fileName,String outPutName) {
		File file = new File(fileName);
		InputStream in = null;
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outPutName)));
			try {
				System.out.println("以字节为单位读取文件内容，一次读一个字节：");
				in = new FileInputStream(file);
				int tempbyte;
				while ((tempbyte = in.read()) != -1) {
					//System.out.print(" "+Integer.toHexString(tempbyte));
					dos.writeByte(tempbyte);
				}
				System.out.println("=======over============");
				dos.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	public void test(double[] hh)
	{
		DecimalFormat df=new DecimalFormat("#.00000");
		System.out.println(df.format(hh[2]));

	}

	public  void multirr(int length, short id[], double win[], double frame[])
	{
		int i = 0;
		while(--length >= 0)
		{
			frame[i] = id[i] * win[i];
			i++;
		}
	}
	public  void cfftall(int m0, double x[], double ainv)
	{
		int    i, j, lm, li, k, lmx, lmx2, np, lix;
		double  temp1, temp2;
		double  c, s, csave, sstep, cstep;
		double  c0, s0, c1, s1;

		lmx = 1 << m0;

		csave = PI * 2.0 / lmx;
		cstep = Math.cos(csave);
		sstep = Math.sin(csave);

		lmx += lmx;
		np   = lmx;

	/*----- fft butterfly numeration */
		for (i = 3; i <= m0; ++i) {
			lix = lmx;
			lmx >>= 1;
			lmx2 = lmx >> 1;
			c = cstep;
			s = sstep;
			s0 = ainv * s;
			c1 = -s;
			s1 = ainv * c;
			for (li = 0; li < np; li += lix ) {
				j = li;
				k = j + lmx;
				temp1  = x[j] - x[k];
				x[j]  += x[k];
				x[k]   = temp1;
				temp2  = x[++j] - x[++k];
				x[j]  += x[k];
				x[k]   = temp2;

				temp1  = x[++j] - x[++k];
				x[j]  += x[k];
				temp2  = x[++j] - x[++k];
				x[j]  += x[k];
				x[k-1] = c * temp1 - s0 * temp2;
				x[k]   = s0 * temp1 + c * temp2;

				j = li + lmx2;
				k = j + lmx;
				temp1  = x[j] - x[k];
				x[j]  += x[k];
				temp2  = x[++j] - x[++k];
				x[j]  += x[k];
				x[k-1] = -ainv * temp2;
				x[k]   =  ainv * temp1;

				temp1  = x[++j] - x[++k];
				x[j]  += x[k];
				temp2  = x[++j] - x[++k];
				x[j]  += x[k];
				x[k-1] = c1 * temp1 - s1 * temp2;
				x[k]   = s1 * temp1 + c1 * temp2;

			}
			for (lm = 4; lm < lmx2; lm += 2) {
				csave = c;
				c = cstep * c - sstep * s;
				s = sstep * csave + cstep * s;

				s0 = ainv * s;
				c1 = -s;
				s1 = ainv * c;

				for (li = 0; li < np; li += lix ) {
					j = li + lm;
					k = j + lmx;
					temp1  = x[j] - x[k];
					x[j]  += x[k];
					temp2  = x[++j] - x[++k];
					x[j]  += x[k];
					x[k-1] = c * temp1 - s0 * temp2;
					x[k]   = s0 * temp1 + c * temp2;

					j = li + lm + lmx2;
					k = j + lmx;
					temp1  = x[j] - x[k];
					x[j]  += x[k];
					temp2  = x[++j] - x[++k];
					x[j]  += x[k];
					x[k-1] = c1 * temp1 - s1 * temp2;
					x[k]   = s1 * temp1 + c1 * temp2;
				}
			}
			csave = cstep;
			cstep = 2.0 * cstep * cstep - 1.0;
			sstep = 2.0 * sstep * csave;
		}
		if (m0 >= 2)
			for (li = 0; li < np; li += 8) {
				j = li;
				k = j + 4;
				temp1 = x[j] - x[k];
				x[j] += x[k];
				temp2 = x[++j] - x[++k];
				x[j] += x[k];
				x[k-1] = temp1;
				x[k]   = temp2;
				temp1  = x[++j] - x[++k];
				x[j]  += x[k];
				temp2  = x[++j] - x[++k];
				x[j]  += x[k];
				x[k-1] = -ainv * temp2;
				x[k]   =  ainv * temp1;
			}
		for (li = 0; li < np; li += 4) {
			j = li;
			k = j + 2;
			temp1 = x[j] - x[k];
			x[j]  += x[k];
			x[k]   = temp1;
			temp2  = x[++j] - x[++k];
			x[j]  += x[k];
			x[k]   = temp2;
		}

	/*----- fft bit reversal */
		lmx = np / 2;
		j = 0;
		for (i = 2; i < np - 2; i += 2) {
			k = lmx;
			while(k <= j) {
				j -= k;
				k >>= 1;
			}
			j += k;
			if ( i < j ) {
				temp1 = x[j];
				x[j] = x[i];
				x[i] = temp1;
				lm = j + 1;
				li = i + 1;
				temp1 = x[lm];
				x[lm] = x[li];
				x[li] = temp1;
			}
		}
		if (ainv == 1.0) return;

		temp1 = 1.0 / lmx;
		for (i = 0; i < np; ++i)
		{
			x[i] *= temp1;//dddddd
		}
		return;
	}
	public void rfft(int m0, double x[])
	{
		int nn, nn2, i, j;
		double d, ti0, tr0, ti1, tr1, ac, as;
		double sstep, cstep, s, c, ww;

		nn = 1 << m0;
		nn2 = nn/2;

		cfftall(m0-1, x, 1.0);
		d   = Math.PI * 2.0 / nn;
		cstep = Math.cos(d);
		sstep = Math.sin(d);

		c = cstep;
		s = sstep;

		for (i = 2; i < nn2; i += 2) {
			j = nn - i;
			tr0 = (x[i]   + x[j])   * 0.5;
			ti0 = (x[i+1] - x[j+1]) * 0.5;
			tr1 = (x[i+1] + x[j+1]) * 0.5;
			ti1 = (x[j]   - x[i])   * 0.5;

			ac = tr1 * c - ti1 * s;
			as = ti1 * c + tr1 * s;

			x[j]   =  tr0 - ac;
			x[j+1] = -ti0 + as;
			x[i]   =  tr0 + ac;
			x[i+1] =  ti0 + as;

			ww = c * cstep - s * sstep;
			s  = s * cstep + c * sstep;
			c  = ww;
		}
		tr0     = x[0];
		tr1     = x[1];
		x[0]    = tr0 + tr1;
		x[1]    = 0.0;
		x[nn]   = tr0 - tr1;
		x[nn+1] = 0.0;
	}
	public void irfft(int m0, double x[])
	{
		int nn, i, j;
		double d, ti0, tr0, ti1, tr1, ac, as;
		double sstep, cstep, s, c, ww;

		nn = 1 << m0;

		d   = PI * 2.0 / nn;
		cstep = Math.cos(d);
		sstep = Math.sin(d);

		c = cstep;
		s = sstep;

		for (i = 2; i < (j = nn - i); i += 2) {
			tr0 = (x[i]   + x[j])* 0.5;
			ti0 = (x[i+1] - x[j+1]) * 0.5;
			as  = (x[i+1] + x[j+1]) * 0.5;
			ac  = (x[i]   - x[j]) * 0.5;

			tr1 = ac * c + as * s;
			ti1 = as * c - ac * s;

			x[i]   =  tr0 - ti1;
			x[i+1] =  ti0 + tr1;
			x[j]   =  tr0 + ti1;
			x[j+1] =  tr1 - ti0;

			ww = c * cstep - s * sstep;
			s  = s * cstep + c * sstep;
			c  = ww;
		}
		tr0  = x[0] + x[nn];
		tr1  = x[0] - x[nn];
		x[0] = tr0 * 0.5;
		x[1] = tr1 * 0.5;
		cfftall(m0-1, x, -1.0);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PuJianFa puJianFa = new PuJianFa();
		int  m0, i, j;
		int  length = 0;
		int  shift = 0, lhalf, lpow2, half_pow2;
		int  nread, noffile = 0;
		double smul = 0.0;
		double lambda = 0.0;
		double ar, ai, power;
		double kk;
		if (0   == length) length = puJianFa.DEFAULT_LENGTH;
		if (0   == shift)  shift  = puJianFa.DEFAULT_SHIFT;
		if (0.0 == smul)   smul   = puJianFa.DEFAULT_MULTIPLE;
		if (0.0 == lambda) lambda = puJianFa.DEFAULT_SMOOTHING;

		m0 = 0;
		lpow2 = 1;
		lhalf = ((length + 1)/2);
		length = lhalf + lhalf;    /* rounding */
		while(lpow2 < length) {
			lpow2 += lpow2;
			++m0;
		}
		half_pow2 = lpow2/2;

		//double *win, *frame, *noise, *pre;
		//double *rev_win;
		//frame = xd_realloc(NULL, lpow2+2);
		//noise = xd_realloc(NULL, half_pow2+1);
		//win   = xd_realloc(NULL, length);
		//rev_win = xd_realloc(NULL, shift + shift);
		//pre     = xd_realloc(NULL, shift);
		double[] win = new double[length];
		double[] frame = new double[lpow2+2];
		double[] noise = new double[half_pow2+1];
		double[] pre = new double[shift];
		double[] rev_win = new double[shift + shift];

		//short *is, *ix;
		//is = (short *)xx_realloc(NULL, length * sizeof(short));
		//ix = (short *)xx_realloc(NULL, shift * sizeof(short));
		short[] is = new short[length];
		short[] ix = new short[shift];

		//DecimalFormat dof=new DecimalFormat("#.00000");
		for (i = 0; i < length; ++i)//win 从1-512递增，512-1024递减
		{
			win[i] = 0.5 - 0.5 * Math.cos(puJianFa.PI * 2.0 * i / length);
			//System.out.println(i+"="+dof.format(win[i]));
		}
		for (i = -shift; i < shift; ++i)
			rev_win[i+shift] = (0.5 + 0.5 * Math.cos(puJianFa.PI * i / shift)) / win[i+lhalf];

		InputStream in = null;
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(puJianFa.outPutFileName)));

			try {
				// 一次读多个字节
				byte[] tempbytes = new byte[length*2];
				int byteread = 0;
				int m = 0;
				int index = 0;
				in = new FileInputStream(puJianFa.readFileName);
				//while ((byteread = in.read(tempbytes)) != -1) {
				byteread = in.read(tempbytes);
				for( j=0;j<byteread;j+=2)
				{
					//if(j%16 == 0)
					//System.out.println("\n"+Integer.toHexString(m++)+":");
					short LBit = (short) (tempbytes[j+1]&0x00ff);
					short HBit = tempbytes[j];
					is[index++] = (short) (LBit + (HBit<<8));
					//System.out.print(","+(double)is[index-1]);
				}
				//}
				//标准噪声前四帧
				//double[] f = {3.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,0.0,0.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,4.0,3.0,3.0,3.0,3.0,3.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,3.0,2.0,3.0,2.0,3.0,2.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,4.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,3.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,1.0,3.0,1.0,3.0,0.0,13.0,17.0,26.0,61.0,61.0,67.0,77.0,94.0,79.0,80.0,102.0,93.0,113.0,106.0,93.0,108.0,118.0,164.0,141.0,164.0,168.0,169.0,164.0,166.0,165.0,160.0,179.0,179.0,173.0,176.0,175.0,173.0,180.0,170.0,171.0,158.0,174.0,164.0,170.0,159.0,151.0,150.0,161.0,165.0,156.0,150.0,116.0,115.0,96.0,104.0,101.0,90.0,95.0,74.0,95.0,96.0,82.0,64.0,67.0,68.0,63.0,72.0,80.0,91.0,86.0,92.0,56.0,57.0,56.0,73.0,68.0,91.0,98.0,80.0,86.0,90.0,101.0,105.0,94.0,113.0,125.0,110.0,98.0,111.0,136.0,108.0,110.0,118.0,104.0,94.0,103.0,118.0,101.0,103.0,84.0,66.0,58.0,61.0,68.0,75.0,66.0,80.0,102.0,71.0,71.0,77.0,62.0,75.0,78.0,57.0,50.0,44.0,35.0,34.0,25.0,29.0,6.0,-2.0,-4.0,-3.0,-25.0,-60.0,-47.0,-44.0,-45.0,-52.0,-58.0,-77.0,-81.0,-107.0,-101.0,-65.0,-62.0,-86.0,-64.0,-60.0,-75.0,-48.0,-77.0,-78.0,-60.0,-72.0,-74.0,-92.0,-81.0,-84.0,-78.0,-73.0,-65.0,-70.0,-56.0,-77.0,-74.0,-84.0,-126.0,-91.0,-100.0,-129.0,-123.0,-124.0,-124.0,-124.0,-132.0,-144.0,-150.0,-150.0,-163.0,-173.0,-184.0,-176.0,-181.0,-182.0,-170.0,-171.0,-172.0,-183.0,-159.0,-180.0,-163.0,-154.0,-170.0,-183.0,-186.0,-178.0,-206.0,-172.0,-174.0,-184.0,-187.0,-207.0,-207.0,-201.0,-193.0,-206.0,-232.0,-229.0,-225.0,-214.0,-202.0,-194.0,-217.0,-236.0,-237.0,-236.0,-238.0,-237.0,-219.0,-244.0,-234.0,-231.0,-224.0,-211.0,-206.0,-194.0,-199.0,-207.0,-210.0,-206.0,-198.0,-189.0,-200.0,-225.0,-214.0,-214.0,-239.0,-255.0,-263.0,-266.0,-260.0,-259.0,-255.0,-288.0,-284.0,-271.0,-277.0,-257.0,-247.0,-264.0,-286.0,-262.0,-259.0,-266.0,-286.0,-283.0,-282.0,-284.0,-276.0,-273.0,-278.0,-294.0,-285.0,-275.0,-285.0,-285.0,-302.0,-310.0,-282.0,-256.0,-284.0,-271.0,-254.0,-283.0,-285.0,-264.0,-258.0,-260.0,-263.0,-270.0,-269.0,-276.0,-268.0,-247.0,-221.0,-202.0,-214.0,-212.0,-212.0,-214.0,-223.0,-220.0,-212.0,-218.0,-221.0,-235.0,-229.0,-242.0,-237.0,-269.0,-250.0,-248.0,-266.0,-240.0,-241.0,-250.0,-242.0,-226.0,-236.0,-203.0,-187.0,-199.0,-203.0,-211.0,-182.0,-187.0,-170.0,-181.0,-187.0,-158.0,-176.0,-157.0,-134.0,-173.0,-148.0,-139.0,-144.0,-132.0,-117.0,-106.0,-103.0,-75.0,-99.0,-88.0,-91.0,-77.0,-40.0,-45.0,-46.0,-67.0,-85.0,-67.0,-63.0,-71.0,-57.0,-44.0,-36.0,-43.0,-21.0,0.0,-13.0,-10.0,-21.0,-34.0,-12.0,-15.0,-8.0,-5.0,-14.0,-7.0,-8.0,-24.0,-42.0,-30.0,-18.0,-15.0,-37.0,-11.0,-10.0,-13.0,-5.0,-7.0,8.0,0.0,-3.0,-13.0,-28.0,-32.0,-39.0,-79.0,-43.0,-41.0,-63.0,-58.0,-51.0,-56.0,-38.0,-28.0,-26.0,-43.0,-51.0,-36.0,-33.0,-30.0,1.0,12.0,-3.0,-18.0,-13.0,7.0,-6.0,-16.0,-6.0,4.0,17.0,-1.0,27.0,36.0,23.0,26.0,12.0,23.0,43.0,53.0,51.0,92.0,86.0,101.0,105.0,87.0,93.0,99.0,101.0,92.0,77.0,80.0,100.0,114.0,137.0,116.0,124.0,143.0,153.0,142.0,163.0,159.0,169.0,178.0,200.0,194.0,166.0,196.0,212.0,207.0,196.0,188.0,177.0,190.0,171.0,159.0,150.0,155.0,151.0,168.0,184.0,195.0,198.0,185.0,175.0,196.0,199.0,212.0,162.0,161.0,174.0,219.0,250.0,236.0,242.0,256.0,270.0,273.0,316.0,359.0,354.0,334.0,343.0,343.0,340.0,352.0,340.0,352.0,339.0,361.0,361.0,353.0,348.0,372.0,392.0,402.0,422.0,403.0,404.0,404.0,412.0,378.0,402.0,411.0,411.0,421.0,416.0,428.0,436.0,445.0,431.0,454.0,458.0,428.0,451.0,449.0,475.0,456.0,447.0,468.0,468.0,503.0,488.0,461.0,474.0,499.0,503.0,476.0,462.0,474.0,492.0,511.0,502.0,532.0,504.0,518.0,529.0,528.0,527.0,537.0,507.0,501.0,483.0,493.0,474.0,438.0,455.0,448.0,454.0,432.0,450.0,450.0,437.0,419.0,403.0,392.0,390.0,377.0,357.0,358.0,357.0,362.0,365.0,378.0,388.0,369.0,374.0,362.0,333.0,334.0,335.0,312.0,304.0,275.0,280.0,283.0,270.0,274.0,263.0,283.0,271.0,280.0,274.0,293.0,269.0,268.0,280.0,279.0,289.0,280.0,276.0,271.0,256.0,262.0,273.0,220.0,213.0,195.0,224.0,236.0,194.0,216.0,231.0,186.0,170.0,193.0,174.0,135.0,149.0,127.0,101.0,94.0,119.0,100.0,97.0,94.0,67.0,69.0,97.0,99.0};
				//for(int hh = 0; hh < 1024;hh++)
				//frame[hh] = f[hh];


				puJianFa.multirr(length, is, win, frame);
				//for(j = 0;j<length;j++)
				//System.out.println(j+"="+(double)frame[j]);

				for (i = length; i < lpow2; ++i)
					frame[i] = 0.0;
				puJianFa.rfft(m0, frame);
				for (i = j = 0; j <= lpow2; ++i, j += 2) {
					noise[i] = Math.sqrt(frame[j] * frame[j] + frame[j + 1] * frame[j + 1]);
					//System.out.println(i+"="+(double)noise[i]);
				}
				for (i = 0; i < shift; ++i)
					pre[i] = 0.0;
				/************************************************************************************/
				do {
					puJianFa.multirr(length, is, win, frame);

					for (i = length; i < lpow2; ++i)
						frame[i] = 0.0;
					puJianFa.rfft(m0, frame);
					for (i = j = 0; i <= lpow2; i += 2, ++j)
					{
						ar = frame[i];
						ai = frame[i+1];
						power   = Math.sqrt(ar * ar + ai * ai + 1.0e-30);
						ar   /= power;
						ai   /= power;

						kk = Math.pow(power, 0.4) - 0.9 * Math.pow(noise[j], 0.4);
						if (kk < 0) kk = 0;
						kk = Math.pow(kk, (1 / 0.4));
						power = kk;
						frame[i]   = ar * power * puJianFa.DEFAULT_AGCKK;
						frame[i+1] = ai * power * puJianFa.DEFAULT_AGCKK;
					}
					puJianFa.irfft(m0, frame);
					for (i = 0; i < shift; ++i)
					{
						ar = pre[i] + frame[i+lhalf-shift] * rev_win[i];
						ix[i] = (short)ar;
					}
					for (i = 0; i < shift; ++i)
					{
						pre[i] = frame[i+lhalf] * rev_win[i+shift];
					}

					//byteread = in.read(tempbytes);

					//fwrite(result_B_To_L_Buf,32,1,fp2);
					//fwrite(ix, sizeof(*ix), shift, dstfd);
					//nread = rdframe(length, shift, is, srcfd);

					for (i = 0; i < shift; i++) {

						byte HBit = (byte) (ix[i]>>8);
						dos.writeByte(HBit);
						byte LBit = (byte) (ix[i]&0x0ff); //将读出的数据处理成short
						dos.writeByte(LBit);

					}
				/*
				System.out.println("======================================================");
				for(i = 0;i < length;i++)
			    {
					if(i %256 == 0)
					{
						System.out.println();
						System.out.println("*****************************************************\n"); 
					}
				  if(i%8 == 0)
					System.out.println("\n"+Integer.toHexString(m++)+":");
				  System.out.print(" "+(double)is[i]);
			    }
				System.out.println("======================================================");
			   */
					//读取思路：每次分析4帧内容，一帧32位。4帧=1帧新读取buf + 3帧旧buf
					byte[] frameReadBytes = new byte[shift*2];//读取buf
					byteread = in.read(frameReadBytes);		//每次读取256个字节 32位
					int leneff = length - shift;				//1024 -256
					index = leneff;            				//将从文件读取新的一个帧存入
					for (i = 0; i < leneff; ++i) 			//取前面的三个帧
						is[i] = is[i + shift];
					for( j=0;j<byteread;j+=2)
					{
						//if(j%16 == 0)
						//System.out.println("\n"+Integer.toHexString(m++)+":");
						short LBit = (short) (frameReadBytes[j+1]&0x00ff);
						short HBit = frameReadBytes[j];
						is[index++] = (short) (LBit + (HBit<<8));
						//System.out.print(" "+(double)is[index-1]);
					}
				}while (byteread  != -1);

				/************************************************************************************/
				dos.close();
				in.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println("===========================finish=============================");

	}

}
