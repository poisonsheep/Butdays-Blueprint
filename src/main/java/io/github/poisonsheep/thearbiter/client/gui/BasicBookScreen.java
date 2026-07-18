package io.github.poisonsheep.thearbiter.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BasicBookScreen extends Screen {
    protected static final int IMAGE_WIDTH = 288;
    protected static final int IMAGE_HEIGHT = 208;
    protected int leftPos;
    protected int bottomPos;
    protected int rightPos;
    protected int topPos;
    protected int startXRightPage;
    protected int startXLeftPage;
    protected int pageBackButtonX = this.leftPos + 5;
    protected int pageButtonY = this.topPos - 10;
    protected int pageButtonForwardX = this.rightPos - 5;

    public BasicBookScreen(Component title) {
        super(title);
    }
    @Override
    protected void init() {
        super.init();
        this.leftPos = ((this.width - IMAGE_WIDTH) / 2);
        this.bottomPos = (this.height - IMAGE_HEIGHT) / 2 - 15;
        this.rightPos = this.leftPos + IMAGE_WIDTH;
        this.topPos = this.bottomPos + IMAGE_HEIGHT;
        this.startXRightPage = (this.leftPos + (IMAGE_WIDTH / 4) + ((IMAGE_WIDTH) / 3)) - 18;
        this.startXLeftPage = this.leftPos + 15;
        this.pageBackButtonX = this.leftPos + 15;
        this.pageButtonY = this.topPos - 13 - 13;
        this.pageButtonForwardX = this.rightPos - 23 - 22;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        renderAnthology(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void renderAnthology(GuiGraphics guiGraphics) {
        guiGraphics.blit(BlueprintAnthologyScreen.BLUEPRINT_ANTHOLOGY_TEXTURE, this.leftPos, this.bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
    }
}
