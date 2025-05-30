package io.github.poisonsheep.thearbiter.client.blueprint;

import io.github.poisonsheep.thearbiter.Item.Blueprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static io.github.poisonsheep.thearbiter.client.blueprint.BlueprintRegistry.to;


/**
 * @author DustW
 **/
public class BlueprintBakedModel implements BakedModel {
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_119123_, @Nullable Direction p_119124_, RandomSource p_119125_) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @NotNull
            @Override
            public BakedModel resolve(@NotNull BakedModel p_173465_, @NotNull ItemStack p_173466_, @Nullable ClientLevel p_173467_, @Nullable LivingEntity p_173468_, int p_173469_) {
                ResourceLocation cocktail = Blueprint.getBlueprint(p_173466_);
                BakedModel model;
                if (cocktail == null) {
                    model = Minecraft.getInstance().getModelManager().getMissingModel();
                } else {
                    model = Minecraft.getInstance().getModelManager().getModel(to(cocktail));
                }

                return model;
            }
        };
    }
}
