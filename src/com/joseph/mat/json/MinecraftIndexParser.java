package com.joseph.mat.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

public class MinecraftIndexParser {
	/**
	 * Parses the given json file and returns a hash map that maps the asset's name
	 * to an instance of Minecraft asset. This method assumes that the given file is
	 * a json file, and that it conforms to the expected minecraft asset index format.
	 * <p>
	 * This method will throw an IOException if the given file is not found, if
	 * the beginning of the json file is not formatted correctly, or if closing the 
	 * json reader fails.
	 * @param file - a json file to parse for Minecraft Assets
	 * @return a HashMap mapping the name of the asset to the asset
	 * @throws IOException
	 */
	public static HashMap<String, MinecraftAsset> parse(File file) throws IOException {
		// create objects
		JsonReader reader = new JsonReader(new FileReader(file));
		HashMap<String, MinecraftAsset> map = new HashMap<String, MinecraftAsset>();
		// begin reading json
		reader.beginObject();
		if (!reader.nextName().equals("objects")) {
			reader.close();
			throw new MalformedJsonException("Invalid index file");
		}
		reader.beginObject();
		// loop over all asset objects until the end of the "objects" object is reached
		while (reader.peek() != JsonToken.END_OBJECT && reader.peek() != JsonToken.END_DOCUMENT) {
			// get the asset name and being the object
			String name = reader.nextName();
			reader.beginObject();
			// skip over the name for hash but keep its value
			reader.nextName(); // hash
			String hash = reader.nextString();
			// skop over the name for size but keep its value
			reader.nextName(); // size
			int size = reader.nextInt();
			// construct a new MinecraftAsset object with all the information we just read in
			MinecraftAsset ma = new MinecraftAsset(name, hash, size);
			// and put it in the map
			map.put(name, ma);
			// end this asset
			reader.endObject();
		}
		// end the json file
		reader.endObject();
		reader.endObject();
		reader.close();
		// DOCUMENT END
		return map;
	}
}