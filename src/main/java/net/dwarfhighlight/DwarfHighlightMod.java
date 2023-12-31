package net.dwarfhighlight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DwarfHighlightMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("DwarfsHighlighterMod");
	public static HashMap<String, List<String>> neededItems = new HashMap<String, List<String>>();
	public static List<List<String>> neededPersonalItems = new ArrayList<List<String>>();
	public static List<List<String>> neededTCLItems = new ArrayList<List<String>>();
	
	public static int listSize = 2;
	
	
	public static void setneededPersonalItems (List<List<String>> neededItemsUpdate) {
		
		neededPersonalItems.clear();
		
		for(List<String> item : neededItemsUpdate) {
			
			neededPersonalItems.add(item);
			
		}
	}
	
	
	public static void setneededTCLItems (List<List<String>> neededItemsUpdate) {
		
		neededTCLItems.clear();
		
		for(List<String> item : neededItemsUpdate) {
			
			neededTCLItems.add(item);
			
		}		
	}
	
	
	public static void mergeItemLists () {
		
		neededItems.clear();
		
		for(List<String> item : neededPersonalItems) {
			
			String itemName = item.get(0);
			
			if(!neededItems.containsKey(itemName)) {
				
				neededItems.put(itemName, item.subList(1,listSize+1));
				
			}
		}
		
		for(List<String> item : neededTCLItems) {
			
			String itemName = item.get(0);
			
			if(neededItems.containsKey(itemName)) {
				
				if(neededItems.get(itemName).get(listSize-1).equals("p")) {
					
					neededItems.get(itemName).set(listSize-1, "p/g");
					neededItems.put(itemName, neededItems.get(itemName));
					
				}
				
			}else {
				
				neededItems.put(itemName, item.subList(1,listSize+1));
				
			}
			
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
				
				DwarfHighlightMod.LOGGER.info("Created List File.");
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		try {
			
			File testIfConfig = FabricLoader.getInstance().getConfigDir().resolve("DwarfHighlighterTCLList.txt").toFile();
			
			if(testIfConfig.createNewFile()) {
				
				DwarfHighlightMod.LOGGER.info("Created TCL List File.");
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
}
