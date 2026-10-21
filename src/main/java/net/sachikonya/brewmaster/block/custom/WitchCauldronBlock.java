package net.sachikonya.brewmaster.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.sachikonya.brewmaster.block.entity.ModBlockEntities;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class WitchCauldronBlock extends BaseEntityBlock{
    public static final MapCodec<WitchCauldronBlock> CODEC = simpleCodec(WitchCauldronBlock::new);

    public WitchCauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HAS_OR_IS_BREWING, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WitchCauldronBlockEntity(blockPos, blockState);
    }

    private static final VoxelShape INSIDE = Block.box(2, 2, 2, 14, 16, 14);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), INSIDE, BooleanOp.ONLY_FIRST);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof WitchCauldronBlockEntity witchCauldronBlockEntity) {
                witchCauldronBlockEntity.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {

        if (!(pLevel.getBlockEntity(pPos)
                instanceof WitchCauldronBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pPos, pHitResult.getDirection())) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide()
            );
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    //Default Interaction / Empty Hand
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if(!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof WitchCauldronBlockEntity witchCauldronBlockEntity){
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(witchCauldronBlockEntity, Component.translatable("block.brewmaster.witch_cauldron")), pPos);
            } else {
                throw new IllegalStateException("Out Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()){
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.WITCH_CAULDRON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }


    public static final BooleanProperty HAS_OR_IS_BREWING = BooleanProperty.create("has_or_is_brewing");
    private static final double Y_LEVEL = 0.75;

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_OR_IS_BREWING);
        builder.add(FACING);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(!state.getValue(HAS_OR_IS_BREWING)) { return; }

        brewingParticles(level, pos, random);
    }

    private void brewingParticles(Level level, BlockPos pos, RandomSource random) {

        double x = pos.getX() + 0.25 + random.nextDouble() * 0.5;
        double y = pos.getY() + Y_LEVEL;
        double z = pos.getZ() + 0.25 + random.nextDouble() * 0.5;

        if(level.getBlockEntity(pos) instanceof WitchCauldronBlockEntity blockEntity && blockEntity.hasPotionFluid()) {
            int color = 0xFF000000 | blockEntity.getPotionMixture().getPotionColor();
            level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color), x, y, z, 0, 0, 0);
        } else {
            level.addParticle(ParticleTypes.BUBBLE_POP, x ,y, z, 0, 0.02, 0);
        }

        if(random.nextInt(30) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.6f, 1.0f, false);
        }
    }


}