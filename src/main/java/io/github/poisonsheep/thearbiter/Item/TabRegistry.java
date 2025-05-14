package io.github.poisonsheep.thearbiter.Item;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.client.blueprint.BlueprintList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ButdaysBlueprint.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ButdaysBlueprint.MODID);
    public static final RegistryObject<CreativeModeTab> BLUEPRINT_TAB = TABS.register("blueprint", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ItemRegistry.BLUEPRINT.get()))
            .title(Component.translatable("itemGroup.butdaysblueprint.blueprint_tab"))
            .build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TabRegistry.BLUEPRINT_TAB.get()) {
            for (String blueprint : BlueprintList.INSTANCE.blueprints) {
                ItemStack itemStack = new ItemStack(ItemRegistry.BLUEPRINT.get());
                Blueprint.setBluePrint(itemStack, new ResourceLocation(blueprint));
                event.accept(itemStack);
            }
        }
    }
}
