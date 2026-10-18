package net.sachikonya.brewmaster.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SeaBlessedGourdState(int distillTime) {
    public static final SeaBlessedGourdState EMPTY = new SeaBlessedGourdState(0);

    public static final Codec<SeaBlessedGourdState> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT
                            .fieldOf("distill_time")
                            .forGetter(SeaBlessedGourdState::distillTime)
            ).apply(instance, SeaBlessedGourdState::new));

    public static final StreamCodec<ByteBuf, SeaBlessedGourdState> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SeaBlessedGourdState::distillTime,

                    SeaBlessedGourdState::new
            );
}