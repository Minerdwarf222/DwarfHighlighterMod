package net.fabricmc.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
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

	public static Boolean getToggleValue () {
		return currentValue;
	}
	
	public static Boolean getcheckListValue () {
		return checkListValue;
	}
	
	public static void setcheckListValue () {
		checkListValue = false;
	}
	
	public static Boolean getcheckChestsValue () {
		return checkChestsValue;
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory(){
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parent)
					.setTitle(Text.of("Dwarf's Highlighter Config"));
			
			builder.setSavingRunnable(() -> {
				if(getcheckListValue()) {
					List<Pair<String,Integer>> neededItemsUpdate = new ArrayList<Pair<String,Integer>>();
					try {
						BufferedReader reader = Files.newBufferedReader(FabricLoader.getInstance().getConfigDir().resolve("DwarfHighlighterList.txt"));
						String teststr = "";
						while ((teststr = reader.readLine()) !=null) {
							//ExampleMod.LOGGER.info("Needed Items: "+teststr);
							if(teststr.length()==0) continue;
							if(teststr.charAt(0)=='#') continue;
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
								neededItemsUpdate.add(new Pair<String, Integer>(teststr, -1));
								continue;
							}else {
								String itemName = teststr.substring(0,semicolonIndex);
								if(teststr.length() == semicolonIndex + 1) {
									if (itemName.isEmpty()) continue;
									neededItemsUpdate.add(new Pair<String, Integer>(itemName,-1));
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
										neededItemsUpdate.add(new Pair<String, Integer>(itemName,-1));
										continue;
									}
								}
								neededItemsUpdate.add(new Pair<String, Integer>(itemName,Integer.parseInt(foundQty)));
							}
						}
						reader.close();
						ExampleMod.setneededItems(neededItemsUpdate);
						setcheckListValue();
					} catch (FileNotFoundException e) {
						ExampleMod.LOGGER.error("File Not Found.");
					} catch (IOException e) {
						ExampleMod.LOGGER.error("Some IOException.");
						e.printStackTrace();
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
			
			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Reload List: "), checkListValue)
					.setDefaultValue(true)
					.setTooltip(Text.of("Reloads list when saved."))
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
