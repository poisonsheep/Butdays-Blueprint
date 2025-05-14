package io.github.poisonsheep.thearbiter.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerBlueprintProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerBlueprint> PLAYER_BLUEPRINT_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerBlueprint>() {});
    private PlayerBlueprint blueprint = null;
    private final LazyOptional<PlayerBlueprint> optional = LazyOptional.of(this::createPlayerBlueprint);

    private PlayerBlueprint createPlayerBlueprint() {
        if(this.blueprint == null){
            this.blueprint = new PlayerBlueprint();
        }
        return this.blueprint;
    }
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_BLUEPRINT_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerBlueprint().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createPlayerBlueprint().loadNBTData(tag);
    }
}
