package com.wegame.server;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WateWave extends JFrame implements Runnable {

	boolean m_isRunning = false;

	int m_width;
	int m_height;
	int length;

	int[] arrWaveCurrent;// 当前波形
	int[] arrWaveNext;// 下一帧的波形

	int[] arrClrInfo;// 图片原始颜色信息
	int[] arrClrBuff;// 图片新的颜色信息

	private Thread runner;
	private Random random;
	private Image offImage;
	private MemoryImageSource source;
	private JPanel panel = null;

	public WateWave(String pic) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		random = new Random();
		try {
			URL url =getClass().getResource(pic);
			offImage = ImageIO.read(url);// 你自己的图片目录
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		m_width = offImage.getWidth(this);
		m_height = offImage.getHeight(this);
		length = m_width * m_height;

		arrWaveCurrent = new int[length];
		arrWaveNext = new int[length];
		arrClrInfo = new int[length];
		arrClrBuff = new int[length];

		PixelGrabber pg = new PixelGrabber(offImage, 0, 0, m_width, m_height, arrClrInfo, 0, m_width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		source = new MemoryImageSource(m_width, m_height, arrClrBuff, 0, m_width);
		source.setAnimated(true);
		offImage.getGraphics();
		offImage = createImage(source);
		panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(offImage, 0, 0, this);
			}
		};
		this.setContentPane(panel);
		this.setSize(m_width, m_height);
		this.setVisible(true);
		start();
	}

	public void start() {
		m_isRunning = true;
		runner = new Thread(this);
		runner.start();
	}

	public void stop() {
		m_isRunning = false;
	}

	public void destroy() {
		stop();
		runner.interrupt();
	}

	public void run() {
		while (m_isRunning) {
			source.newPixels();
			 dot();
			rippleRender();

			try {
				Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void dot() {
		int x = 10 + random.nextInt() % (m_width - 20);
		int y = 10 + random.nextInt() % (m_height - 20);
		dropStone(x, y, 3, 128);
	}

	/**
	 * * 某点下一时刻的波幅算法为：上下左右四点的波幅和的一半减去当前波幅， 即 X0' =（X1 + X2 + y1 + X4）/ 2 - X0
	 * 
	 * <pre>
	 * +----x3----+ 
	 * +     |    + 
	 * x1---x0----x2 
	 * +     |    +
	 * +----x4----+
	 * 
	 * <pre>
	 */

	void rippleRender() {

		int index = m_width;
		int indexPreX = index - 1;
		int indexNextX = index + 1;
		int indexPreY = index - m_width;
		int indexNextY = index + m_width;

		for (int y = 1; y < m_height - 1; y++) {
			for (int x = 1; x < m_width - 1; x++, index++) {
				int x1 = arrWaveCurrent[indexPreX++];
				int x2 = arrWaveCurrent[indexNextX++];
				int x3 = arrWaveCurrent[indexPreY++];
				int x4 = arrWaveCurrent[indexNextY++];
				// 波能扩散:上下左右四点的波幅和的一半减去当前波幅
				// X0' =（X1 + X2 + X3 + X4）/ 2 - X0
				arrWaveNext[index] = ((x1 + x2 + x3 + x4) >> 1) - arrWaveNext[index];
				// 波能衰减 1/32
				arrWaveNext[index] -= arrWaveNext[index] >> 5;
				// 计算出偏移象素和原始象素的内存地址偏移量 :
				int xoffset = x1 - x2;
				int yoffset = x3 - x4;
				int offset = m_width * yoffset + xoffset;
				// 判断坐标是否在窗口范围内
				if (index + offset > 0 && index + offset < length) {
					arrClrBuff[index] = arrClrInfo[index + offset];
				} else {
					arrClrBuff[index] = arrClrInfo[index];
				}
			}
		}
		// 交换波能数据缓冲区
		int[] temp = arrWaveCurrent;
		arrWaveCurrent = arrWaveNext;
		arrWaveNext = temp;
	}

	/**
	 * 扔石头
	 * 
	 * @param x
	 * @param y
	 * @param r
	 *            半径
	 * @param h
	 *            波源能量
	 */
	void dropStone(int x, int y, int r, int h) {
		// 判断坐标是否在屏幕范围内
		if ((x + r) > m_width || (y + r) > m_height || (x - r) < 0 || (y - r) < 0) {
			return;
		}

		int value = r * r;

		for (int posx = x - r; posx < x + r; posx++) {
			for (int posy = y - r; posy < y + r; posy++) {
				if ((posx - x) * (posx - x) + (posy - y) * (posy - y) < value) {
					arrWaveCurrent[m_width * posy + posx] = -h;
				}
			}
		}
	}


}