package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.datafixers.util.Pair;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("DwarfsHighlighterMod");
	public static List<Pair<String,Integer>> neededItems = new ArrayList<Pair<String,Integer>>();
	public static void setneededItems (List<Pair<String,Integer>> neededItemsUpdate) {
		neededItems.clear();
		//LOGGER.debug("The pairs being checked:");
		for(Pair<String,Integer> item : neededItemsUpdate) {
			neededItems.add(item);
			//LOGGER.debug(item.toString());
		}
	}
	
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		try {
			File testIfConfig = FabricLoader.getInstance().getConfigDir().resolve("DwarfHighlighterList.txt").toFile();
			if(testIfConfig.createNewFile()) {
				ExampleMod.LOGGER.info("Created List File.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
