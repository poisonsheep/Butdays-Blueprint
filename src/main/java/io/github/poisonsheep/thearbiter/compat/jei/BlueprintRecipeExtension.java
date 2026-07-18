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
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public class BlueprintRecipeExtension implements ICraftingCategoryExtension {
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
    }
    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        minecraft.player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).ifPresent(playerBlueprint -> {
            if (!playerBlueprint.getBlueprints().contains(recipe.getBlueprint())) {
                int arrowX = recipeWidth / 2 + 16 - 8;
                int arrowY = recipeHeight / 2 - 8;
                // draw the required blueprint item above the arrow
                ItemStack blueprintStack = new ItemStack(ItemRegistry.BLUEPRINT.get());
                Blueprint.setBluePrint(blueprintStack, new ResourceLocation(recipe.getBlueprint()));
                guiGraphics.renderItem(blueprintStack, arrowX, arrowY - 18);
                // draw X over the arrow
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(arrowX, arrowY, 0);
                guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
                guiGraphics.blit(BlueprintAnthologyScreen.BOOK_TEXTURES, 0, 0, 18, 233, 8, 8);
                guiGraphics.pose().popPose();
            }
        });
    }
}
