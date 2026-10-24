package com.codar5.gameinfo.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final ColorSelectedCallback callback;
    private final int[] colorPalette = new int[]{-1, -2039584, -4144960, -6250336, -8355712, -10461088, -12566464, -16777216, -65536, -49088, -32640, -49152, -3407872, -8388608, -12582912, -8388544, -16711936, -12517568, -8323200, -16711872, -16724992, -16744448, -16760832, -12550144, -16776961, -12566273, -8355585, -16760577, -16777012, -16777088, -16777152, -16760704};
    private EditBox colorInputField;
    private Button confirmCustomButton;
    private int previewColor = -1;

    public ColorPickerScreen(Screen parent, ColorSelectedCallback callback) {
        super(Component.translatable("gameinfo.color_picker.title"));
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    protected void init() {
        int colorSize = 32;
        int spacing = 8;
        int cols = 8;
        int rows = 4;
        int gridWidth = cols * colorSize + (cols - 1) * spacing;
        int gridHeight = rows * colorSize + (rows - 1) * spacing;
        int startX = (this.width - gridWidth) / 2;
        int startY = 50;
        for (int i = 0; i < this.colorPalette.length; ++i) {
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * (colorSize + spacing);
            int y = startY + row * (colorSize + spacing);
            int colorIndex = i;
            this.addRenderableWidget(new ColorButton(x, y, colorSize, colorSize, this.colorPalette[i], button -> {
                this.callback.onColorSelected(this.colorPalette[colorIndex], "");
                this.onClose();
            }));
        }
        int customY = startY + gridHeight + 25;
        int inputWidth = 100;
        int inputHeight = 20;
        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50;
        int elementStartX = (this.width - totalElementWidth) / 2;
        this.colorInputField = new EditBox(this.font, elementStartX, customY + 20, inputWidth, inputHeight, Component.translatable("gameinfo.color_picker.custom_title"));
        this.colorInputField.setHint(Component.translatable("gameinfo.color_picker.input_hint"));
        this.colorInputField.setValue("FFFFFFFF");
        this.colorInputField.setResponder(this::onColorInputChanged);
        this.addRenderableWidget(this.colorInputField);
        this.confirmCustomButton = this.addRenderableWidget(Button.builder(Component.translatable("gameinfo.color_picker.use"), button -> {
            try {
                String input = this.colorInputField.getValue().trim();
                if (input.length() == 8) {
                    int color = (int) Long.parseLong(input, 16);
                    this.callback.onColorSelected(color, "");
                    this.onClose();
                }
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }).bounds(elementStartX + inputWidth + 25 + 20 + 10, customY + 20, 50, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gameinfo.gui.button.cancel"), button -> this.onClose()).bounds(elementStartX + inputWidth + 25 + 20 + 10, customY + 50, 50, 20).build());
    }

    private void onColorInputChanged(String input) {
        try {
            if (input.length() == 8) {
                this.previewColor = (int) Long.parseLong(input, 16);
            }
        } catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, Integer.MIN_VALUE);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 20, -10496);
        context.drawCenteredString(this.font, Component.translatable("gameinfo.color_picker.presets"), this.width / 2, 35, -1);
        this.renderCustomColorArea(context);
    }

    private void renderCustomColorArea(GuiGraphics context) {
        int customY = 227;
        int bgX = this.width / 2 - 130;
        int bgWidth = 260;
        int bgHeight = 80;
        context.fill(bgX, customY - 5, bgX + bgWidth, customY + bgHeight, 0x30333333);
        context.drawCenteredString(this.font, Component.translatable("gameinfo.color_picker.custom_title"), this.width / 2, customY + 8, -5570646);
        int inputWidth = 100;
        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50;
        int elementStartX = (this.width - totalElementWidth) / 2;
        int previewSize = 20;
        int previewX = elementStartX + inputWidth + 25;
        int previewY = customY + 20;
        context.fill(previewX - 1, previewY - 1, previewX + previewSize + 1, previewY + previewSize + 1, -16777216);
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, this.previewColor);
        context.drawString(this.font, Component.translatable("gameinfo.color_picker.format_hint"), this.width / 2 - 120, customY + 50, -7829368);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public interface ColorSelectedCallback {
        void onColorSelected(int var1, String var2);
    }

    private static class ColorButton extends Button {
        private final int color;

        public ColorButton(int x, int y, int width, int height, int color, Button.OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.color = color;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.color);
            if (this.isHovered()) {
                int x = this.getX();
                int y = this.getY();
                int w = this.width;
                int h = this.height;
                int borderColor = -1;
                context.fill(x - 1, y - 1, x + w + 1, y, borderColor);
                context.fill(x - 1, y + h, x + w + 1, y + h + 1, borderColor);
                context.fill(x - 1, y, x, y + h, borderColor);
                context.fill(x + w, y, x + w + 1, y + h, borderColor);
            }
        }
    }
}
