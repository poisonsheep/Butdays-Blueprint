package io.github.poisonsheep.thearbiter.recipe;

import io.github.poisonsheep.thearbiter.capability.PlayerBlueprint;
import io.github.poisonsheep.thearbiter.capability.PlayerBlueprintProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.List;
import java.util.Optional;

public class BlueprintRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    private final String blueprint;
    private final CraftingRecipe craftingRecipe;
    private int width;
    private int height;

    public BlueprintRecipe(ResourceLocation id, String blueprint, CraftingRecipe craftingRecipe){
        this.id = id;
        this.blueprint = blueprint;
        this.craftingRecipe = craftingRecipe;
        if(craftingRecipe instanceof ShapedRecipe) {
            this.width = ((IShapedRecipe<CraftingContainer>) craftingRecipe).getRecipeWidth();
            this.height = ((IShapedRecipe<CraftingContainer>) craftingRecipe).getRecipeHeight();
        }
    }

    @Override
    public boolean matches(CraftingContainer container, Level worldIn) {
        if(worldIn.isClientSide()) {
            return false;
        }
        for(Player p :worldIn.players()) {
            if(isGoodForCrafting(container)) {
                if(isAbleToCraft(p)) {
                    return craftingRecipe.matches(container, worldIn);
                }
            }
        }
        return false;
    }


    @Override
    public ItemStack assemble(CraftingContainer container,  RegistryAccess registryAccess) {
       return craftingRecipe.assemble(container,registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return craftingRecipe.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return craftingRecipe.getResultItem(registryAccess);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return craftingRecipe.getIngredients();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.BLUEPRINT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return craftingRecipe.getType();
    }

    @Override
    public CraftingBookCategory category() {
        return craftingRecipe.category();
    }

    public String getBlueprint(){
        return blueprint;
    }

    public CraftingRecipe getRecipe() {
        return craftingRecipe;
    }
    public boolean isGoodForCrafting(CraftingContainer container) {
        final Optional<AbstractContainerMenu> menu = Optional.ofNullable(container instanceof TransientCraftingContainer transientInv ? transientInv.menu : null);
        if(menu.isEmpty() && ForgeHooks.getCraftingPlayer() == null) {
            return false;
        }
        return true;
    }
    public boolean isAbleToCraft(Player player){
//        Player player = ForgeEvent.player;
        if(player != null) {
            PlayerBlueprint playerBlueprint = player.getCapability(PlayerBlueprintProvider.PLAYER_BLUEPRINT_CAPABILITY).orElseThrow(() -> new RuntimeException("Player does not have PlayerBlueprint capability"));
            List<String> blueprints = playerBlueprint.getBlueprints();
            if(blueprints.contains(blueprint)) {
                return true;
            }
        }
        return false;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
