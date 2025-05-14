package io.github.poisonsheep.thearbiter;

import io.github.poisonsheep.thearbiter.Item.ItemRegistry;
import io.github.poisonsheep.thearbiter.Item.TabRegistry;
import io.github.poisonsheep.thearbiter.advancement.    AdvancementTriggerRegistry;
import io.github.poisonsheep.thearbiter.event.ForgeEvent;
import io.github.poisonsheep.thearbiter.event.blueprint.LearnEvent;
import io.github.poisonsheep.thearbiter.loot.ModGlobalLootModifiers;
import io.github.poisonsheep.thearbiter.network.ModNetwork;
import io.github.poisonsheep.thearbiter.recipe.RecipeRegistry;
import io.github.poisonsheep.thearbiter.recipe.RecipeSerializerRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ButdaysBlueprint.MODID)
public class ButdaysBlueprint
{
    public static final IEventBus modBusEvent = FMLJavaModLoadingContext.get().getModEventBus();
    public final static String MODID = "butdaysblueprint";
    public static final String VERSION = "1.20.1-1.0.0";
    public ButdaysBlueprint()
    {
        MinecraftForge.EVENT_BUS.register(new ForgeEvent());
        MinecraftForge.EVENT_BUS.register(new LearnEvent());
        modBusEvent.addListener(this::setup);
        modBusEvent.addListener(this::enqueueIMC);
        modBusEvent.addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.register(this);
        ItemRegistry.ITEMS.register(modBusEvent);
        RecipeRegistry.register(modBusEvent);
        RecipeSerializerRegistry.register(modBusEvent);
        TabRegistry.TABS.register(modBusEvent);
        ModGlobalLootModifiers.GLM.register(modBusEvent);
        ModNetwork.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        AdvancementTriggerRegistry.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}
