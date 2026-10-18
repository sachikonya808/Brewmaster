package net.sachikonya.brewmaster.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.sachikonya.brewmaster.entity.custom.AnvilEntity;
import org.jetbrains.annotations.NotNull;

// Anvil Entity + Behavior by Xylonity, from their Companions Mod: https://github.com/Xylonity/Companions
public class AnvilRenderer extends EntityRenderer<AnvilEntity> {
    public AnvilRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(@NotNull AnvilEntity p_entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        final BlockState state = p_entity.getBlockState();
        if(state.getRenderShape() != RenderShape.MODEL) {
            return;
        }

        final Level level = p_entity.level();
        if(state == level.getBlockState(p_entity.blockPosition())) {
            return;
        }

        poseStack.pushPose();

        poseStack.scale(1.0f,1.0f,1.0f);

        final BlockPos pos = p_entity.blockPosition();

        poseStack.translate(-0.5, 0.0, -0.5);

        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().tesselateBlock(level, dispatcher.getBlockModel(state), state, pos, poseStack,
                bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(state)), false, RandomSource.create(),
                state.getSeed(p_entity.getStartPos()), OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AnvilEntity anvilEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
