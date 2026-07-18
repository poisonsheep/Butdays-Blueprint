package io.github.poisonsheep.thearbiter.compat.jei;

import io.github.poisonsheep.thearbiter.Item.Blueprint;
import io.github.poisonsheep.thearbiter.Item.ItemRegistry;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprintProvider;
import io.github.poisonsheep.thearbiter.client.gui.BlueprintAnthologyScreen;
import io.github.poisonsheep.thearbiter.recipe.BlueprintRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public class BlueprintRecipeExtension implements ICraftingCategoryExtension {
    private static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("minecraft", "textures/gui/widgets.png");
    private final BlueprintRecipe recipe;

    public BlueprintRecipeExtension(BlueprintRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        IIngredientType<ItemStack> item = VanillaTypes.ITEM_STACK;
        craftingGridHelper.createAndSetOutputs(builder, item, List.of(RecipeUtil.getResultItem(recipe)));
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        craftingGridHelper.createAndSetInputs(builder, item, recipe.getIngredients()
                .stream()
                .map(Ingredient::getItems)
                .map(Arrays::asList)
                .toList(), width, height);

        // add blueprint as a real JEI catalyst slot above the arrow (clickable, navigates to blueprint JEI page)
        ItemStack bpStack = new ItemStack(ItemRegistry.BLUEPRINT.get());
        Blueprint.setBluePrint(bpStack, new ResourceLocation(recipe.getBlueprint()));
        builder.addSlot(RecipeIngredientRole.CATALYST, 1 + 18 * width + 9, 2).addItemStack(bpStack);
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        minecraft.player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).ifPresent(playerBlueprint -> {
            int iconX = recipeWidth - 13;
            int iconY = 0;
            boolean unlocked = playerBlueprint.getBlueprints().contains(recipe.getBlueprint());

            if (!unlocked) {
                int arrowX = recipeWidth / 2 + 16 - 8;
                int arrowY = recipeHeight / 2 - 8;

                // draw red X over the arrow
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(arrowX, arrowY + 2, 0);
                guiGraphics.pose().scale(1.5F, 1.5F, 1.0F);
                guiGraphics.blit(BlueprintAnthologyScreen.BOOK_TEXTURES, 0, 0, 18, 233, 8, 8);
                guiGraphics.pose().popPose();

                // draw lock icon at top-right corner
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(iconX, iconY, 0);
                guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
                guiGraphics.blit(WIDGETS_TEXTURE, 0, 0, 0, 146, 20, 20, 256, 256);
                guiGraphics.pose().popPose();

                // tooltip for lock icon
                boolean hoveringLock = mouseX >= iconX && mouseX < iconX + 12 && mouseY >= iconY && mouseY < iconY + 12;
                if (hoveringLock) {
                    guiGraphics.renderTooltip(minecraft.font,
                            Component.translatable("jei.butdaysblueprint.recipe_locked"), (int) mouseX, (int) mouseY);
                }
            } else {
                // draw unlock icon at top-right corner when recipe is learned
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(iconX, iconY, 0);
                guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
                guiGraphics.blit(WIDGETS_TEXTURE, 0, 0, 20, 146, 20, 20, 256, 256);
                guiGraphics.pose().popPose();

                // tooltip for unlock icon
                boolean hoveringUnlock = mouseX >= iconX && mouseX < iconX + 12 && mouseY >= iconY && mouseY < iconY + 12;
                if (hoveringUnlock) {
                    guiGraphics.renderTooltip(minecraft.font,
                            Component.translatable("jei.butdaysblueprint.recipe_unlocked"), (int) mouseX, (int) mouseY);
                }
            }
        });
    }
}
