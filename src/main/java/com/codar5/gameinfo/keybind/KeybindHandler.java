package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = "gameinfo", value = {Dist.CLIENT})
public class KeybindHandler {
    private static final int SDL_SCANCODE_U = 24;
    public static KeyMapping openGuiKeyBinding;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        openGuiKeyBinding = new KeyMapping("key.gameinfo.open_gui", SDL_SCANCODE_U, "key.categories.gameinfo");
        event.register(openGuiKeyBinding);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (openGuiKeyBinding == null) {
            return;
        }
        while (openGuiKeyBinding.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) continue;
            mc.setScreen(new GameInfoConfigScreen(null));
        }
    }
}
