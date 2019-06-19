package com.joseph.mat;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	public static void main(String[] args) {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			JOptionPane.showMessageDialog(null, "This program does not currently support macOS");
		}
		try {
			// Make the LaF of Swing the Windows LaF
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Unable to properly set Look and Feel.");
			e.printStackTrace();
		}
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("windows")) && !System.getProperty("user.home").contains("AppData")) {
			System.setProperty("user.home", System.getProperty("user.home") + "/AppData/Roaming");
		}
	}
}