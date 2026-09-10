package club.rainbowkitty.rkcore.common.client;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Vanilla {@link ChestRenderer} with the texture choice parametrised. The stock renderer resolves
 * its sprite through a hardcoded {@code ChestRenderState.ChestMaterialType} switch that a
 * {@code ChestBlock} subclass cannot extend; this subclass keeps all of vanilla's state extraction
 * (model baking, facing transform, double-chest combiners, lid curve) and only swaps the sprite for
 * one a {@link ChestSpriteSelector} supplies. Resolved against the blocks atlas, not vanilla's
 * dedicated chest atlas ({@code Sheets.CHEST_MAPPER}) — a mod's chest sprites are ordinary block
 * textures, stitched in alongside everything else under {@code textures/block/}.
 *
 * <p>A server-side Polymer mod registers this (via {@code BlockEntityRendererRegistry}) from its
 * client entrypoint so clients that run the mod render the real block; other clients see the mod's
 * fallback (a display entity or a Polymer block substitute).
 */
@Environment(EnvType.CLIENT)
public class PolymerChestRenderer<T extends BlockEntity & LidBlockEntity> extends ChestRenderer<T> {
    private final SpriteGetter sprites;
    private final MultiblockChestResources<ChestModel> models;
    private final ChestSpriteSelector<? super T> spriteSelector;
    private final Map<Identifier, SpriteId> spriteCache = new HashMap<>();

    public PolymerChestRenderer(BlockEntityRendererProvider.Context context,
            ChestSpriteSelector<? super T> spriteSelector) {
        super(context);
        this.sprites = context.sprites();
        this.models = ChestRenderer.LAYERS.map(layer -> new ChestModel(context.bakeLayer(layer)));
        this.spriteSelector = spriteSelector;
    }

    @Override
    public PolymerChestRenderState createRenderState() {
        return new PolymerChestRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, ChestRenderState state, float partialTicks,
            Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(
                blockEntity, state, partialTicks, cameraPosition, breakProgress);
        ((PolymerChestRenderState) state).sprite =
                this.spriteSelector.sprite(blockEntity, state.type);
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(ChestRenderer.modelTransformation(state.facing));
        float open = state.open;
        open = 1.0F - open;
        open = 1.0F - open * open * open;
        SpriteId spriteId = this.spriteCache.computeIfAbsent(
                ((PolymerChestRenderState) state).sprite,
                id -> new SpriteId(TextureAtlas.LOCATION_BLOCKS, id));
        ChestModel model = this.models.select(state.type);
        submitNodeCollector.submitModel(model, open, poseStack, state.lightCoords,
                OverlayTexture.NO_OVERLAY, -1, spriteId, this.sprites, 0, state.breakProgress);
        poseStack.popPose();
    }
}
