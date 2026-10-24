package com.codar5.gameinfo.gui;

import com.codar5.gameinfo.GameInfoNeoForge;
import com.codar5.gameinfo.config.GameInfoConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;
    private GameInfoConfig backupConfig;
    private int currentTab = 0;
    private final List<TabButton> tabButtons = new ArrayList<>();
    private static final String[] TAB_KEYS = new String[]{"gameinfo.gui.tab.toggles", "gameinfo.gui.tab.color"};
    private Button fpsButton;
    private Button timeButton;
    private Button slimeChunkButton;
    private Button coordButton;
    private Button netherButton;
    private Button biomeButton;
    private Button remarkButton;
    private Button showEquipmentButton;
    private Button showFurnaceInfoButton;
    private Button showEntityInfoButton;
    private Button showBlockInfoButton;
    private Button enableButton;
    private EditBox xPosField;
    private EditBox yPosField;
    private EditBox versionField;
    private Button colorButton;

    public GameInfoConfigScreen(Screen parent) {
        super(Component.translatable("gameinfo.gui.title"));
        this.parent = parent;
        this.config = GameInfoNeoForge.config;
        this.backupConfig = new GameInfoConfig();
        this.backupConfig.copyFrom(this.config);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.createTabButtons();
        if (this.currentTab == 0) {
            this.createInfoToggleTab();
        } else if (this.currentTab == 1) {
            this.createColorSettingTab();
        }
        this.createBottomButtons();
    }

    private void createTabButtons() {
        this.tabButtons.clear();
        int centerX = this.width / 2;
        int tabWidth = 80;
        int tabHeight = 20;
        int startX = centerX - TAB_KEYS.length * tabWidth / 2;
        for (int i = 0; i < TAB_KEYS.length; ++i) {
            int tabIndex = i;
            TabButton tabButton = new TabButton(startX + i * tabWidth, 35, tabWidth, tabHeight, TAB_KEYS[i], tabIndex == this.currentTab, () -> this.switchTab(tabIndex));
            this.tabButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
        }
    }

    private void switchTab(int newTab) {
        if (newTab != this.currentTab) {
            this.currentTab = newTab;
            this.init();
        }
    }

    private void createInfoToggleTab() {
        int centerX = this.width / 2;
        int startY = 80;
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 25;
        int sideMargin = 40;
        int leftX = centerX - buttonWidth - sideMargin;
        this.fpsButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.fps", () -> this.config.showFPS, value -> {
            this.config.showFPS = value;
        }, leftX, startY, buttonWidth, buttonHeight));
        this.timeButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.time", () -> this.config.showTimeAndDays, value -> {
            this.config.showTimeAndDays = value;
        }, leftX, startY + spacing, buttonWidth, buttonHeight));
        this.coordButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.coordinates", () -> this.config.showCoordinates, value -> {
            this.config.showCoordinates = value;
        }, leftX, startY + spacing * 2, buttonWidth, buttonHeight));
        this.netherButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.nether", () -> this.config.showNetherCoordinates, value -> {
            this.config.showNetherCoordinates = value;
        }, leftX, startY + spacing * 3, buttonWidth, buttonHeight));
        this.enableButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.enabled", () -> this.config.enabled, value -> {
            this.config.enabled = value;
            System.out.println("[GameInfo] Enable HUD: " + value);
        }, leftX, startY + spacing * 4, buttonWidth, buttonHeight));
        this.slimeChunkButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.slime_chunk", () -> this.config.showSlimeChunk, value -> {
            this.config.showSlimeChunk = value;
        }, leftX, startY + spacing * 5, buttonWidth, buttonHeight));
        int rightX = centerX + sideMargin;
        this.biomeButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.biome", () -> this.config.showBiome, value -> {
            this.config.showBiome = value;
        }, rightX, startY, buttonWidth, buttonHeight));
        this.showEquipmentButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.equipment", () -> this.config.showEquipment, value -> {
            this.config.showEquipment = value;
        }, rightX, startY + spacing, buttonWidth, buttonHeight));
        this.remarkButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.remark", () -> this.config.remark, value -> {
            this.config.remark = value;
        }, rightX, startY + spacing * 2, buttonWidth, buttonHeight));
        this.showEntityInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.entity_info", () -> this.config.showEntityInfo, value -> {
            this.config.showEntityInfo = value;
        }, rightX, startY + spacing * 3, buttonWidth, buttonHeight));
        this.showBlockInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.block_info", () -> this.config.showBlockInfo, value -> {
            this.config.showBlockInfo = value;
        }, rightX, startY + spacing * 4, buttonWidth, buttonHeight));
        this.showFurnaceInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton("gameinfo.gui.toggle.furnace_info", () -> this.config.showFurnaceInfo, value -> {
            this.config.showFurnaceInfo = value;
        }, rightX, startY + spacing * 5, buttonWidth, buttonHeight));
    }

    private void createColorSettingTab() {
        int centerX = this.width / 2;
        int startY = 95;
        int spacing = 30;
        this.xPosField = this.addRenderableWidget(GuiHelper.createNumberField(this.font, "gameinfo.gui.label.field.x_pos", () -> this.config.xPos, value -> {
            this.config.xPos = value;
        }, centerX - 65, startY, 50, 20));
        this.yPosField = this.addRenderableWidget(GuiHelper.createNumberField(this.font, "gameinfo.gui.label.field.y_pos", () -> this.config.yPos, value -> {
            this.config.yPos = value;
        }, centerX + 15, startY, 50, 20));
        this.colorButton = this.addRenderableWidget(Button.builder(Component.translatable("gameinfo.gui.button.select_color"), button -> this.minecraft.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
            this.config.color = color;
        }))).bounds(centerX - 50, startY + spacing, 100, 20).build());
        int versionY = startY + spacing * 2 + 10;
        this.versionField = this.addRenderableWidget(GuiHelper.createTextField(this.font, "gameinfo.gui.label.field.version", () -> this.config.version, value -> {
            this.config.version = value;
        }, centerX - 60, versionY, 120, 20));
    }

    private void createBottomButtons() {
        int centerX = this.width / 2;
        int bottomY = this.height - 40;
        this.addRenderableWidget(GuiHelper.createButton("gameinfo.gui.button.save", () -> {
            this.config.saveConfig();
            this.backupConfig.copyFrom(this.config);
            this.onClose();
        }, centerX - 55, bottomY, 50, 20));
        this.addRenderableWidget(GuiHelper.createButton("gameinfo.gui.button.cancel", () -> {
            this.config.copyFrom(this.backupConfig);
            this.onClose();
        }, centerX + 5, bottomY, 50, 20));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x50000000);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, -10496);
        int tabContentY = 60;
        int tabContentHeight = this.height - 110;
        context.fill(20, tabContentY, this.width - 20, tabContentY + tabContentHeight, 0x20000000);
        if (this.currentTab == 0) {
            this.renderInfoToggleTab(context);
        } else if (this.currentTab == 1) {
            this.renderColorSettingTab(context);
        }
    }

    private void renderInfoToggleTab(GuiGraphics context) {
        int centerX = this.width / 2;
        context.drawCenteredString(this.font, Component.translatable("gameinfo.gui.section.select_info"), centerX, 70, -5570646);
    }

    private void renderColorSettingTab(GuiGraphics context) {
        int centerX = this.width / 2;
        int startY = 95;
        int spacing = 30;
        int labelGap = 5;
        context.drawCenteredString(this.font, Component.translatable("gameinfo.gui.section.customize"), centerX, 70, -5570646);
        MutableComponent xLabel = Component.translatable("gameinfo.gui.label.x_pos");
        int xLabelX = centerX - 65 - labelGap - this.font.width(xLabel);
        context.drawString(this.font, xLabel, xLabelX, startY + 6, -1);
        MutableComponent yLabel = Component.translatable("gameinfo.gui.label.y_pos");
        int yLabelX = centerX + 15 - labelGap - this.font.width(yLabel);
        context.drawString(this.font, yLabel, yLabelX, startY + 6, -1);
        int colorSize = 24;
        int colorX = centerX + 65;
        int colorY = startY + spacing - 2;
        context.fill(colorX - 1, colorY - 1, colorX + colorSize + 1, colorY + colorSize + 1, -16777216);
        context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, this.config.color);
        int versionY = startY + spacing * 2 + 10;
        MutableComponent versionLabel = Component.translatable("gameinfo.gui.label.version");
        int versionLabelX = centerX - 60 - labelGap - this.font.width(versionLabel);
        context.drawString(this.font, versionLabel, versionLabelX, versionY + 6, -1);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class TabButton extends Button {
        private final boolean isActive;

        public TabButton(int x, int y, int width, int height, String translationKey, boolean isActive, Runnable onPress) {
            super(x, y, width, height, Component.translatable(translationKey), button -> onPress.run(), DEFAULT_NARRATION);
            this.isActive = isActive;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
            int textColor;
            int bgColor = this.isActive ? -11908534 : -14013910;
            textColor = this.isActive ? -10496 : -3355444;
            if (this.isHovered() && !this.isActive) {
                bgColor = -12961222;
            }
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
            if (this.isActive) {
                context.fill(this.getX() + 1, this.getY() + this.height - 1, this.getX() + this.width - 1, this.getY() + this.height, bgColor);
            }
            int textX = this.getX() + this.width / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), textX, textY, textColor);
        }
    }
}
