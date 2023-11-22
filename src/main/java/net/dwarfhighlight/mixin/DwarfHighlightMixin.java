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
	@Inject(at = @At("HEAD"), method = "close()V")
	private void onCloseInject(CallbackInfo info) {
		onCloseCheck((HandledScreen<?>) (Object) this);
	}
	@SuppressWarnings("resource")
	private void onCloseCheck(HandledScreen<?> screen) {
		if(!MinerHighlighterModMenuIntegration.getToggleValue()) return;
		if(screen.getClass() != GenericContainerScreen.class) return;
		if(!screen.getTitle().getString().equals("Barrel")&&!MinerHighlighterModMenuIntegration.getcheckChestsValue()) return;
		ScreenHandler container = screen.getScreenHandler();
		List<ItemStack> listOfItems = container.slots.stream().filter(slot -> slot.hasStack() && slot.inventory.getClass() != PlayerInventory.class).map(Slot::getStack).toList();
		String messagevalue = "";
		HashMap<String, Pair<Integer, Integer>> foundItems = new HashMap<String, Pair<Integer, Integer>>();
		HashMap<String, String> nameTranslate = new HashMap<String, String>();
		//DwarfHighlightMod.LOGGER.info("Checking for matches:");
		for(ItemStack compareItem : listOfItems) {
			String checkNameKey = "";
			if(compareItem.getNbt() != null 
					&& (
							DwarfHighlightMod.neededItems.containsKey(checkNameKey = compareItem.getNbt().getCompound("Monumenta").getString("Region").toLowerCase() + " " + compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name").toLowerCase())
							|| 
							DwarfHighlightMod.neededItems.containsKey(checkNameKey = compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name").toLowerCase())
							)) 
			{
				//String foundItem = compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name");
				String foundItem = checkNameKey;
				int itemQty = compareItem.getCount();
				if(foundItems.containsKey(foundItem)) {
					foundItems.put(foundItem, new Pair<Integer, Integer>(foundItems.get(foundItem).getFirst()+itemQty,foundItems.get(foundItem).getSecond()));
				}else {
					foundItems.put(foundItem, new Pair<Integer, Integer>(itemQty, Integer.parseInt(DwarfHighlightMod.neededItems.get(checkNameKey).get(0))));
					nameTranslate.put(checkNameKey, compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name"));
				}
				break;
			}
		}
		for(String addItem : foundItems.keySet()) {
			if (foundItems.get(addItem).getSecond() == -1){
				messagevalue = messagevalue + foundItems.get(addItem).getFirst() + "x " + nameTranslate.get(addItem) + " [" + DwarfHighlightMod.neededItems.get(addItem).get(DwarfHighlightMod.listSize-1)+"]; ";
			}else {
				messagevalue = messagevalue + foundItems.get(addItem).getFirst() +"x/" + foundItems.get(addItem).getSecond() +"x " + nameTranslate.get(addItem) + " [" + DwarfHighlightMod.neededItems.get(addItem).get(DwarfHighlightMod.listSize-1)+ "]; ";
			}
		}
		if(!messagevalue.isEmpty()) {
			MinecraftClient.getInstance().player.sendMessage(Text.of((messagevalue.substring(0, messagevalue.length()-2)+".")));
		}
	}
}