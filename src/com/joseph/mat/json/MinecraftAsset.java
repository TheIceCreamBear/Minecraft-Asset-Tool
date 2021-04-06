package com.joseph.mat.json;

/**
 * An immutable class to represent a minecraft asset that is defined in the version index
 * files. Each asset has a key (or name), a hash, and a size.
 * @author Joseph
 *
 */
public class MinecraftAsset {
	private String key;
	private String hash;
	private int size;
	
	/**
	 * Constructs a new MinecraftAsset with the given key, hash, and size.
	 * @param key
	 * @param hash
	 * @param size
	 */
	public MinecraftAsset(String key, String hash, int size) {
		this.key = key;
		this.hash = hash;
		this.size = size;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public String toTestString() {
		return "{" + this.hash + ": " + this.key + ", s=" + this.size + "}";
	}
	
	public String toString() {
		return this.key.substring(this.key.lastIndexOf('/') + 1);
	}
}