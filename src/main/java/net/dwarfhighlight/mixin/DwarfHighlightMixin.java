package net.dwarfhighlight.mixin;

import net.dwarfhighlight.DwarfHighlightMod;
import net.dwarfhighlight.MinerHighlighterModMenuIntegration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerInventory;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.util.Pair;

@Mixin(HandledScreen.class)
public class DwarfHighlightMixin {
	
	private HashMap<String, Pair<Integer, Integer>> foundItems = new HashMap<String, Pair<Integer, Integer>>();
	private HashMap<String, String> nameTranslate = new HashMap<String, String>();
	
	
	@Inject(at = @At("HEAD"), method = "close()V")
	private void onCloseInject(CallbackInfo info) {
		
		onCloseCheck((HandledScreen<?>) (Object) this);
		
	}
	
	
	@SuppressWarnings("resource")
	private void onCloseCheck(HandledScreen<?> screen) {
		
		if(!MinerHighlighterModMenuIntegration.getToggleValue()) return;
		if(screen.getClass() != GenericContainerScreen.class) return;
		
		String containerType = screen.getTitle().getString();
		if(!containerType.equals("Barrel")&&!MinerHighlighterModMenuIntegration.getcheckChestsValue()) return;
		
		ScreenHandler container = screen.getScreenHandler();
		List<ItemStack> listOfItems = container.slots.stream().filter(slot -> slot.hasStack() && slot.inventory.getClass() != PlayerInventory.class).map(Slot::getStack).toList();
		String messagevalue = "";
		
		//DwarfHighlightMod.LOGGER.info("Checking for matches:");
		for(ItemStack compareItem : listOfItems) {
			
			if(compareItem.getNbt() == null) continue;
			
			int itemQty = compareItem.getCount();
			String compareItemName = compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name");
			String compareItemRegion = compareItem.getNbt().getCompound("Monumenta").getString("Region").toLowerCase();
			
			String compareItemNameLowerCase = compareItemName.toLowerCase();
			String compareItemNameWithRegion = compareItemRegion + " " + compareItemNameLowerCase;
			
			if(DwarfHighlightMod.neededItems.containsKey(compareItemNameWithRegion)) {
				
				addItemToFound(compareItemNameWithRegion, compareItemName, itemQty);
				
			}
			else if(DwarfHighlightMod.neededItems.containsKey(compareItemName.toLowerCase())) {
				
				addItemToFound(compareItemNameLowerCase, compareItemName, itemQty);
				
			}
		}
		
		for(String addItem : foundItems.keySet()) {
			
			int qtyFound = foundItems.get(addItem).getFirst();
			int amtWanted = foundItems.get(addItem).getSecond();
			String whoWants = DwarfHighlightMod.neededItems.get(addItem).get(DwarfHighlightMod.listSize-1);
			
			if (amtWanted == -1){
				messagevalue = messagevalue + qtyFound + "x " + nameTranslate.get(addItem) + " [" + whoWants +"]; ";
			}else {
				messagevalue = messagevalue + qtyFound +"x/" + amtWanted +"x " + nameTranslate.get(addItem) + " [" + whoWants + "]; ";
			}
		}
		
		if(!messagevalue.isEmpty()) {
			
			MinecraftClient.getInstance().player.sendMessage(Text.of((messagevalue.substring(0, messagevalue.length()-2)+".")));
			
		}
	}
	
	public void addItemToFound(String itemNameWithRegion, String actualItemName, int itemQty) {
		
		int amtWanted = Integer.parseInt(DwarfHighlightMod.neededItems.get(itemNameWithRegion).get(0));
		
		if(foundItems.containsKey(itemNameWithRegion)) {
			
			int oldQty = foundItems.get(itemNameWithRegion).getFirst();
			foundItems.put(itemNameWithRegion, new Pair<Integer, Integer>(oldQty+itemQty,amtWanted));
			
		}else {
			
			foundItems.put(itemNameWithRegion, new Pair<Integer, Integer>(itemQty, amtWanted));
			nameTranslate.put(itemNameWithRegion, actualItemName);
			
		}
	}
}