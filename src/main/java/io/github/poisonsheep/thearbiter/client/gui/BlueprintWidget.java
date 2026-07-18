package io.github.poisonsheep.thearbiter.client.gui;

import io.github.poisonsheep.thearbiter.capability.PlayerBlueprint;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprintProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BlueprintWidget extends ItemWidget {
    private final String blueprint;
    private Player player = Minecraft.getInstance().player;

    public BlueprintWidget(ItemStack stack, ItemRenderer itemRenderer, String blueprint, int pX, int pY, int pWidth, int pHeight, OnClick onClick) {
        super(stack, itemRenderer, pX, pY, pWidth, pHeight, onClick);
        this.blueprint = blueprint;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if(Screen.hasShiftDown()) {
            if(player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).isPresent()) {
                PlayerBlueprint playerBlueprint = player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).orElseThrow(() -> new RuntimeException("Player does not have PlayerBlueprint capability"));
                List<String> blueprints = playerBlueprint.getBlueprints();
                if(blueprints.contains(blueprint)) {
                    guiGraphics.blit(BlueprintAnthologyScreen.BOOK_TEXTURES, this.getX() - 1, this.getY() - 3, 0, 236, 18, 20);
                }
            }
        }
    }
}
