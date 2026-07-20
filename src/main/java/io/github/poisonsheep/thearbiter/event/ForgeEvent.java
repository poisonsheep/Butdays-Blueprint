package io.github.poisonsheep.thearbiter.event;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.item.ItemRegistry;
import io.github.poisonsheep.thearbiter.advancement.AdvancementTriggerRegistry;
import io.github.poisonsheep.thearbiter.network.packet.BlueprintUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//Forge事件总线是用来处理和游戏运行相关的事件
@Mod.EventBusSubscriber(modid = ButdaysBlueprint.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {

    @SubscribeEvent
    public void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        BlueprintUpdatePacket.synchronize(event);
    }
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        BlueprintUpdatePacket.synchronize(event);
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        BlueprintUpdatePacket.synchronize(event);
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide()) {
            if (hasAnthology(player)) return;

            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            InteractionHand hand = InteractionHand.MAIN_HAND;
            if (!held.is(Items.BOOK)) {
                held = player.getItemInHand(InteractionHand.OFF_HAND);
                hand = InteractionHand.OFF_HAND;
            }
            if (!held.is(Items.BOOK)) return;

            held.shrink(1);
            ItemStack anthology = new ItemStack(ItemRegistry.BLUEPRINT_ANTHOLOGY.get());
            if (!player.getInventory().add(anthology)) {
                player.drop(anthology, false);
            }
            AdvancementTriggerRegistry.DREAM_AGAIN.trigger(player);
        }
    }

    private static boolean hasAnthology(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemRegistry.BLUEPRINT_ANTHOLOGY.get())) return true;
        }
        return player.getOffhandItem().is(ItemRegistry.BLUEPRINT_ANTHOLOGY.get());
    }
}
