package io.github.poisonsheep.thearbiter.event;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.client.blueprint.BlueprintBakedModel;
import io.github.poisonsheep.thearbiter.client.blueprint.BlueprintRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ButdaysBlueprint.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEvent {
    @SubscribeEvent
    public static void registerModelUnBake(ModelEvent.RegisterAdditional event) {
        BlueprintRegistry.register(event);
    }
    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        event.getModels().put(new ModelResourceLocation(
                ButdaysBlueprint.MODID,
                "blueprint",
                "inventory"
        ), new BlueprintBakedModel());
    }
}
