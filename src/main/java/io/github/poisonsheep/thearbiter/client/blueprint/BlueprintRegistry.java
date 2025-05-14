package io.github.poisonsheep.thearbiter.client.blueprint;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.util.JsonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class BlueprintRegistry {
    public static void register(ModelEvent.RegisterAdditional event) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        for (String namespace : manager.getNamespaces()) {
            try {
                ResourceLocation resourceName = new ResourceLocation(namespace, "blueprint/list.json");
                if (manager.getResource(resourceName).isPresent()) {
                    Optional<Resource> resource = manager.getResource(resourceName);
                    if (resource.isPresent()) {
                        InputStreamReader reader = new InputStreamReader(resource.get().open());
                        BlueprintList list = JsonUtil.INSTANCE.noExpose.fromJson(reader, BlueprintList.class);
                        BlueprintList.INSTANCE.blueprints.addAll(list.blueprints);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        for (var info : BlueprintList.INSTANCE.blueprints) {
            event.register(to(new ResourceLocation(info)));
        }
    }
    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult evt) {
        //CocktailModelRegistry.bakeModel(evt);
        //PlateModelRegistry.bakeModel(evt);
        evt.getModels().put(new ModelResourceLocation(
                ButdaysBlueprint.MODID,
                "blueprint",
                "inventory"
        ), new BlueprintBakedModel());
    }

    public static ResourceLocation to(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath());
    }
}