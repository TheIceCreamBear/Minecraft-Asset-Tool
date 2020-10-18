package com.joseph.mat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * Dialog Frame that shows the progress of the loading of the program
 * @author Joseph
 *
 */
public class LoadingDialog {
	private JFrame frame;
	
	private JLabel lab;
	private JProgressBar progress;
	
	public LoadingDialog(int max) {
		this.frame = new JFrame("Loading...");
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setBounds(d.width / 2 - 100, d.height / 2 - 50, 200, 50);
		this.frame.setLayout(new BorderLayout());
		this.frame.setUndecorated(true);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.lab = new JLabel("Loading...");
		this.frame.add(lab, BorderLayout.NORTH);
		
		this.progress = new JProgressBar(0, max);
		this.progress.setPreferredSize(new Dimension(200, 30));
		this.frame.add(progress, BorderLayout.CENTER);
		
		this.frame.setVisible(true);
		this.frame.toFront();
//		this.frame.setAlwaysOnTop(true);
		
	}
	
	public void updateProgressBar(int progress, String text) {
		this.progress.setValue(progress);
		this.lab.setText(text);
	}
	
	public void closeFrame() {
		this.frame.dispose();
	}
}