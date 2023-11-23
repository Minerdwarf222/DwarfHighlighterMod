package net.dwarfhighlight;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MinerHighlighterModMenuIntegration implements ModMenuApi {

	private static Boolean currentValue = false;
	private static Boolean checkListValue = true;
	private static Boolean checkChestsValue = false;
	private static Boolean checkTCLListValue = true;

	
	public static Boolean getToggleValue () {
		
		return currentValue;
		
	}
	
	
	public static Boolean getcheckListValue () {
		
		return checkListValue;
		
	}
	
	
	public static void setcheckListValue () {
		
		checkListValue = false;
		
	}
	
	
	public static Boolean getcheckTCLListValue () {
		
		return checkTCLListValue;
		
	}
	
	
	public static void setcheckLTCListValue () {
		
		checkTCLListValue = false;
		
	}
	
	
	public static Boolean getcheckChestsValue () {
		
		return checkChestsValue;
		
	}
	
	
	public static String getTypeOfList (boolean forPrivateList) {
		
		if(forPrivateList) {
			
			return "p";
			
		}else {
			
			return "g";
			
		}
		
	}
	
	
	private void runConfigupdate(boolean forPrivateList) {
		
		String filename = "";
		
		if(forPrivateList) {
			
			filename = "DwarfHighlighterList.txt";
			
		}else {
			
			filename = "DwarfHighlighterTCLList.txt";
			
		}
		
		boolean skipLines = false;
		List<List<String>> neededItemsUpdate = new ArrayList<List<String>>();
		
		try {
			
			BufferedReader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve(filename));
			String inputLine = "";
			
			while ((inputLine = reader.readLine()) != null) {

				if(inputLine.length()==0) {skipLines = false; continue;}
				if(inputLine.charAt(0)=='#') {skipLines = false; continue;}
				
				if(skipLines) continue;
				
				if(inputLine.charAt(0)=='*') skipLines = true;
				
				switch(inputLine.charAt(0)) {
				
				case('3'):
					
					inputLine = "Ring" + inputLine.substring(1); //Monumenta -> Region
					break;
					
				case('2'):
					
					inputLine = "Isles" + inputLine.substring(1);
					break;
					
				case('1'):
					
					inputLine = "Valley" + inputLine.substring(1);
					break;
					
				default:
					
					break;
					
				}
				
				inputLine = inputLine.toLowerCase();
				int semicolonIndex = inputLine.indexOf(';');
				
				if (semicolonIndex == -1) {
					
					List<String> tempList = new ArrayList<String>();
					
					tempList.add(inputLine);
					tempList.add("-1");
					tempList.add(getTypeOfList(forPrivateList));
					
					neededItemsUpdate.add(tempList);
					
					continue;
					
				}else {
					
					String itemName = inputLine.substring(0,semicolonIndex);
					
					if(inputLine.length() == semicolonIndex + 1) {
						
						if (itemName.isEmpty()) continue;
						
						List<String> tempList = new ArrayList<String>();
						
						tempList.add(itemName);
						tempList.add("-1");
						tempList.add(getTypeOfList(forPrivateList));
						
						neededItemsUpdate.add(tempList);
						continue;
						
					}
					
					String checkQty = inputLine.substring(semicolonIndex+1);
					String foundQty = "";
					
					for (int i = 0; i <checkQty.length(); i++) {
						
						String testingChar = checkQty.substring(i,i+1);
						
						if(testingChar.matches("\\d")) {
							
							foundQty = foundQty + testingChar;
							
						}else {
							
							break;
							
						}
						
					}
					
					if(foundQty.isEmpty()) {
						
						if(inputLine.length() == semicolonIndex + 1) {
							
							if (itemName.isEmpty()) continue;
							
							List<String> tempList = new ArrayList<String>();
							
							tempList.add(itemName);
							tempList.add("-1");
							tempList.add(getTypeOfList(forPrivateList));
							
							neededItemsUpdate.add(tempList);
							
							continue;
							
						}
						
					}
					
					List<String> tempList = new ArrayList<String>();
					
					tempList.add(itemName);
					tempList.add(""+Integer.parseInt(foundQty));
					tempList.add(getTypeOfList(forPrivateList));
					
					neededItemsUpdate.add(tempList);
				}
			}
			
			reader.close();
			
			if(forPrivateList) {
				
				DwarfHighlightMod.setneededPersonalItems(neededItemsUpdate);
				setcheckListValue();
				
			}else {
				
				DwarfHighlightMod.setneededTCLItems(neededItemsUpdate);
				setcheckLTCListValue();
				
			}
		} catch (FileNotFoundException e) {
			
			DwarfHighlightMod.LOGGER.error(filename + " File Not Found.");
			
		} catch (IOException e) {
			
			DwarfHighlightMod.LOGGER.error("Some IOException.");
			e.printStackTrace();
			
		}
	}
	
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory(){
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parent)
					.setTitle(Text.of("Dwarf's Highlighter Config"));
			
			builder.setSavingRunnable(() -> {
				
				boolean updateList = false;
				
				if(getToggleValue()) {
					
					if(getcheckListValue()) {
						
						updateList = true;
						runConfigupdate(true);
						
					}
					
					if(getcheckTCLListValue()) {
						
						updateList = true;
						runConfigupdate(false);
						
					}
					
					if(updateList) {
						
						DwarfHighlightMod.mergeItemLists();
						
					}
					
				}
			});
			
			ConfigCategory general = builder.getOrCreateCategory(Text.of("category.examplemod.general"));	
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Turn on mod: "), currentValue)
					.setDefaultValue(false)
					.setTooltip(Text.of("Turns on the mod."))
					.setSaveConsumer(newValue -> currentValue = newValue)
					.build());
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Reload Personal List: "), checkListValue)
					.setDefaultValue(true)
					.setTooltip(Text.of("Reloads Personal list when saved."))
					.setSaveConsumer(newValue -> checkListValue = newValue)
					.build());
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Reload TCL List: "), checkListValue)
					.setDefaultValue(true)
					.setTooltip(Text.of("Reloads TCL list when saved."))
					.setSaveConsumer(newValue -> checkTCLListValue = newValue)
					.build());
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Check Chests Too: "), checkChestsValue)
					.setDefaultValue(false)
					.setTooltip(Text.of("Checks chests too."))
					.setSaveConsumer(newValue -> checkChestsValue = newValue)
					.build());
			
			Screen screen = builder.build();
			return screen;
		};
	}
	
}
