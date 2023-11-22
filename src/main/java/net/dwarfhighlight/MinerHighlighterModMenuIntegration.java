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
	
	public static String getTypeOfList (int a) {
		if(a == 1) {
			return "p";
		}else {
			return "g";
		}
	}
	
	private void runConfigupdate(int a) {
		String filename = "";
		if(a == 1) {
			filename = "DwarfHighlighterList.txt";
		}else {
			filename = "DwarfHighlighterTCLList.txt";
		}
		boolean skipLines = false;
		List<List<String>> neededItemsUpdate = new ArrayList<List<String>>();
		try {
			BufferedReader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve(filename));
			String teststr = "";
			while ((teststr = reader.readLine()) !=null) {
				//ExampleMod.LOGGER.info("Needed Items: "+teststr);
				if(teststr.length()==0) {skipLines = false; continue;}
				if(teststr.charAt(0)=='#') {skipLines = false; continue;}
				if(skipLines) continue;
				if(teststr.charAt(0)=='*') skipLines = true;
				switch(teststr.charAt(0)) {
				case('3'):
					teststr = "Ring" + teststr.substring(1); //Monumenta -> Region
					break;
				case('2'):
					teststr = "Isles" + teststr.substring(1);
					break;
				case('1'):
					teststr = "Valley" + teststr.substring(1);
					break;
				default:
					break;
				}
				teststr = teststr.toLowerCase();
				int semicolonIndex = -1;
				if ((semicolonIndex = teststr.indexOf(';')) == -1) {
					List<String> tempList = new ArrayList<String>();
					tempList.add(teststr);
					tempList.add("-1");
					tempList.add(getTypeOfList(a));
					neededItemsUpdate.add(tempList);
					continue;
				}else {
					String itemName = teststr.substring(0,semicolonIndex);
					if(teststr.length() == semicolonIndex + 1) {
						if (itemName.isEmpty()) continue;
						List<String> tempList = new ArrayList<String>();
						tempList.add(itemName);
						tempList.add("-1");
						tempList.add(getTypeOfList(a));
						neededItemsUpdate.add(tempList);
						continue;
					}
					String checkQty = teststr.substring(semicolonIndex+1);
					String foundQty = "";
					for (int i = 0; i <checkQty.length(); i++) {
						if(checkQty.substring(i,i+1).matches("\\d")) {
							foundQty = foundQty + checkQty.substring(i,i+1);
						}else {
							break;
						}
					}
					if(foundQty.isEmpty() || foundQty.equals("")) {
						if(teststr.length() == semicolonIndex + 1) {
							if (itemName.isEmpty()) continue;
							List<String> tempList = new ArrayList<String>();
							tempList.add(itemName);
							tempList.add("-1");
							tempList.add(getTypeOfList(a));
							neededItemsUpdate.add(tempList);
							continue;
						}
					}
					List<String> tempList = new ArrayList<String>();
					tempList.add(itemName);
					tempList.add(""+Integer.parseInt(foundQty));
					tempList.add(getTypeOfList(a));
					neededItemsUpdate.add(tempList);
				}
			}
			reader.close();
			if(a == 1) {
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
						runConfigupdate(1);
					}
					if(getcheckTCLListValue()) {
						updateList = true;
						runConfigupdate(2);
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
					.setSaveConsumer(newValue2 -> checkListValue = newValue2)
					.build());
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Reload TCL List: "), checkListValue)
					.setDefaultValue(true)
					.setTooltip(Text.of("Reloads TCL list when saved."))
					.setSaveConsumer(newValue2 -> checkListValue = newValue2)
					.build());
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Check Chests Too: "), checkChestsValue)
					.setDefaultValue(false)
					.setTooltip(Text.of("Checks chests too."))
					.setSaveConsumer(newValue3 -> checkChestsValue = newValue3)
					.build());
			
			Screen screen = builder.build();
			return screen;
		};
	}
	
}
