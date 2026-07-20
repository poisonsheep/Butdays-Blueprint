package io.github.poisonsheep.thearbiter.client.gui;

import io.github.poisonsheep.thearbiter.item.Blueprint;
import io.github.poisonsheep.thearbiter.item.ItemRegistry;
import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.client.blueprint.BlueprintList;
import io.github.poisonsheep.thearbiter.client.misc.RecipeData;
import io.github.poisonsheep.thearbiter.recipe.RecipeDataList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlueprintViewScreen extends BasicBookScreen {

    public static final ResourceLocation BLUEPRINT_ANTHOLOGY_VIEW_TEXTURE = ResourceLocation.fromNamespaceAndPath(ButdaysBlueprint.MODID,"textures/gui/blueprint_anthology_gui2.png");
    int page;
    private final Screen parent;
    int maxPagePairCount;
    BlueprintWidget[][][][] items;

    protected BlueprintViewScreen(Screen parent) {
        super(Component.literal(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        createMenu();
        load(this.page);
        PageButton pageBack = new PageButton(this.leftPos + 18, this.topPos - 36, false, button -> {
            if(page == 0) {
                this.minecraft.setScreen(this.parent);
            }
            unload(page);
            this.page = page == 0 ? maxPagePairCount - 1 : (page - 1) % maxPagePairCount;
            load(this.page);
        }, true);
        this.addRenderableWidget(pageBack);
        PageButton pageForward = new PageButton(this.rightPos - 42, this.topPos - 36, true, button -> {
            unload(page);
            this.page = (page + 1) % maxPagePairCount;
            load(this.page);
        }, true);
        this.addRenderableWidget(pageForward);
    }

    @Override
    public void renderAnthology(GuiGraphics guiGraphics) {
        guiGraphics.blit(this.BLUEPRINT_ANTHOLOGY_VIEW_TEXTURE, this.leftPos, this.bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private void createMenu() {
        createMenu(6, 9);
    }

    private static void forEach(BlueprintWidget[][][][] widgets, Consumer<BlueprintWidget> widget) {
        for (BlueprintWidget[][][] itemWidgetsPage : widgets) {
            for (BlueprintWidget[][] itemWidgetsLeftRight : itemWidgetsPage) {
                for (BlueprintWidget[] itemWidgets : itemWidgetsLeftRight) {
                    for (BlueprintWidget BlueprintWidget : itemWidgets) {
                        if (BlueprintWidget != null) {
                            widget.accept(BlueprintWidget);
                        }
                    }
                }
            }
        }
    }

    private void createMenu(int rowLength, int columnLength) {
        Set<String> blueprintsWithRecipes = RecipeDataList.INSTANCE.recipeData.stream()
                .map(RecipeData::getBlueprint)
                .collect(Collectors.toSet());

        List<String> blueprints = BlueprintList.INSTANCE.blueprints.stream()
                .filter(blueprintsWithRecipes::contains)
                .collect(Collectors.toList());
        this.maxPagePairCount = (blueprints.size()/ (rowLength * columnLength)) / 2 + 1;
        items = new BlueprintWidget[maxPagePairCount][2][columnLength][rowLength];
        int registryIdx = 0;
        int width = 17;
        int buttonSize = width - 2;
        int offsetFromEdge = 19;
        for (BlueprintWidget[][][] pagePair : items) {
            for (int pageSide = 0; pageSide < pagePair.length; pageSide++) {
                BlueprintWidget[][] page = pagePair[pageSide];
                int yOffset = this.bottomPos + offsetFromEdge;
                for (BlueprintWidget[] row : page) {
                    int xOffset = this.leftPos + offsetFromEdge + 4;
                    for (int columnIdx = 0; columnIdx < row.length; columnIdx++) {
                        int startX = ((IMAGE_WIDTH / 2) - 8) * pageSide;
                        if (registryIdx > blueprints.size() - 1) {
                            break;
                        }
                        String blueprint = blueprints.get(registryIdx);
                        ItemStack stack = new ItemStack(ItemRegistry.BLUEPRINT.get());
                        Blueprint.setBluePrint(stack, ResourceLocation.parse(blueprint));
                        BlueprintWidget itemWidget = new BlueprintWidget(stack, this.minecraft.getItemRenderer(), blueprint, xOffset + startX, yOffset, buttonSize, buttonSize, button -> {
                            this.minecraft.setScreen(new BlueprintInformationScreen(blueprint, this));
                        });
                        row[columnIdx] = this.addRenderableWidget(itemWidget);
                        registryIdx++;
                        xOffset += width;
                    }
                    yOffset += width;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        forEach(items, BlueprintWidget -> {
            if (BlueprintWidget.isMouseOver(mouseX, mouseY)) {
                List<Component> tooltipLines = BlueprintWidget.stack.getTooltipLines(Minecraft.getInstance().player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
                if (BlueprintWidget.hasAdditonalInfo) {
                    tooltipLines.add(1, Component.literal("Click for more info"));
                }
                tooltipLines.add(Component.translatable("gui.help.list"));
                guiGraphics.renderComponentTooltip(this.font, tooltipLines, mouseX, mouseY);
            }
        });
        mark(mouseX, mouseY);
    }

    private void mark(int mouseX, int mouseY) {

    }
    private void unload(int page) {
        BlueprintWidget[][][] renderableItem = this.items[page];
        for (BlueprintWidget[][] itemWidgetsByPage : renderableItem) {
            for (BlueprintWidget[] itemWidgets : itemWidgetsByPage) {
                for (BlueprintWidget BlueprintWidget : itemWidgets) {
                    if (BlueprintWidget != null) {
                        BlueprintWidget.visible = false;
                        BlueprintWidget.active = false;
                    }
                }
            }
        }
    }

    private void load(int page) {
        BlueprintWidget[][][] renderableItem = this.items[page];
        for (BlueprintWidget[][] itemWidgetsByPage : renderableItem) {
            for (BlueprintWidget[] itemWidgets : itemWidgetsByPage) {
                for (BlueprintWidget itemWidget : itemWidgets) {
                    if (itemWidget != null) {
                        itemWidget.visible = true;
                        itemWidget.active = true;
                    }
                }
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
