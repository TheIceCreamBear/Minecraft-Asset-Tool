package com.joseph.mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
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
		try {
			// Make the LaF of Swing the System LaF
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Unable to properly set Look and Feel.");
			e.printStackTrace();
		}
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					new GuiMain();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lists the index files that are in the current index directory
	 * @return
	 */
	public static File[] getIndexFiles() {
		return new File(Reference.MINECRAFT_ASSETS_INDEX_DIR).listFiles();
	}
	
	/**
	 * Generates a map that maps an index file to another map, which maps each minecraft to its name. This 
	 * assumes the max progress of ld is at least indexFiles.length, and assumes that no progress has been
	 * updated to it.
	 * 
	 * @param indexFiles - a list of index files, usually gathered from {@link Main#getIndexFiles()}
	 * @param ld - a loading dialog
	 * @return a map that maps an index file to the assets it describes
	 */
	public static HashMap<File, HashMap<String, MinecraftAsset>> generateFileToParseMap(File[] indexFiles, LoadingDialog ld) {
		HashMap<File, HashMap<String, MinecraftAsset>> fileToParseMap = new HashMap<File, HashMap<String, MinecraftAsset>>();
		
		int progress = 0;
		
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
		
		return fileToParseMap;
	}
	
	/**
	 * Prints a given asset map, to the console, used for debugging. if useTestString is true, it will
	 * use a more verbose toString of MinecraftAsset and it will also print out the size of the largest asset
	 * @param map - the map to print
	 * @param useTestString - flag determining if a more verbose string should be used
	 */
	public static void printAssetMap(HashMap<String, MinecraftAsset> map, boolean useTestString) {
		if (useTestString) {
			MinecraftAsset max = new MinecraftAsset("notmax", "invalid", -1);
			for (String name : map.keySet()) {
				if (map.get(name).getSize() > max.getSize()) {
					max = map.get(name);
				}
				System.out.println(name + ": " + map.get(name).toTestString());
			}
			System.out.println(max.getSize());
			return;
		}
		
		for (String name : map.keySet()) {
			System.out.println(name + ": " + map.get(name).toString());
		}
	}
	
	/**
	 * Converts the given minecraft assets, as defined in the assets array, into their normal file representation,
	 * and places that file inside the destination dir
	 * @param assets - the assets to convert
	 * @param destinationDir - the dir to place the assets in
	 */
	public static void convertSelectedMinecraftAssets(MinecraftAsset[] assets, String destinationDir) {
		LoadingDialog ld = new LoadingDialog(assets.length);
		
		int count = 0;
		
		for (int i = 0; i < assets.length; i++) {
			// update status
			ld.updateProgressBar(i, "Processing file " + assets[i].toString() + ": " + i + "/" + assets.length);
			MinecraftAsset current = assets[i];
			
			// calculate the source file
			String objectFolder = current.getHash().substring(0, 2);
			String fileSource = Reference.MINECRAFT_ASSETS_OBJECTS + File.separator + objectFolder + File.separator + current.getHash();
			
			// calculate the destination file
			String currentKeyFixed = current.getKey().replace('/', File.separatorChar);
			String fileDestinationParent = destinationDir + File.separator;
			if (currentKeyFixed.contains(File.separator)) {
				 fileDestinationParent = fileDestinationParent + currentKeyFixed.substring(0, currentKeyFixed.lastIndexOf(File.separatorChar));
			}
			String fileDestination = destinationDir + File.separator + currentKeyFixed;
			
			// open the files needed
			File source = new File(fileSource);
			File destinationParent = new File(fileDestinationParent);
			File destination = new File(fileDestination);
			
			// if the source file doesn't exist (for some reason) go to the next loop
			if (!source.exists()) {
				continue;
			}
			
			// make sure that the folders exist
			if (!destinationParent.exists()) {
				destinationParent.mkdirs();
			}
			
			// make sure that the file exists
			if (!destination.exists()) {
				try {
					destination.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
			
			// code resources found while looking for (better) ways to copy a file 
			// from one location to another
			// https://stackoverflow.com/questions/2520305/java-io-to-copy-one-file-to-another
			// https://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
			
			// try with resources with many resources looks gross
			// try with resources code convention (maybe?)
			// https://stackoverflow.com/questions/30753996/what-is-the-code-convention-for-formatting-try-with-resources
			try (FileInputStream inStream = new FileInputStream(source);
				 FileOutputStream outStream = new FileOutputStream(destination);
				 FileChannel src = inStream.getChannel();
				 FileChannel dest = outStream.getChannel())
			{
				// transfer the contents of the soruce file to the destination file
				dest.transferFrom(src, 0, current.getSize());
				count++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ld.closeFrame();
		JOptionPane.showMessageDialog(null, count + "/" + assets.length + " files successfully transfered.");
	}
}