package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.MinerHighlighterModMenuIntegration;
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
public class ExampleMixin {
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
		ExampleMod.LOGGER.info("Checking for matches:");
		for(ItemStack compareItem : listOfItems) {
			for(Pair<String, Integer> iteratePair : ExampleMod.neededItems) {
				if(compareItem.getNbt() != null 
						&& (
								iteratePair.getFirst().equals(compareItem.getNbt().getCompound("Monumenta").getString("Region").toLowerCase() + " " + compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name").toLowerCase())
								|| 
								iteratePair.getFirst().equals(compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name").toLowerCase())
								)) 
				{
					String foundItem = compareItem.getNbt().getCompound("plain").getCompound("display").getString("Name");
					int itemQty = compareItem.getCount();
					if(foundItems.containsKey(foundItem)) {
						foundItems.put(foundItem, new Pair<Integer, Integer>(foundItems.get(foundItem).getFirst()+itemQty,foundItems.get(foundItem).getSecond()));
					}else {
						foundItems.put(foundItem, new Pair<Integer, Integer>(itemQty, iteratePair.getSecond()));
					}
					break;
				}
			}
		}
		for(String addItem : foundItems.keySet()) {
			if (foundItems.get(addItem).getSecond() == -1){
				messagevalue = messagevalue + foundItems.get(addItem).getFirst() + "x " + addItem + "; ";
			}else {
				messagevalue = messagevalue + foundItems.get(addItem).getFirst() +"x " + addItem + ", " + foundItems.get(addItem).getSecond() + " needed; ";
			}
		}
		if(!messagevalue.isEmpty()) {
			MinecraftClient.getInstance().player.sendMessage(Text.of((messagevalue.substring(0, messagevalue.length()-2)+".")));
		}
	}
}