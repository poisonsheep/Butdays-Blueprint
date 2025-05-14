package io.github.poisonsheep.thearbiter.client.misc;

import net.minecraft.world.item.crafting.Recipe;

public class RecipeData {
    private String blueprint;
    private Recipe<?> recipe;

    public RecipeData(String blueprint, Recipe<?> recipe) {
        this.blueprint = blueprint;
        this.recipe = recipe;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public Recipe<?> getRecipe() {
        return recipe;
    }
}
