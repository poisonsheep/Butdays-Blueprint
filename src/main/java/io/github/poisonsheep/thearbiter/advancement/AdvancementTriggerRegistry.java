package io.github.poisonsheep.thearbiter.advancement;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class AdvancementTriggerRegistry {
    public static AdvancementTrigger ENTHRONED = new AdvancementTrigger(ResourceLocation.fromNamespaceAndPath(ButdaysBlueprint.MODID, "enthroned"));
    public static AdvancementTrigger FIRST_READ = new AdvancementTrigger(ResourceLocation.fromNamespaceAndPath(ButdaysBlueprint.MODID, "first_read"));
    public static AdvancementTrigger DREAM_AGAIN = new AdvancementTrigger(ResourceLocation.fromNamespaceAndPath(ButdaysBlueprint.MODID, "dream_again"));

    public static void register() {
        CriteriaTriggers.register(ENTHRONED);
        CriteriaTriggers.register(FIRST_READ);
        CriteriaTriggers.register(DREAM_AGAIN);
    }
}
