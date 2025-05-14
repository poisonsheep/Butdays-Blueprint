package io.github.poisonsheep.thearbiter.Item;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ButdaysBlueprint.MODID);

    public static final RegistryObject<Item> BLUEPRINT = ITEMS.register("blueprint",()->new Blueprint());

}
