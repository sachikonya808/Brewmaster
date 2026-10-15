package net.sachikonya.brewmaster.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FlaskUpgradeData(int tier) {
    public static final FlaskUpgradeData DEFAULT = new FlaskUpgradeData(0);

    public static final Codec<FlaskUpgradeData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT
                        .fieldOf("tier")
                        .forGetter(FlaskUpgradeData::tier)
    ).apply(instance, FlaskUpgradeData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskUpgradeData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    FlaskUpgradeData::tier,

                    FlaskUpgradeData::new
            );
}
