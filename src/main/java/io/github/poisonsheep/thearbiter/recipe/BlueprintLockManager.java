package io.github.poisonsheep.thearbiter.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import io.github.poisonsheep.thearbiter.client.misc.RecipeData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod.EventBusSubscriber(modid = "butdaysblueprint")
public class BlueprintLockManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final ResourceLocation CONFIG_ID = ResourceLocation.fromNamespaceAndPath("butdaysblueprint", "blueprint_locks.json");

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        RecipeManager recipeManager = server.getRecipeManager();

        LockConfig config = loadLockConfig(server);
        if (config == null) return;

        boolean hasSingle = config.locks != null && !config.locks.isEmpty();
        boolean hasMulti = config.multi_locks != null && !config.multi_locks.isEmpty();
        if (!hasSingle && !hasMulti) return;

        Map<ResourceLocation, Recipe<?>> byName = getByNameMap(recipeManager);
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = getRecipesMap(recipeManager);
        if (byName == null || recipes == null) {
            LOGGER.error("Failed to access RecipeManager maps, blueprint locks not applied");
            return;
        }

        Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>(byName);
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes = new HashMap<>();
        for (Map.Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : recipes.entrySet()) {
            newRecipes.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }

        // build recipe → blueprints mapping
        Map<ResourceLocation, List<String>> recipeBlueprints = new LinkedHashMap<>();

        // single locks: blueprint → [recipe_ids]
        if (hasSingle) {
            for (Map.Entry<String, List<String>> entry : config.locks.entrySet()) {
                String bp = entry.getKey();
                for (String recipeId : entry.getValue()) {
                    ResourceLocation id = ResourceLocation.tryParse(recipeId);
                    if (id != null) {
                        recipeBlueprints.computeIfAbsent(id, k -> new ArrayList<>()).add(bp);
                    }
                }
            }
        }

        // multi locks: [{blueprints: [...], recipes: [...]}]
        if (hasMulti) {
            for (MultiLockEntry mle : config.multi_locks) {
                List<String> bps = mle.blueprints;
                for (String recipeId : mle.recipes) {
                    ResourceLocation id = ResourceLocation.tryParse(recipeId);
                    if (id != null) {
                        recipeBlueprints.computeIfAbsent(id, k -> new ArrayList<>()).addAll(bps);
                    }
                }
            }
        }

        // deduplicate and wrap
        int wrapped = 0;
        for (Map.Entry<ResourceLocation, List<String>> entry : recipeBlueprints.entrySet()) {
            ResourceLocation id = entry.getKey();
            List<String> blueprints = new ArrayList<>(new LinkedHashSet<>(entry.getValue())); // dedupe, preserve order

            Recipe<?> vanilla = newByName.get(id);
            if (vanilla == null) {
                LOGGER.warn("Recipe not found: {}", id);
                continue;
            }
            if (!(vanilla instanceof CraftingRecipe craftingRecipe)) {
                LOGGER.warn("{} is not a CraftingRecipe", id);
                continue;
            }

            BlueprintRecipe wrappedRecipe = new BlueprintRecipe(id, blueprints, craftingRecipe);
            newByName.put(id, wrappedRecipe);
            newRecipes.get(wrappedRecipe.getType()).put(id, wrappedRecipe);
            for (String bp : blueprints) {
                RecipeDataList.INSTANCE.recipeData.add(new RecipeData(bp, craftingRecipe));
            }
            wrapped++;
            LOGGER.debug("Locked {} -> {}", id, blueprints);
        }

        setFieldByType(recipeManager, newByName);
        setFieldByType(recipeManager, newRecipes);
        LOGGER.info("Blueprint locks applied: {} recipe(s) wrapped", wrapped);
    }

    @SuppressWarnings("unchecked")
    private static Map<ResourceLocation, Recipe<?>> getByNameMap(RecipeManager manager) {
        for (Field field : RecipeManager.class.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Object val = field.get(manager);
                    if (val instanceof Map<?,?> map && !map.isEmpty()) {
                        Object firstKey = map.keySet().iterator().next();
                        if (firstKey instanceof ResourceLocation) {
                            return (Map<ResourceLocation, Recipe<?>>) val;
                        }
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        LOGGER.error("Could not find byName map in RecipeManager");
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> getRecipesMap(RecipeManager manager) {
        for (Field field : RecipeManager.class.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Object val = field.get(manager);
                    if (val instanceof Map<?,?> outer && !outer.isEmpty()) {
                        Object firstKey = outer.keySet().iterator().next();
                        Object firstVal = outer.get(firstKey);
                        if (firstKey instanceof RecipeType && firstVal instanceof Map) {
                            return (Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>>) val;
                        }
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        LOGGER.error("Could not find recipes map in RecipeManager");
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void setFieldByType(RecipeManager manager, Map<?, ?> newMap) {
        Object sampleKey = newMap.keySet().iterator().next();
        for (Field field : RecipeManager.class.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Map<?, ?> current = (Map<?, ?>) field.get(manager);
                    if (current != null && !current.isEmpty()) {
                        Object currentKey = current.keySet().iterator().next();
                        if (currentKey.getClass() == sampleKey.getClass()) {
                            field.set(manager, newMap);
                            return;
                        }
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
        LOGGER.error("Could not set field in RecipeManager by type matching");
    }

    private static LockConfig loadLockConfig(MinecraftServer server) {
        try {
            Optional<Resource> resource = server.getResourceManager().getResource(CONFIG_ID);
            if (resource.isEmpty()) {
                LOGGER.debug("No blueprint_locks.json found, skipping");
                return null;
            }
            try (InputStreamReader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, LockConfig.class);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load blueprint_locks.json: {}", e.getMessage());
            return null;
        }
    }

    private static class LockConfig {
        Map<String, List<String>> locks;
        List<MultiLockEntry> multi_locks;
    }

    private static class MultiLockEntry {
        List<String> blueprints;
        List<String> recipes;
    }
}
