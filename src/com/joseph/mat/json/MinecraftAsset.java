package com.joseph.mat.json;

public class MinecraftAsset {
	private String key;
	private String hash;
	private long size;
	
	public MinecraftAsset(String key, String hash, long size) {
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
	
	public long getSize() {
		return this.size;
	}
	
	@Override
	public String toString() {
		return "{" + this.hash + ": " + this.key + ", s=" + this.size + "}";
	}
}