package io.github.poisonsheep.thearbiter.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.poisonsheep.thearbiter.Item.Blueprint;
import io.github.poisonsheep.thearbiter.Item.ItemRegistry;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprintProvider;
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
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class BlueprintRecipeExtension implements ICraftingCategoryExtension {
    private static final ResourceLocation WIDGETS_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/widgets.png");
    private final BlueprintRecipe recipe;
    private boolean wasLeftDown = false;

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
            int iconX = recipeWidth - 4;
            int iconY = -8;
            // unlocked only when ALL required blueprints are learned
            boolean unlocked = recipe.getBlueprints().stream()
                    .allMatch(playerBlueprint.getBlueprints()::contains);

            if (!unlocked) {
                // force our elements to render on top of JEI widgets
                RenderSystem.enableDepthTest();

                int arrowX = recipeWidth / 2 + 16 - 8;
                int arrowY = recipeHeight / 2 - 8;

                // draw blueprint item(s) above the arrow — stacked like cards
                List<String> bpList = recipe.getBlueprints();
                int bpCount = bpList.size();
                int stackWidth = 16 + (bpCount - 1) * 8;
                int itemX = arrowX + 6 - stackWidth / 2;
                int itemY = arrowY - 18;
                for (int i = bpCount - 1; i >= 0; i--) {
                    ItemStack bpStack = new ItemStack(ItemRegistry.BLUEPRINT.get());
                    Blueprint.setBluePrint(bpStack, ResourceLocation.parse(bpList.get(i)));
                    guiGraphics.renderItem(bpStack, itemX + i * 8, itemY);
                }

                boolean hoveringItem = mouseX >= itemX && mouseX < itemX + 16 + (bpCount - 1) * 8
                        && mouseY >= itemY && mouseY < itemY + 16;

                // draw lock icon at top-right corner
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(iconX, iconY, 300);
                guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
                guiGraphics.blit(WIDGETS_TEXTURE, 0, 0, 0, 146, 20, 20, 256, 256);
                guiGraphics.pose().popPose();

                // tooltip + click for blueprint item
                if (hoveringItem) {
                    int tipX = 4;
                    int tipY = itemY - 12;
                    List<Component> lines = new java.util.ArrayList<>();
                    if (bpList.size() == 1) {
                        String bp = bpList.get(0);
                        boolean learned = playerBlueprint.getBlueprints().contains(bp);
                        String mark = learned ? " §a✓" : " §c✕";
                        lines.add(Component.translatable("jei.butdaysblueprint.blueprint_required"));
                        lines.add(Component.literal("  ").append(Component.translatable(bp.replace(":", ".")))
                                .append(Component.literal(mark)));
                    } else {
                        lines.add(Component.translatable("jei.butdaysblueprint.blueprints_required"));
                        for (String bp : bpList) {
                            boolean learned = playerBlueprint.getBlueprints().contains(bp);
                            String mark = learned ? " §a✓" : " §c✕";
                            lines.add(Component.literal("  ").append(Component.translatable(bp.replace(":", ".")))
                                    .append(Component.literal(mark)));
                        }
                    }
                    guiGraphics.renderComponentTooltip(minecraft.font, lines, tipX, tipY);
                }

                // click blueprint item → navigate to the clicked one
                long window = minecraft.getWindow().getWindow();
                boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
                if (leftDown && !wasLeftDown && hoveringItem && JeiPlugin.runTime != null) {
                    // find which card was clicked based on mouse X
                    int clickedIdx = 0;
                    for (int i = 0; i < bpCount; i++) {
                        if (mouseX >= itemX + i * 8 && mouseX < itemX + i * 8 + 16) {
                            clickedIdx = i;
                            break;
                        }
                    }
                    ItemStack bpCopy = new ItemStack(ItemRegistry.BLUEPRINT.get());
                    Blueprint.setBluePrint(bpCopy, ResourceLocation.parse(bpList.get(clickedIdx)));
                    minecraft.execute(() -> {
                        if (JeiPlugin.runTime != null && JeiPlugin.runTime.getRecipesGui() != null) {
                            JeiPlugin.runTime.getRecipesGui().show(List.of(
                                    JeiPlugin.runTime.getJeiHelpers().getFocusFactory()
                                            .createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, bpCopy)
                            ));
                        }
                    });
                }
                wasLeftDown = leftDown;

                // tooltip for lock icon
                boolean hoveringLock = mouseX >= iconX && mouseX < iconX + 12 && mouseY >= iconY && mouseY < iconY + 12;
                if (hoveringLock) {
                    guiGraphics.renderTooltip(minecraft.font,
                            Component.translatable("jei.butdaysblueprint.recipe_locked"), (int) mouseX, (int) mouseY);
                }
                RenderSystem.disableDepthTest();
            } else {
                // draw unlock icon at top-right corner when recipe is learned
                RenderSystem.enableDepthTest();
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(iconX, iconY, 300);
                guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
                guiGraphics.blit(WIDGETS_TEXTURE, 0, 0, 20, 146, 20, 20, 256, 256);
                guiGraphics.pose().popPose();

                // tooltip for unlock icon
                boolean hoveringUnlock = mouseX >= iconX && mouseX < iconX + 12 && mouseY >= iconY && mouseY < iconY + 12;
                if (hoveringUnlock) {
                    guiGraphics.renderTooltip(minecraft.font,
                            Component.translatable("jei.butdaysblueprint.recipe_unlocked"), (int) mouseX, (int) mouseY);
                }
                RenderSystem.disableDepthTest();
            }
        });
    }
}
