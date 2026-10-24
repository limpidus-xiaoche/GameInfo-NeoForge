package com.codar5.gameinfo;

import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = "gameinfo", value = {Dist.CLIENT})
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById("gameinfo").ifPresent(container -> container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> new GameInfoConfigScreen(parent)));
    }
}
