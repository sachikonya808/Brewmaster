package net.sachikonya.brewmaster.entity.custom;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.sachikonya.brewmaster.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

// Anvil Entity + Behavior by Xylonity, from their Companions Mod: https://github.com/Xylonity/Companions
public class AnvilEntity extends FallingBlockEntity {
    private BlockState blockState = Blocks.ANVIL.defaultBlockState();

    private static final float[] POP_SCALES = { 2f, 5f };
    private float DAMAGE_PER_BLOCK = 2f;

    private int popsDone = 0;

    private double MAX_DAMAGE = 20d;
    private double spawnY = Double.MIN_VALUE;

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public AnvilEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.noPhysics = true;
    }

    public static AnvilEntity create(ServerLevel serverLevel, double x, double y, double z, double spawnY, int amplifier) {

        final AnvilEntity anvil = new AnvilEntity(ModEntities.FALLING_ANVIL.get(), serverLevel);

        anvil.DAMAGE_PER_BLOCK = anvil.DAMAGE_PER_BLOCK * amplifier;
        anvil.MAX_DAMAGE = anvil.MAX_DAMAGE * amplifier;

        anvil.setBlockState(Blocks.ANVIL.defaultBlockState());
        anvil.moveTo(x, y, z, 0f, 0f);
        anvil.setStartPos(anvil.blockPosition());
        anvil.time = 1;
        anvil.spawnY = spawnY;

        return anvil;
    }

    @Override
    public void tick() {
        super.tick();

        if(!level().isClientSide && !isRemoved() && getY() <= this.spawnY) {
            squish();
        }
    }

    public void squish() {
        final int distance = Mth.ceil(getStartPos().getY() - getY() - 1.0F);
        final float damage = Math.min(Mth.floor(Math.max(distance, 0) * DAMAGE_PER_BLOCK), (float)MAX_DAMAGE);

        if(damage > 0) {
            level().getEntities(this, getBoundingBox(), e -> e instanceof Player && e.isAlive())
                    .forEach(entity -> entity.hurt(damageSources().anvil(this),damage));
        }

        level().playSound(null, blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1f, 0.7f);
        if(level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()), getX(), getY() + 0.5, getZ(), 40, 0.65, 0.5, 0.65, 0.15);
        }

        discard();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(this.popsDone == 0 ? 1f : POP_SCALES[this.popsDone - 1]);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putDouble("AnvilTargetY", this.spawnY);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AnvilTargetY")) {
            this.spawnY = pCompound.getDouble("TargetY");
        }
    }
}
