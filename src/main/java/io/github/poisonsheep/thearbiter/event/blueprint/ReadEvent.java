package io.github.poisonsheep.thearbiter.event.blueprint;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ReadEvent extends Event {
    private ServerPlayer player;
    private ItemStack stack;
    public ReadEvent(ServerPlayer player, ItemStack stack){
        this.player = player;
        this.stack = stack;
    }
    public ServerPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
