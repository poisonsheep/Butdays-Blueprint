package io.github.poisonsheep.thearbiter.compat.jei;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import io.github.poisonsheep.thearbiter.Item.Blueprint;
import io.github.poisonsheep.thearbiter.Item.ItemRegistry;
import io.github.poisonsheep.thearbiter.client.blueprint.BlueprintList;
import io.github.poisonsheep.thearbiter.recipe.BlueprintRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    public static IJeiRuntime runTime;
    public static final ResourceLocation UID = new ResourceLocation(ButdaysBlueprint.MODID, "jei_plugin");
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runTime = jeiRuntime;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ItemRegistry.BLUEPRINT.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (String blueprint : BlueprintList.INSTANCE.blueprints) {
            ItemStack stack = new ItemStack(ItemRegistry.BLUEPRINT.get());
            ResourceLocation name = new ResourceLocation(blueprint);
            Blueprint.setBluePrint(stack, name);
            registration.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, Component.translatable(blueprint.replace(":", ".") + ".message"));
        }
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(BlueprintRecipe.class, BlueprintRecipeExtension::new);
    }
}
