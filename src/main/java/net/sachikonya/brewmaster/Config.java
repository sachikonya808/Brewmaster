package net.sachikonya.brewmaster;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EFFECT_STRINGS = BUILDER
            .comment("Effects to Disable")
            .defineListAllowEmpty("effects", List.of(), () -> "", Config::validateEffectName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateEffectName(final Object obj) {
        return obj instanceof String effectName && BuiltInRegistries.MOB_EFFECT.containsKey(ResourceLocation.parse(effectName));
    }
}
