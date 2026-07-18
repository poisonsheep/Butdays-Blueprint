package io.github.poisonsheep.thearbiter.client.gui;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BlueprintAnthologyScreen extends BasicBookScreen {
    public static final ResourceLocation BLUEPRINT_ANTHOLOGY_TEXTURE = new ResourceLocation(ButdaysBlueprint.MODID,"textures/gui/blueprint_anthology_gui.png");
    public static final ResourceLocation BOOK_TEXTURES = new ResourceLocation(ButdaysBlueprint.MODID, "textures/gui/ui.png");
    Component title  = Component.translatable("butdaysblueprint.title");
    Component author  = Component.translatable("butdaysblueprint.author");
    Component textComponent = Component.translatable("butdaysblueprint.intro");
    Component preface  = Component.translatable("butdaysblueprint.title.preface");

    Component viewBlueprint  = Component.translatable("butdaysblueprint.tip.viewBlueprint");
    private ScrollableText scrollableText;
    int toolTipMaxWidth;
    public BlueprintAnthologyScreen(Component component) {
        super(component);
    }

    @Override
    protected void init() {
        super.init();
        this.toolTipMaxWidth = (IMAGE_WIDTH / 2) - 20;
        int textStartHeight = this.bottomPos + 56;
        int y1 = this.topPos - 36;
        this.scrollableText = new ScrollableText(textComponent, (this.IMAGE_WIDTH / 2) - 46, y1 - textStartHeight -24, textStartHeight, y1);
        this.scrollableText.setLeftPos(this.leftPos + this.IMAGE_WIDTH / 2 + 18);
        this.addRenderableWidget(scrollableText);
        PageButton pageForward = new PageButton(this.rightPos - 42, this.topPos - 36, true, button -> {
            this.minecraft.setScreen(new BlueprintViewScreen(this));
        }, true);
        this.addRenderableWidget(pageForward);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderLogo(guiGraphics);
        writeTitle(guiGraphics, preface, this.leftPos + Math.round(this.IMAGE_WIDTH * 3 / 4) - 14, this.bottomPos + 34, 2F);
        writeTitle(guiGraphics, title,this.leftPos + 30, this.bottomPos + this.IMAGE_HEIGHT / 2 - 4, 0.9F);
        writeTitle(guiGraphics, author,this.leftPos + 56, this.bottomPos + this.IMAGE_HEIGHT / 2 + 4, 0.6F);
    }

    protected void renderLogo(GuiGraphics guiGraphics) {
        float toolTipMaxWidthScaled = this.toolTipMaxWidth;
        guiGraphics.blit(BOOK_TEXTURES, Math.round(this.leftPos + (IMAGE_WIDTH / 2 - toolTipMaxWidthScaled) / 2) + 30, Math.round(this.bottomPos + 25), 0, 64, 64, 64);
    }
    protected void writeTitle(GuiGraphics guiGraphics, Component component, int x, int y, float scale) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        this.font.drawInBatch(component.getVisualOrderText(), 0, 0, 0x000000, false,
            guiGraphics.pose().last().pose(), guiGraphics.bufferSource(),
            Font.DisplayMode.NORMAL, 0, 15728880);
        guiGraphics.pose().popPose();
    }
}
