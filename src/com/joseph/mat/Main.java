package com.joseph.mat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.joseph.mat.gui.GuiMain;
import com.joseph.mat.gui.LoadingDialog;
import com.joseph.mat.json.MinecraftAsset;
import com.joseph.mat.json.MinecraftIndexParser;
import com.joseph.mat.reference.Reference;

public class Main {
	public static void main(String[] args) {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			JOptionPane.showMessageDialog(null, "This program does not currently support macOS");
		}
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("windows")) && !System.getProperty("user.home").contains("AppData")) {
			System.setProperty("user.home", System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming");
		}
		try {
			// Make the LaF of Swing the Windows LaF
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Unable to properly set Look and Feel.");
			e.printStackTrace();
		}
		
		File[] indexFiles = new File(Reference.MINECRAFT_ASSETS_INDEX_DIR).listFiles();
		HashMap<File, HashMap<String, MinecraftAsset>> fileToParseMap = new HashMap<File, HashMap<String, MinecraftAsset>>();
		
		System.out.println(Reference.MINECRAFT_ASSETS_INDEX_DIR);
		System.out.println(Arrays.toString(indexFiles));
		
		int maxLoadingProgress = 2 * indexFiles.length + 1;
		int progress = 0;
		LoadingDialog ld = new LoadingDialog(maxLoadingProgress);
		ld.updateProgressBar(progress, "Parsing index files: 0/" + indexFiles.length);
		for (int i = 0; i < indexFiles.length; i++, progress++) {
			File currentFile = indexFiles[i];
			ld.updateProgressBar(progress, "Parsing index files: " + (i + 1) + "/" + indexFiles.length);
			try {
				fileToParseMap.put(currentFile, MinecraftIndexParser.parse(currentFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			final int progresss = progress;
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					new GuiMain(ld, progresss, fileToParseMap);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void printAssetMap(HashMap<String, MinecraftAsset> map, boolean useTestString) {
		if (useTestString) {
			for (String name : map.keySet()) {
				System.out.println(name + ": " + map.get(name).toTestString());
			}
			return;
		}
		
		for (String name : map.keySet()) {
			System.out.println(name + ": " + map.get(name).toString());
		}
	}
}