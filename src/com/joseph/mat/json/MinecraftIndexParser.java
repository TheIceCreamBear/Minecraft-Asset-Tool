package com.joseph.mat.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

public class MinecraftIndexParser {
	public static HashMap<String, MinecraftAsset> parse(File file) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(file));
		HashMap<String, MinecraftAsset> map = new HashMap<String, MinecraftAsset>();
		reader.beginObject();
		if (!reader.nextName().equals("objects")) {
			reader.close();
			throw new MalformedJsonException("Invalid index file");
		}
		reader.beginObject();
		while (reader.peek() != JsonToken.END_OBJECT && reader.peek() != JsonToken.END_DOCUMENT) {
			String name = reader.nextName();
			reader.beginObject();
			reader.nextName(); // hash
			String hash = reader.nextString();
			reader.nextName(); // size
			int size = reader.nextInt();
			MinecraftAsset ma = new MinecraftAsset(name, hash, size);
			map.put(name, ma);
			reader.endObject();
		}
		reader.endObject();
		reader.endObject();
		reader.close();
		// DOCUMENT END
		return map;
	}
}