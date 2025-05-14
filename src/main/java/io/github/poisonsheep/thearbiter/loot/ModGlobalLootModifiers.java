package io.github.poisonsheep.thearbiter.loot;

import com.mojang.serialization.Codec;
import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ButdaysBlueprint.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> BLUEPRINT_MODIFIER = GLM.register("blueprint_modifier", BlueprintModifier.CODEC);
}