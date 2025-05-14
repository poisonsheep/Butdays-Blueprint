package io.github.poisonsheep.thearbiter.recipe;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerializerRegistry {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ButdaysBlueprint.MODID);

    //这里决定配方的id
    public static final RegistryObject<RecipeSerializer<?>> BLUEPRINT_SERIALIZER = SERIALIZER.register("blueprint", () -> new BlueprintSerializer());

    public static void register(IEventBus bus) {
        SERIALIZER.register(bus);
    }
}
