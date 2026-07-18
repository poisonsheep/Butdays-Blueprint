package io.github.poisonsheep.thearbiter.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class BlueprintContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    public BlueprintContainerObjectSelectionList(int width, int height, int listTop, int listBottom, int entrySize) {
        super(Minecraft.getInstance(), width, height, listTop, listBottom, entrySize);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 5;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getRowTop(int index) {
        int rowTop = super.getRowTop(index);
        if (rowTop < this.y0) {
            return Integer.MAX_VALUE;
        }
        return rowTop;
    }
}
