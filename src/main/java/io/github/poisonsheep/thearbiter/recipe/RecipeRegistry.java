package io.github.poisonsheep.thearbiter.recipe;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ButdaysBlueprint.MODID);
    public static final RegistryObject<RecipeType<BlueprintRecipe>> BLUEPRINT_RECIPE = register("blueprint");
    private static <TYPE extends BlueprintRecipe> RegistryObject<RecipeType<TYPE>> register(String name) {
        return TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return new ResourceLocation(ButdaysBlueprint.MODID, name).toString();
            }
        });
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
