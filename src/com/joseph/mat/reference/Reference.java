package com.joseph.mat.reference;

import java.io.File;

public class Reference {
	public static final String MINECRAFT_ROOT_DIR;
	public static final String MINECRAFT_ASSETS_DIR;
	public static final String MINECRAFT_ASSETS_INDEX_DIR;
	public static final String MINECRAFT_ASSETS_OBJECTS;
	
	static {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("windows")) {
			// make sure that user.home points to AppData Roaming on windows
			if (!System.getProperty("user.home").contains("AppData")) {
				System.setProperty("user.home", System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming");
			}
			MINECRAFT_ROOT_DIR = System.getProperty("user.home") + File.separator + ".minecraft";
		} else if (os.contains("mac")) {
			// fix location on mac
			MINECRAFT_ROOT_DIR = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "Minecraft";
		} else {
			// linux (and unix) work as expected
			MINECRAFT_ROOT_DIR = System.getProperty("user.home") + File.separator + ".minecraft";
		}
		MINECRAFT_ASSETS_DIR = MINECRAFT_ROOT_DIR + File.separator + "assets";
		MINECRAFT_ASSETS_INDEX_DIR = MINECRAFT_ASSETS_DIR + File.separator + "indexes";
		MINECRAFT_ASSETS_OBJECTS = MINECRAFT_ASSETS_DIR + File.separator + "objects";
	}
}