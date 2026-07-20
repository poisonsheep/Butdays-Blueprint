package io.github.poisonsheep.thearbiter;

import com.mojang.logging.LogUtils;
import io.github.poisonsheep.thearbiter.item.ItemRegistry;
import io.github.poisonsheep.thearbiter.item.TabRegistry;
import io.github.poisonsheep.thearbiter.advancement.AdvancementTriggerRegistry;
import io.github.poisonsheep.thearbiter.event.ForgeEvent;
import io.github.poisonsheep.thearbiter.event.blueprint.LearnEvent;
import io.github.poisonsheep.thearbiter.loot.ModGlobalLootModifiers;
import io.github.poisonsheep.thearbiter.network.ModNetwork;
import io.github.poisonsheep.thearbiter.recipe.RecipeRegistry;
import io.github.poisonsheep.thearbiter.recipe.RecipeSerializerRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ButdaysBlueprint.MODID)
public class ButdaysBlueprint
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public final static String MODID = "butdaysblueprint";
    public static final String VERSION = "1.20.1-1.0.0";
    /** Network protocol version — only change when packet structure changes */
    public static final String NETWORK_VERSION = "1";

    @SuppressWarnings("removal")
    public ButdaysBlueprint()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ForgeEvent());
        MinecraftForge.EVENT_BUS.register(new LearnEvent());
        modEventBus.addListener(this::setup);
        ItemRegistry.ITEMS.register(modEventBus);
        RecipeRegistry.register(modEventBus);
        RecipeSerializerRegistry.register(modEventBus);
        TabRegistry.TABS.register(modEventBus);
        ModGlobalLootModifiers.GLM.register(modEventBus);
        ModNetwork.register();
    }

    private void setup(final FMLCommonSetupEvent event) {
        AdvancementTriggerRegistry.register();
    }
}
