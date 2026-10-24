package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameInfoNeoForge;
import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.renderer.FurnaceDataCache;
import com.codar5.gameinfo.util.SlimeChunkUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "gameinfo", value = {Dist.CLIENT})
public class HudOverlay {
    static Minecraft client;
    static Font textRenderer;
    static int DEFAULT_COLOR;
    static int DEFAULT_HEIGHT;
    static int color;
    static GameInfoConfig config;
    private static final int SLIME_CHUNK_COLOR = -11141291;
    public static final ConcurrentHashMap<BlockPos, ItemStack[]> serverFurnaceCache = new ConcurrentHashMap<>();
    static EquipmentSlot[] armorSlots;
    private static final HudOverlay INSTANCE = new HudOverlay();

    public HudOverlay() {
        client = Minecraft.getInstance();
        textRenderer = client != null ? client.font : null;
        config = GameInfoNeoForge.config;
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        if (client == null || textRenderer == null) {
            client = Minecraft.getInstance();
            if (client != null) {
                textRenderer = client.font;
            }
        }
        if (config == null) {
            config = GameInfoNeoForge.config;
        }
        INSTANCE.render(event.getGuiGraphics());
    }

    public void toggleHudVisibility() {
        HudOverlay.config.enabled = !HudOverlay.config.enabled;
        config.saveConfig();
    }

    public static void logger(String message) {
        System.out.println("[DEBUG]： " + message);
    }

    private int getMainColor() {
        return 0xFF000000 | HudOverlay.config.color & 0xFFFFFF;
    }

    private int getDefaultColor() {
        return 0xFF000000 | DEFAULT_COLOR & 0xFFFFFF;
    }

    public void render(GuiGraphics drawContext) {
        LocalPlayer player;
        if (textRenderer == null && (textRenderer = HudOverlay.client.font) == null) {
            HudOverlay.logger("文本渲染器仍然为null，无法渲染HUD信息");
        }
        if ((player = HudOverlay.client.player) == null) {
            return;
        }
        if (!HudOverlay.config.enabled) {
            return;
        }
        int xPos = HudOverlay.config.xPos;
        int yPos = HudOverlay.config.yPos;
        color = HudOverlay.config.color;
        yPos += this.renderFPS(drawContext, xPos, yPos);
        yPos += this.renderTimeAndDays(drawContext, xPos, yPos);
        yPos += this.renderCoordinates(drawContext, xPos, yPos);
        yPos += this.renderNetherCoordinates(drawContext, xPos, yPos);
        yPos += this.renderBiome(drawContext, xPos, yPos);
        this.renderRemark(drawContext);
        this.renderPlayerEquipment(drawContext);
        this.renderFurnaceInfo(drawContext);
    }

    private void renderFurnaceInfo(GuiGraphics ctx) {
        FurnaceDataCache.FurnaceItems fc;
        Object blockEntity;
        if (!HudOverlay.config.showFurnaceInfo) {
            return;
        }
        if (HudOverlay.client.level == null || HudOverlay.client.hitResult == null) {
            return;
        }
        if (HudOverlay.client.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = ((BlockHitResult) HudOverlay.client.hitResult).getBlockPos().immutable();
        if (!(HudOverlay.client.level.getBlockState(pos).getBlock() instanceof AbstractFurnaceBlock)) {
            return;
        }
        ItemStack input = ItemStack.EMPTY;
        ItemStack fuel = ItemStack.EMPTY;
        ItemStack output = ItemStack.EMPTY;
        boolean gotData = false;
        ItemStack[] cached = serverFurnaceCache.get(pos);
        if (cached != null) {
            input = cached[0];
            fuel = cached[1];
            output = cached[2];
            gotData = true;
        }
        if (!gotData && (blockEntity = HudOverlay.client.level.getBlockEntity(pos)) instanceof AbstractFurnaceBlockEntity) {
            AbstractFurnaceBlockEntity clientFurnace = (AbstractFurnaceBlockEntity) blockEntity;
            input = clientFurnace.getItem(0);
            fuel = clientFurnace.getItem(1);
            output = clientFurnace.getItem(2);
            if (!(input.isEmpty() && fuel.isEmpty() && output.isEmpty())) {
                gotData = true;
            }
        }
        if (!gotData && (fc = FurnaceDataCache.getFurnaceData(pos)) != null) {
            input = fc.input;
            fuel = fc.fuel;
            output = fc.output;
        }
        if (HudOverlay.client.player != null && (blockEntity = HudOverlay.client.player.containerMenu) instanceof AbstractFurnaceMenu) {
            AbstractFurnaceMenu menu = (AbstractFurnaceMenu) blockEntity;
            try {
                ItemStack m0 = menu.getSlot(0).getItem();
                ItemStack m1 = menu.getSlot(1).getItem();
                ItemStack m2 = menu.getSlot(2).getItem();
                FurnaceDataCache.updateFurnaceData(pos, m0, m1, m2);
                input = m0;
                fuel = m1;
                output = m2;
            } catch (Exception m0) {
                // empty catch block
            }
        }
        int panelWidth = 160;
        int panelHeight = 78;
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = screenHeight / 2 + 40;
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, -1073741824);
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + 1, -5592406);
        ctx.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, -5592406);
        ctx.fill(panelX, panelY, panelX + 1, panelY + panelHeight, -5592406);
        ctx.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, -5592406);
        MutableComponent title = Component.translatable("gameinfo.hud.furnace.title");
        ctx.drawCenteredString(textRenderer, title, panelX + panelWidth / 2, panelY + 5, this.getMainColor());
        int rowX = panelX + 8;
        int rowY = panelY + 22;
        this.drawFurnaceRow(ctx, input, "gameinfo.hud.furnace.input", rowX, rowY);
        this.drawFurnaceRow(ctx, fuel, "gameinfo.hud.furnace.fuel", rowX, rowY += 18);
        this.drawFurnaceRow(ctx, output, "gameinfo.hud.furnace.output", rowX, rowY += 18);
    }

    private void drawFurnaceRow(GuiGraphics ctx, ItemStack stack, String labelKey, int x, int y) {
        MutableComponent label = Component.translatable(labelKey);
        ctx.drawString(textRenderer, label, x, y + 4, this.getMainColor(), true);
        int labelWidth = textRenderer.width(label);
        int itemX = x + labelWidth + 6;
        if (!stack.isEmpty()) {
            ctx.renderItem(stack, itemX, y);
            String name = stack.getHoverName().getString();
            int count = stack.getCount();
            String text = count > 1 ? name + " x" + count : name;
            ctx.drawString(textRenderer, Component.literal(text), itemX + 20, y + 5, this.getDefaultColor(), true);
        } else {
            ctx.drawString(textRenderer, Component.translatable("gameinfo.hud.furnace.empty"), itemX, y + 5, -8355712, true);
        }
    }

    private void renderPlayerEquipment(GuiGraphics drawContext) {
        if (!HudOverlay.config.showEquipment) {
            return;
        }
        LocalPlayer player = HudOverlay.client.player;
        if (player == null || HudOverlay.client.options.hideGui) {
            return;
        }
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int startX = screenWidth / 2 + 91 + 10;
        int startY = screenHeight - 22;
        List<ItemStack> durableItems = HudOverlay.getShowItemStacks(player);
        for (int i = 0; i < durableItems.size(); ++i) {
            ItemStack stack = durableItems.get(i);
            int currentY = startY - i * 20;
            drawContext.renderItem(stack, startX, currentY);
            int maxDamage = stack.getMaxDamage();
            int durability = stack.getMaxDamage() - stack.getDamageValue();
            String durabilityText = String.format("%d/%d", durability, maxDamage);
            int textX = startX + 20;
            int textY = currentY + 5;
            drawContext.drawString(textRenderer, durabilityText, textX, textY, this.getMainColor(), true);
        }
    }

    private static List<ItemStack> getShowItemStacks(LocalPlayer player) {
        ArrayList<ItemStack> durableItems = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) {
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            if (DurabilityRecord.hasExperienceMending(stack)) {
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            if (!DurabilityRecord.hasDurabilityChangedRecently(slot, stack, currentTime)) continue;
            durableItems.add(stack);
        }
        return durableItems;
    }

    private void renderRemark(GuiGraphics drawContext) {
        if (!HudOverlay.config.remark) {
            return;
        }
        MutableComponent versionLabel = Component.translatable("gameinfo.hud.version");
        String versionValue = HudOverlay.config.version != null && !HudOverlay.config.version.trim().isEmpty() ? HudOverlay.config.version : SharedConstants.getCurrentVersion().getName();
        int y = 2;
        int x = client.getWindow().getGuiScaledWidth() - textRenderer.width(versionLabel) - textRenderer.width(versionValue) - 2;
        drawContext.drawString(textRenderer, versionLabel, x, y, this.getMainColor(), true);
        drawContext.drawString(textRenderer, versionValue, x += textRenderer.width(versionLabel), y, this.getDefaultColor(), true);
    }

    private int renderFPS(GuiGraphics drawContext, int x, int y) {
        if (!HudOverlay.config.showFPS) {
            return 0;
        }
        int fps = client.getFps();
        MutableComponent fpsLabel = Component.translatable("gameinfo.hud.fps");
        drawContext.drawString(textRenderer, fpsLabel, x, y, this.getMainColor(), true);
        int width = textRenderer.width(fpsLabel);
        MutableComponent fpsValue = Component.literal(fps == -1 ? Component.translatable("gameinfo.hud.unknown").getString() : String.valueOf(fps));
        drawContext.drawString(textRenderer, fpsValue, x + width, y, this.getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderTimeAndDays(GuiGraphics drawContext, int x, int y) {
        if (!HudOverlay.config.showTimeAndDays) {
            return 0;
        }
        LocalPlayer player = HudOverlay.client.player;
        if (player == null) {
            return 0;
        }
        Level world = player.level();
        long dayTime = world.getDayTime();
        long timeOfDay = dayTime % 24000L;
        int hours = (int) ((6L + timeOfDay / 1000L) % 24L);
        int minutes = (int) (timeOfDay % 1000L * 60L / 1000L);
        int days = (int) (dayTime / 24000L);
        MutableComponent daysLabel = Component.translatable("gameinfo.hud.days");
        drawContext.drawString(textRenderer, daysLabel, x, y, this.getMainColor(), true);
        int width = textRenderer.width(daysLabel);
        drawContext.drawString(textRenderer, Component.literal(String.valueOf(days)), x + width, y, this.getDefaultColor(), true);
        MutableComponent timeLabel = Component.translatable("gameinfo.hud.time");
        drawContext.drawString(textRenderer, timeLabel, x + (width += textRenderer.width(String.valueOf(days))), y, this.getMainColor(), true);
        String formatDate = String.format("%02d:%02d", hours, minutes);
        drawContext.drawString(textRenderer, Component.literal(formatDate), x + (width += textRenderer.width(timeLabel)), y, this.getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderCoordinates(GuiGraphics drawContext, int x, int y) {
        if (!HudOverlay.config.showCoordinates) {
            return 0;
        }
        LocalPlayer player = HudOverlay.client.player;
        if (player == null) {
            return 0;
        }
        BlockPos pos = player.blockPosition();
        String directionString = this.getDirectionString();
        MutableComponent xyzLabel = Component.translatable("gameinfo.hud.xyz");
        drawContext.drawString(textRenderer, xyzLabel, x, y, this.getMainColor(), true);
        int width = textRenderer.width(xyzLabel);
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.drawString(textRenderer, Component.literal(xyz), x + width, y, this.getDefaultColor(), true);
        if (HudOverlay.config.showSlimeChunk && this.isSlimeChunk(pos)) {
            int xyzWidth = textRenderer.width(xyz);
            MutableComponent slimeText = Component.literal(" - ").append(Component.translatable("gameinfo.hud.slime_chunk"));
            drawContext.drawString(textRenderer, slimeText, x + width + xyzWidth, y, SLIME_CHUNK_COLOR, true);
        }
        return DEFAULT_HEIGHT;
    }

    private boolean isSlimeChunk(BlockPos pos) {
        if (HudOverlay.client.level == null) {
            return false;
        }
        long seed = this.getWorldSeed();
        if (seed == 0L) {
            return false;
        }
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return SlimeChunkUtil.isSlimeChunk(seed, chunkX, chunkZ);
    }

    private long getWorldSeed() {
        IntegratedServer server;
        if (client.hasSingleplayerServer() && (server = client.getSingleplayerServer()) != null) {
            return server.overworld().getSeed();
        }
        return 0L;
    }

    private int renderNetherCoordinates(GuiGraphics drawContext, int x, int y) {
        if (!HudOverlay.config.showNetherCoordinates) {
            return 0;
        }
        LocalPlayer player = HudOverlay.client.player;
        if (player == null) {
            return 0;
        }
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        String coordinateText = "";
        String labelKey = "gameinfo.hud.nether";
        if (world.dimension().location().equals(Level.OVERWORLD.location())) {
            coordinateText = String.format("%d %d", pos.getX() / 8, pos.getZ() / 8);
            labelKey = "gameinfo.hud.nether";
        } else if (world.dimension().location().equals(Level.NETHER.location())) {
            coordinateText = String.format("%d %d", pos.getX() * 8, pos.getZ() * 8);
            labelKey = "gameinfo.hud.overworld";
        }
        MutableComponent coordinateLabel = Component.translatable(labelKey);
        drawContext.drawString(textRenderer, coordinateLabel, x, y, this.getMainColor(), true);
        int width = textRenderer.width(coordinateLabel);
        drawContext.drawString(textRenderer, Component.literal(coordinateText), x + width, y, this.getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderBiome(GuiGraphics drawContext, int x, int y) {
        String biomeName;
        BlockPos pos;
        if (!HudOverlay.config.showBiome) {
            return 0;
        }
        LocalPlayer player = HudOverlay.client.player;
        if (player == null) {
            return 0;
        }
        Level world = player.level();
        Holder<Biome> biomeEntry = world.getBiome(pos = player.blockPosition());
        ResourceLocation biomeId = biomeEntry.unwrapKey().map(ResourceKey::location).orElse(null);
        if (biomeId == null) {
            biomeName = Component.translatable("gameinfo.hud.unknown_biome").getString();
        } else {
            String translationKey = "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
            biomeName = Component.translatable(translationKey).getString();
        }
        MutableComponent biomeLabel = Component.translatable("gameinfo.hud.biome");
        drawContext.drawString(textRenderer, biomeLabel, x, y, this.getMainColor(), true);
        int width = textRenderer.width(biomeLabel);
        drawContext.drawString(textRenderer, Component.literal(biomeName), x + width, y, this.getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private String getDirectionString() {
        Direction direction = null;
        if (HudOverlay.client.player != null) {
            direction = HudOverlay.client.player.getDirection();
        }
        if (direction == null) {
            return Component.translatable("gameinfo.direction.unknown").getString();
        }
        String key = switch (direction) {
            case Direction.NORTH -> "gameinfo.direction.north";
            case Direction.SOUTH -> "gameinfo.direction.south";
            case Direction.EAST -> "gameinfo.direction.east";
            case Direction.WEST -> "gameinfo.direction.west";
            default -> "gameinfo.direction.unknown";
        };
        return Component.translatable(key).getString();
    }

    static {
        DEFAULT_COLOR = -1;
        DEFAULT_HEIGHT = 10;
        color = DEFAULT_COLOR;
        armorSlots = new EquipmentSlot[]{EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    }
}
