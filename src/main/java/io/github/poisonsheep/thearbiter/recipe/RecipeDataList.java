package io.github.poisonsheep.thearbiter.recipe;

import io.github.poisonsheep.thearbiter.client.misc.RecipeData;

import java.util.ArrayList;
import java.util.List;

public class RecipeDataList {
    public static final RecipeDataList INSTANCE = new RecipeDataList();
    private RecipeDataList() {}
    public List<RecipeData> recipeData = new ArrayList<>();
}
