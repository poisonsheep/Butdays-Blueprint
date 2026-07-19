package io.github.poisonsheep.thearbiter.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.poisonsheep.thearbiter.client.misc.RecipeData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlueprintSerializer implements RecipeSerializer<BlueprintRecipe> {
    @Override
    public BlueprintRecipe fromJson(ResourceLocation id, JsonObject json) {
        List<String> blueprints = new ArrayList<>();
        // support both "blueprint" (single string, backward compat) and "blueprints" (array)
        if (json.has("blueprints")) {
            JsonArray arr = GsonHelper.getAsJsonArray(json, "blueprints");
            for (JsonElement e : arr) {
                blueprints.add(e.getAsString());
            }
        } else {
            blueprints.add(GsonHelper.getAsString(json, "blueprint"));
        }

        Recipe<?> recipe = RecipeManager.fromJson(id, GsonHelper.getAsJsonObject(json, "recipe"));
        // register under each blueprint so anthology search finds it
        for (String bp : blueprints) {
            RecipeData data = new RecipeData(bp, recipe);
            RecipeDataList.INSTANCE.recipeData.add(data);
        }
        return new BlueprintRecipe(id, blueprints, (CraftingRecipe) recipe);
    }

    @Nullable
    @Override
    public BlueprintRecipe fromNetwork(ResourceLocation blueprintId, FriendlyByteBuf buffer) {
        ResourceLocation innerRecipeId = buffer.readResourceLocation();
        ResourceLocation recipeSerializerId = buffer.readResourceLocation();
        RecipeSerializer<?> value = ForgeRegistries.RECIPE_SERIALIZERS.getValue(recipeSerializerId);
        Recipe<?> recipe = value.fromNetwork(innerRecipeId, buffer);

        int count = buffer.readVarInt();
        List<String> blueprints = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String bp = buffer.readUtf();
            blueprints.add(bp);
            RecipeData data = new RecipeData(bp, recipe);
            if (!RecipeDataList.INSTANCE.recipeData.contains(data)) {
                RecipeDataList.INSTANCE.recipeData.add(data);
            }
        }
        return new BlueprintRecipe(blueprintId, blueprints, (CraftingRecipe) recipe);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BlueprintRecipe blueprintRecipe) {
        Recipe<CraftingContainer> recipe1 = blueprintRecipe.getRecipe();
        if(recipe1.getId() == null) {
            throw new IllegalArgumentException("Unable to serialize a recipe without an id: " + recipe1);
        }
        ResourceLocation serializerKey = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe1.getSerializer());
        if(serializerKey == null) {
            throw new IllegalArgumentException("Unable to serialize a recipe serializer without an id: " + recipe1.getSerializer());
        }
        buffer.writeResourceLocation(recipe1.getId());
        buffer.writeResourceLocation(serializerKey);
        recipe1.getSerializer().toNetwork(buffer, cast(recipe1));

        List<String> blueprints = blueprintRecipe.getBlueprints();
        buffer.writeVarInt(blueprints.size());
        for (String bp : blueprints) {
            buffer.writeUtf(bp);
        }
    }

    public static <T> T cast(Object o) {
        return (T) o;
    }
}
