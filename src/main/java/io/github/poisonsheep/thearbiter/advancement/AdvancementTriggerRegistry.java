package io.github.poisonsheep.thearbiter.advancement;

import io.github.poisonsheep.thearbiter.ButdaysBlueprint;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class AdvancementTriggerRegistry {
    public static AdvancementTrigger ENTHRONED = new AdvancementTrigger(new ResourceLocation(ButdaysBlueprint.MODID, "enthroned"));
    public static AdvancementTrigger FIRST_READ = new AdvancementTrigger(new ResourceLocation(ButdaysBlueprint.MODID, "first_read"));

    public static void register() {
        CriteriaTriggers.register(ENTHRONED);
        CriteriaTriggers.register(FIRST_READ);
    }
}
