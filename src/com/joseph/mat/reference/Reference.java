package com.joseph.mat.reference;

import java.io.File;

public class Reference {
	public static final String MINECRAFT_ROOT_DIR = System.getProperty("user.home") + File.separator + ".minecraft";
	public static final String MINECRAFT_ASSETS_DIR = MINECRAFT_ROOT_DIR + File.separator + "assets";
	public static final String MINECRAFT_ASSETS_INDEX_DIR = MINECRAFT_ASSETS_DIR + File.separator + "indexes";
	public static final String MINECRAFT_ASSETS_OBJECTS = MINECRAFT_ASSETS_DIR + File.separator + "objects";
}