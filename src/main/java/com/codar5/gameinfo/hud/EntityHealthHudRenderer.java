package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameInfoNeoForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "gameinfo", value = {Dist.CLIENT})
public class EntityHealthHudRenderer {
    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        new EntityHealthHudRenderer().render(event.getGuiGraphics());
    }

    public void render(GuiGraphics drawContext) {
        if (!GameInfoNeoForge.config.showEntityInfo) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.level == null || client.player == null) {
            return;
        }
        HitResult hitResult = client.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            this.renderEntityInfo(drawContext, (EntityHitResult) hitResult, client);
        }
    }

    private void renderEntityInfo(GuiGraphics drawContext, EntityHitResult entityHitResult, Minecraft client) {
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        String entityName = entity.getName().getString();
        float health = livingEntity.getHealth();
        float maxHealth = livingEntity.getMaxHealth();
        String healthInfo = String.format("血量: %.1f/%.1f", health, maxHealth);
        int screenWidth = client.getWindow().getGuiScaledWidth();
        MutableComponent nameText = Component.literal(entityName);
        MutableComponent healthText = Component.literal(healthInfo);
        int nameWidth = client.font.width(nameText);
        int healthWidth = client.font.width(healthText);
        int maxTextWidth = Math.max(nameWidth, healthWidth);
        int itemSize = 16;
        int padding = 8;
        int textPadding = 4;
        int lineHeight = 10;
        int barHeight = 3;
        int barPadding = 2;
        int totalWidth = itemSize + textPadding + maxTextWidth + padding * 2;
        int totalHeight = Math.max(itemSize, lineHeight * 2 + barHeight + barPadding) + padding * 2;
        int startX = (screenWidth - totalWidth) / 2;
        int startY = 10;
        drawContext.fill(startX, startY, startX + totalWidth, startY + totalHeight, -2143289344);
        int itemX = startX + padding;
        int itemY = startY + padding + (totalHeight - padding * 2 - itemSize) / 2;
        int textAreaX = itemX + itemSize + textPadding;
        int textAreaWidth = maxTextWidth;
        int nameY = startY + padding + 2;
        int healthY = nameY + lineHeight + 2;
        int barY = healthY + lineHeight + barPadding;
        int nameTextX = textAreaX + (textAreaWidth - nameWidth) / 2;
        int healthTextX = textAreaX;
        ItemStack displayItem = this.getEntityDisplayItem(entity);
        drawContext.renderItem(displayItem, itemX, itemY);
        drawContext.drawString(client.font, nameText, nameTextX, nameY, -1, true);
        int healthColor = this.getHealthColor(health, maxHealth);
        drawContext.drawString(client.font, healthText, healthTextX, healthY, healthColor, true);
        this.renderHealthBar(drawContext, health, maxHealth, textAreaX, barY, maxTextWidth);
    }

    private ItemStack getEntityDisplayItem(Entity entity) {
        if (entity instanceof Player) {
            return new ItemStack((ItemLike) Items.PLAYER_HEAD);
        }
        if (entity instanceof Mob) {
            return new ItemStack((ItemLike) Items.IRON_SWORD);
        }
        if (entity instanceof Animal) {
            return new ItemStack((ItemLike) Items.WHEAT);
        }
        return new ItemStack((ItemLike) Items.EGG);
    }

    private int getHealthColor(float health, float maxHealth) {
        float healthPercentage = health / maxHealth;
        if (healthPercentage > 0.6f) {
            return -16711936;
        }
        if (healthPercentage > 0.3f) {
            return -256;
        }
        return -65536;
    }

    private void renderHealthBar(GuiGraphics drawContext, float health, float maxHealth, int x, int y, int width) {
        int barHeight = 3;
        float healthPercentage = health / maxHealth;
        drawContext.fill(x, y, x + width, y + barHeight, -13421773);
        int healthWidth = (int) ((float) width * healthPercentage);
        int healthColor = this.getHealthColor(health, maxHealth);
        drawContext.fill(x, y, x + healthWidth, y + barHeight, healthColor);
    }
}
