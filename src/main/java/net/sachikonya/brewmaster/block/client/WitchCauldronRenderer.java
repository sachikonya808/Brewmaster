package net.sachikonya.brewmaster.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import net.sachikonya.brewmaster.fluid.ModFluids;

public class WitchCauldronRenderer implements BlockEntityRenderer<WitchCauldronBlockEntity> {
    private static final float MIN_X = 0, MAX_X = 1f;
    private static final float MIN_Z = 0f, MAX_Z = 1f;
    private static final float MIN_Y = 1f/16f, MAX_Y = 17f/16f;
    private static final int WATER_COLOR = 0x3F76E4;
    private static final float ITEM_SCALE = 1.0f;

    public WitchCauldronRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(WitchCauldronBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        Level level = blockEntity.getLevel();
        if(level == null) { return; }

        int light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above());

        FluidStack fluidStack = blockEntity.tank.getFluid();
        float fill = (float)fluidStack.getAmount() / WitchCauldronBlockEntity.CAPACITY;
        float y_level = Mth.lerp(fill, MIN_Y, MAX_Y);

        if(!fluidStack.isEmpty()){
            renderFluid(blockEntity, poseStack, multiBufferSource, fluidStack, y_level, light);
        }
        renderIngredient(blockEntity, v, poseStack, multiBufferSource, y_level, light, !fluidStack.isEmpty());
    }

    private void renderFluid(WitchCauldronBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, FluidStack fluidStack, float y_level, int light) {
        int rgb = fluidStack.is(ModFluids.POTION.get()) ? blockEntity.getPotionMixture().getPotionColor() : WATER_COLOR;

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(fluidStack));

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.translucent());
        PoseStack.Pose pose = poseStack.last();

        int argb = 0xCC000000 | rgb;
        float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();

        vertexConsumer.addVertex(pose, MIN_X, y_level, MIN_Z).setColor(argb).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(pose, MIN_X, y_level, MAX_Z).setColor(argb).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(pose, MAX_X, y_level, MAX_Z).setColor(argb).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(pose, MAX_X, y_level, MIN_Z).setColor(argb).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

    private void renderIngredient(WitchCauldronBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, float y_level, int light, boolean isEmpty) {
        ItemStack stack = blockEntity.getIngredient();
        if(stack.isEmpty()) { return; }

        Level level = blockEntity.getLevel();
        float time = level.getGameTime() + v;

        //Flat items = lie in the water surface / Blocks/3D items = upright
        boolean flat = !Minecraft.getInstance().getItemRenderer().getModel(stack, level, null, 0).isGui3d();

        float bobbing = isEmpty ? Mth.sin(time / 10f) * 0.02f : 0f; //up/down
        float tiltX = isEmpty ? Mth.sin(time / 17f) * 3f : 0f; //wobble
        float tiltZ = isEmpty ? Mth.cos(time / 23f) * 3f : 0f;
        float spin = isEmpty ? time * 1.2f : 0f; //rotation
        float height = flat ? 0.1f : 0.08f; //
        //holy hack
        height = isEmpty ? (flat ? 0f : -0.2f) : height;

        poseStack.pushPose();
        poseStack.translate((MIN_X + MAX_X) / 2f, y_level + height + bobbing, (MIN_Z + MAX_Z) / 2f);

        poseStack.mulPose(Axis.XP.rotationDegrees(tiltX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(tiltZ));
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        if(flat) { //true = lay flat | false = stand upright
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        }
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, blockEntity.getLevel(), (int)blockEntity.getBlockPos().asLong()
        );

        poseStack.popPose();
    }
}
