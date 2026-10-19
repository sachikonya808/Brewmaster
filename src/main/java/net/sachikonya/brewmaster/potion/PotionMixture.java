package net.sachikonya.brewmaster.potion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

import java.util.ArrayList;
import java.util.List;

public record PotionMixture(List<PotionEffectData> effectDataList, double durationMultiplier, int ingredientsAdded) {
    public static final PotionMixture EMPTY =
            new PotionMixture(List.of(),1.0, 0);

    public static final Codec<PotionMixture> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    PotionEffectData.CODEC
                            .listOf()
                            .fieldOf("effects")
                            .forGetter(PotionMixture::effectDataList),
                    Codec.DOUBLE
                            .fieldOf("duration_multiplier")
                            .forGetter(PotionMixture::durationMultiplier),
                    Codec.INT
                            .fieldOf("ingredients_added")
                            .forGetter(PotionMixture::ingredientsAdded)

            ).apply(instance,PotionMixture::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionMixture> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public PotionMixture{
        effectDataList = List.copyOf(effectDataList);
    }

    public PotionMixture addEffect(PotionEffectData effectAdded) {
        List<PotionEffectData> updatedEffects = new ArrayList<>(effectDataList);

        //Checks if effect exists, if so, add +1 to the amplifier
        for (int i = 0; i < updatedEffects.size(); i++){
            PotionEffectData existing = updatedEffects.get(i);
            if (existing.effect().equals(effectAdded.effect())) {
                if(effectAdded.amplifier() == 0) {
                    updatedEffects.set(
                            i,
                            new PotionEffectData(
                                    existing.effect(),
                                    existing.amplifier() + 1,
                                    existing.duration()
                            ));
                } else {
                    updatedEffects.set(
                            i,
                            new PotionEffectData(
                                    existing.effect(),
                                    existing.amplifier() + effectAdded.amplifier(),
                                    existing.duration()
                            ));
                }

                return new PotionMixture(updatedEffects,durationMultiplier, ingredientsAdded);
            }
        }

        //If not, add the effect to the array
        updatedEffects.add(effectAdded);
        return new PotionMixture(updatedEffects, durationMultiplier, ingredientsAdded);
    }

    public PotionMixture addDurationMultiplier(double amount) {
        return new PotionMixture(effectDataList, (durationMultiplier + amount), ingredientsAdded);
    }

    public PotionMixture addUsedIngredientCount(int amount) {
        return new PotionMixture(effectDataList, durationMultiplier, (ingredientsAdded + amount));
    }

    public int getFinalDuration(PotionEffectData effect) {
        return (int)Math.round(effect.duration() * durationMultiplier);
    }

    public boolean isPotionEmpty() {
        return effectDataList.isEmpty();
    }

    public List<Component> getTooltipLines() {
        List<Component> lines = new ArrayList<>();

        if(effectDataList.isEmpty()) {
            lines.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
            return lines;
        }

        for(PotionEffectData data : effectDataList) {
            MutableComponent line = Component.translatable(data.effect().value().getDescriptionId());

            int level = data.amplifier() + 1;
            if(level > 1) {
                Component roman = level <= 10 ? Component.translatable("enchantment.level." + level) : Component.literal(String.valueOf(level));
                line.append(" ").append(roman);
            }

            MobEffect mobEffect = data.effect().value();
            MobEffectInstance temp = new MobEffectInstance(data.effect(), getFinalDuration(data), data.amplifier());
            if(!mobEffect.isInstantenous()) {
                line.append(" (").append(MobEffectUtil.formatDuration(temp, 1.0f, 20f)).append(")");
            }

            lines.add(line.withStyle(data.effect().value().getCategory().getTooltipFormatting()));
        }
        return lines;
    }

    public static final int DEFAULT_COLOR = 0x3F76E4;

    public int getPotionColor() {
        if (effectDataList.isEmpty()) return DEFAULT_COLOR;

        long r = 0, g = 0, b = 0, total = 0;

        for (PotionEffectData data : effectDataList) {
            int c = data.effect().value().getColor();
            int weight = data.amplifier() + 1;
            r += weight * ((c >> 16) & 0xFF);
            g += weight * ((c >> 8) & 0xFF);
            b += weight * (c & 0xFF);
            total += weight;
        }
        return ((int)(r/total << 16) | ((int)(g/total << 8)) | ((int)(b/total)));
    }
}
