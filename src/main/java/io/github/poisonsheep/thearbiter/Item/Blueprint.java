package io.github.poisonsheep.thearbiter.Item;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprint;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprintProvider;
import io.github.poisonsheep.thearbiter.event.blueprint.ReadEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Blueprint extends Item {
    
    public static final ResourceLocation UNKNOWN_BLUEPRINT = new ResourceLocation(ButdaysBlueprint.MODID, "blueprint/unknown");
    public static final ResourceLocation ARBITER_SWORD_BLUEPRINT = new ResourceLocation(ButdaysBlueprint.MODID, "blueprint/arbiter_sword");
    public Blueprint() {
        super(new Properties().stacksTo(1));
    }
    public static void setBluePrint(ItemStack itemStack, ResourceLocation name) {
        itemStack.getOrCreateTag().putString("blueprint", name.toString());
    }
    @Nullable
    public static ResourceLocation getBlueprint(ItemStack itemStack) {
        if (itemStack.getTag() != null && itemStack.getTag().contains("blueprint")) {
            String tag = itemStack.getTag().getString("blueprint");
            return new ResourceLocation(tag);
        }
        return UNKNOWN_BLUEPRINT;
    }
    @Override
    public String getDescriptionId(ItemStack stack) {
        ResourceLocation name = getBlueprint(stack);
        if (name != null) {
            return name.toString().replace(":", ".");
        }
        return super.getDescriptionId(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> list, TooltipFlag flag) {
        ResourceLocation name = getBlueprint(stack);
        if (name != null && level != null) {
            list.add(Component.translatable(name.toString().replace(":", ".") + ".description"));
        }
    }

    @Override
    public  InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ResourceLocation name = getBlueprint(stack);
        if(!level.isClientSide){
            PlayerBlueprint playerBlueprint = player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).orElseThrow(() -> new RuntimeException("Player does not have PlayerBlueprint capability"));
            // 获取玩家的能力列表
            List<String> blueprints = playerBlueprint.getBlueprints();
            if(Objects.equals(name, UNKNOWN_BLUEPRINT)) {
                blueprints.clear();
            }
            // 检查玩家的能力列表中是否包含当前物品的蓝图名称
            if (!blueprints.contains(name.toString())) {
                // 如果不包含，那么表示玩家没有阅读过这个物品
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
                MinecraftForge.EVENT_BUS.post(new ReadEvent((ServerPlayer)player,stack));
                player.awardStat(Stats.ITEM_USED.get(this));
                player.sendSystemMessage(Component.translatable(name.toString().replace(":", ".") + ".tooltip"));
            } else {
                // 如果包含，那么表示玩家已经阅读过这个物品，不要再触发阅读事件
                // 给玩家一个提示信息
                player.sendSystemMessage(Component.translatable("message.butdaysblueprint.already_read"));
            }
            //判断玩家是否是创造模式
            if (!player.getAbilities().instabuild) {
                ItemStack paper = new ItemStack(Items.PAPER);
                if (!player.getInventory().add(paper)) {
                    player.drop(paper, false);
                }
                stack.shrink(1);
            }
        }
        if(level.isClientSide){
            playSound(player);
            addParticle();
        }
        player.getCooldowns().addCooldown(this, 10);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public void playSound(Player player){
        player.playSound(SoundEvents.PLAYER_LEVELUP,1.0F,1.0F);
    }

    public void addParticle(){}

}
