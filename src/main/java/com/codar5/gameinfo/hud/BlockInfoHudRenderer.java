package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameInfoNeoForge;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "gameinfo", value = {Dist.CLIENT})
public class BlockInfoHudRenderer {
    private static Field currentBreakingProgressField;

    private float getPreciseBreakingProgress(Minecraft client) {
        int intProgress;
        if (currentBreakingProgressField != null && client.gameMode != null) {
            try {
                return ((Float) currentBreakingProgressField.get(client.gameMode)).floatValue();
            } catch (Exception exception) {
                // empty catch block
            }
        }
        return (intProgress = client.gameMode.getDestroyStage()) >= 0 ? (float) intProgress / 10.0f : -1.0f;
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        new BlockInfoHudRenderer().render(event.getGuiGraphics());
    }

    public void render(GuiGraphics drawContext) {
        if (!GameInfoNeoForge.config.showBlockInfo) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.level == null || client.player == null) {
            return;
        }
        HitResult hitResult = client.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            float preciseProgress;
            boolean isBreaking;
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            ClientLevel world = client.level;
            BlockState blockState = world.getBlockState(pos);
            ItemStack itemStack = new ItemStack((ItemLike) blockState.getBlock());
            String blockName = blockState.getBlock().getName().getString();
            String positionInfo = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            ItemStack heldItem = client.player.getMainHandItem();
            String progressText = "";
            String toolIcon = "";
            if (!heldItem.isEmpty()) {
                boolean isUnbreakableBlock;
                boolean isCorrectTool = heldItem.isCorrectToolForDrops(blockState);
                boolean requiresTool = blockState.requiresCorrectToolForDrops();
                Block block = blockState.getBlock();
                isUnbreakableBlock = block == Blocks.BEDROCK || block == Blocks.BARRIER;
                boolean canHarvest = isUnbreakableBlock ? false : (requiresTool ? isCorrectTool : true);
                toolIcon = canHarvest ? " ✓" : " ✗";
            }
            isBreaking = (preciseProgress = this.getPreciseBreakingProgress(client)) >= 0.0f;
            if (isBreaking) {
                progressText = String.format(" %.0f%%", preciseProgress * 100.0f);
            }
            int screenWidth = client.getWindow().getGuiScaledWidth();
            String fullBlockName = blockName + toolIcon + progressText;
            MutableComponent nameText = Component.literal(fullBlockName);
            MutableComponent posText = Component.literal(positionInfo);
            int nameWidth = client.font.width(nameText);
            int posWidth = client.font.width(posText);
            int maxTextWidth = Math.max(nameWidth, posWidth);
            int itemSize = 16;
            int padding = 8;
            int textPadding = 6;
            int lineHeight = 11;
            int borderThickness = 1;
            int contentWidth = itemSize + textPadding + maxTextWidth;
            int contentHeight = itemSize;
            int totalWidth = contentWidth + padding * 2 + borderThickness * 2;
            int totalHeight = contentHeight + padding * 2 + borderThickness * 2;
            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10;
            int borderColor = -5592406;
            drawContext.fill(startX, startY, startX + totalWidth, startY + borderThickness, borderColor);
            drawContext.fill(startX, startY + totalHeight - borderThickness, startX + totalWidth, startY + totalHeight, borderColor);
            drawContext.fill(startX, startY, startX + borderThickness, startY + totalHeight, borderColor);
            drawContext.fill(startX + totalWidth - borderThickness, startY, startX + totalWidth, startY + totalHeight, borderColor);
            if (isBreaking) {
                int progressWidth = (int) ((float) (totalWidth - borderThickness * 2) * preciseProgress);
                drawContext.fill(startX + borderThickness, startY + totalHeight - borderThickness, startX + borderThickness + progressWidth, startY + totalHeight, -3373056);
            }
            int bgX = startX + borderThickness;
            int bgY = startY + borderThickness;
            int bgWidth = totalWidth - borderThickness * 2;
            int bgHeight = totalHeight - borderThickness * 2;
            drawContext.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, -1342177280);
            int itemX = bgX + padding;
            int itemY = bgY + padding;
            int textX = itemX + itemSize + textPadding;
            int nameY = bgY + padding + 1;
            int posY = nameY + lineHeight;
            drawContext.renderItem(itemStack, itemX, itemY);
            if (toolIcon.isEmpty() && progressText.isEmpty()) {
                drawContext.drawString(client.font, nameText, textX, nameY, -2039584, true);
            } else {
                MutableComponent blockNameOnly = Component.literal(blockName);
                drawContext.drawString(client.font, blockNameOnly, textX, nameY, -2039584, true);
                int currentX = textX + client.font.width(blockNameOnly);
                if (!toolIcon.isEmpty()) {
                    MutableComponent iconText = Component.literal(toolIcon);
                    int iconColor = toolIcon.contains("✓") ? -9387920 : -4165520;
                    drawContext.drawString(client.font, iconText, currentX, nameY, iconColor, true);
                    currentX += client.font.width(iconText);
                }
                if (!progressText.isEmpty() && preciseProgress > 0.0f) {
                    MutableComponent progressTextObj = Component.literal(progressText);
                    drawContext.drawString(client.font, progressTextObj, currentX, nameY, -3373056, true);
                }
            }
            drawContext.drawString(client.font, posText, textX, posY, -9387840, true);
        }
    }

    static {
        try {
            currentBreakingProgressField = MultiPlayerGameMode.class.getDeclaredField("currentBreakingProgress");
            currentBreakingProgressField.setAccessible(true);
        } catch (Exception e) {
            currentBreakingProgressField = null;
        }
    }
}
