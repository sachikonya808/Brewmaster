package net.sachikonya.brewmaster.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.sachikonya.brewmaster.Brewmaster;
import org.jetbrains.annotations.Nullable;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> HEART_ATTACK =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "heart_attack"));

    public static DamageSource of(Level level, ResourceKey<DamageType> resourceKey, @Nullable Entity attacker) {
        Holder<DamageType> typeHolder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(resourceKey);
        return new DamageSource(typeHolder, attacker);
    }
}
