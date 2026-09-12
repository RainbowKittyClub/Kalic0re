package club.rainbowkitty.rkcore.common.enchant.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import club.rainbowkitty.rkcore.common.enchant.HiddenEquipmentStyle;
import club.rainbowkitty.rkcore.common.enchant.HideEquipmentModelEnchantments;

/**
 * The held-item half of {@link HiddenRendering} — same styles as {@link HiddenEquipmentRendering},
 * a different vanilla pipeline, and one behaviour that does not carry over.
 *
 * <p>Items do not pick a render type per draw call the way armor does. {@code ItemFeatureRenderer}
 * draws one {@code BakedQuad} list twice — every main submit first, then every foil submit — using
 * each quad's own {@code materialInfo().itemRenderType()}, so the lever here is the quad's
 * material, not a call argument. {@link #hideLayer} rewrites a resolved layer's quads onto a
 * depth-only stand-in, keeping the real sprite so the alpha cutout still shapes what it writes.
 *
 * <p><strong>A glinting item must write depth</strong>, even under a non-occluding style, which is
 * why the relaxed glint that works for armor has no counterpart here. The glint shader samples
 * nothing but the scrolling glint texture; an item's silhouette comes entirely from the depth
 * buffer, where vanilla's {@code CompareOp.EQUAL} matches only the pixels the main pass's alpha
 * cutout let through. Relax that test and the glint floods the model's whole quad instead of
 * tracing the sprite. Armor hides the difference because its models are boxes whose geometry
 * already approximates the shape; a flat item sprite is not. So for items {@code HIDDEN_GLINT} and
 * {@code PHANTOM_GLINT} render identically, and only a style drawing no glint at all can leave the
 * depth buffer untouched — which it does by dropping the geometry outright.
 *
 * <p>Two vanilla guarantees this leans on, both worth not breaking: {@code buildGroup} runs every
 * main submit before any foil submit and draws in insertion order, so the depth is always in the
 * buffer before the glint tests against it; and the stand-in stays on the default
 * {@code MAIN_TARGET}, so {@code useTransparentGlint} picks {@code glint()}, which is on that
 * target too. An {@code ITEM_ENTITY_TARGET} mismatch would test {@code EQUAL} against a different
 * framebuffer's depth and drop the glint entirely.
 */
@Environment(EnvType.CLIENT)
public final class HiddenItemRendering {
    private static final Map<Identifier, RenderType> SHEETS = new ConcurrentHashMap<>();
    private static final Map<BakedQuad.MaterialInfo, BakedQuad.MaterialInfo> MATERIALS =
            new ConcurrentHashMap<>();

    private static @Nullable RenderPipeline depthPipeline;

    private HiddenItemRendering() {
    }

    static void rebuild(String modId) {
        SHEETS.clear();
        MATERIALS.clear();
        // Depth exactly where vanilla would have written it, so nothing else in the frame occludes
        // differently than it would have; only the item's own colour goes missing.
        depthPipeline = HiddenRendering.depthOnlyPipeline(modId, "pipeline/depth_item", "core/item",
                BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2, DepthStencilState.DEFAULT).build();
    }

    /**
     * The style to draw this stack's item model in, or null to leave it alone. Never hides in
     * {@link ItemDisplayContext#GUI} — an inventory icon the player cannot see is not a cosmetic,
     * it is a lost item.
     */
    public static @Nullable HiddenEquipmentStyle styleFor(ItemStack stack,
            ItemDisplayContext displayContext) {
        if (displayContext == ItemDisplayContext.GUI) {
            return null;
        }
        return HideEquipmentModelEnchantments.styleFor(stack);
    }

    /** Rewrites one resolved layer in place; call for each active layer of a render state. */
    public static void hideLayer(ItemStackRenderState.LayerRenderState layer,
            HiddenEquipmentStyle style) {
        if (!style.glinting()) {
            layer.setFoilType(ItemStackRenderState.FoilType.NONE);
        }
        List<BakedQuad> quads = layer.prepareQuadList();
        if (!style.occluding() && !style.glinting()) {
            // No colour, no glint to shape, no depth wanted: submitting geometry that leaves
            // nothing behind is pure waste, so drop it. Only the draw goes — a layer's extents,
            // and so the model bounding box, are a separate field.
            quads.clear();
        } else {
            quads.replaceAll(HiddenItemRendering::hide);
        }
    }

    private static BakedQuad hide(BakedQuad quad) {
        BakedQuad.MaterialInfo material = quad.materialInfo();
        RenderType sheet = sheetFor(material.sprite().atlasLocation());
        BakedQuad.MaterialInfo hidden = MATERIALS.computeIfAbsent(material,
                key -> new BakedQuad.MaterialInfo(key.sprite(), key.layer(), sheet,
                        key.tintIndex(), key.shade(), key.lightEmission()));
        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                quad.direction(), hidden);
    }

    // Keyed on the quad's own atlas rather than on vanilla's sheet constants: the alpha cutout is
    // what gives the depth its item shape, so the texture has to be the real one, and going
    // through the sprite covers modded atlases without naming any of them.
    private static RenderType sheetFor(Identifier atlas) {
        return SHEETS.computeIfAbsent(atlas, key -> HiddenRendering.create("depth_item",
                RenderSetup.builder(HiddenRendering.require(depthPipeline))
                        .withTexture("Sampler0", key)
                        .useLightmap()
                        .useOverlay()
                        .createRenderSetup()));
    }
}
