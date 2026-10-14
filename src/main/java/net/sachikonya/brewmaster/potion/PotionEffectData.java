package net.sachikonya.brewmaster.potion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;

public record PotionEffectData(Holder<MobEffect> effect, int amplifier, int duration) {
    public static final Codec<PotionEffectData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    MobEffect.CODEC
                            .fieldOf("effect")
                            .forGetter(PotionEffectData::effect),
                    Codec.INT
                            .fieldOf("amplifier")
                            .forGetter(PotionEffectData::amplifier),
                    Codec.INT
                            .fieldOf("duration")
                            .forGetter(PotionEffectData::duration)
            ).apply(instance, PotionEffectData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionEffectData> STREAM_CODEC =
            StreamCodec.composite(
                    MobEffect.STREAM_CODEC,
                    PotionEffectData::effect,

                    ByteBufCodecs.VAR_INT,
                    PotionEffectData::amplifier,

                    ByteBufCodecs.VAR_INT,
                    PotionEffectData::duration,

                    PotionEffectData::new
            );
}
