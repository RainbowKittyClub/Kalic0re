package club.rainbowkitty.kalic0re.common.enchant.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import club.rainbowkitty.kalic0re.common.enchant.HiddenEquipmentStyle;
import club.rainbowkitty.kalic0re.common.enchant.HideEquipmentModelEnchantments;

/**
 * The worn-equipment half of {@link HiddenRendering}: what a consumer's
 * {@code EquipmentLayerRenderer} mixin should draw each layer with, per
 * {@link HiddenEquipmentStyle}.
 *
 * <p>Hiding equipment is not just "drop the draw call". Vanilla draws an armor layer as up to three
 * separate {@code submitModel} calls — plain texture, glint overlay, then trim — and its glint
 * pipeline depth-tests {@code CompareOp.EQUAL}, which matches only depth the plain pass itself just
 * wrote (both carry the same {@code VIEW_OFFSET_Z_LAYERING} nudge, so they agree exactly). Skip the
 * plain pass and the glint silently dies with it. Hence two routes:
 *
 * <ul>
 *   <li>An occluding style keeps the plain pass but swaps its pipeline for one whose colour write
 *       mask is {@link ColorTargetState#WRITE_NONE} — depth, no colour. Vanilla's glint then works
 *       untouched, and that same depth hides the wearer's body: armor is modelled fractionally
 *       outside the skin, so the invisible mesh cuts a piece-shaped hole clean through them.
 *   <li>A non-occluding style drops the plain pass entirely and instead relaxes the glint to
 *       {@code CompareOp.GREATER_THAN_OR_EQUAL} writing no depth of its own, so it draws wherever
 *       the mesh sits in front of what is already in the buffer — the body, drawn earlier and
 *       never touched — leaving the wearer intact.
 * </ul>
 *
 * <p>Greater-than, not less-than: {@code DepthStencilState.DEFAULT} is
 * {@code (GREATER_THAN_OR_EQUAL, true)}, so 26.2 runs a reversed-Z buffer where nearer geometry
 * holds the <em>larger</em> depth value. Testing less-than-or-equal here would pass on the mesh's
 * back faces instead, which looks close enough to be mistaken for working but drops the glint
 * anywhere the piece extends past the wearer's silhouette.
 *
 * <p>The relaxed glint works here only because an armor model is a box whose geometry already
 * approximates its own silhouette. It does not carry over to flat item sprites — see
 * {@link HiddenItemRendering}.
 */
@Environment(EnvType.CLIENT)
public final class HiddenEquipmentRendering {
    private static final Map<Identifier, RenderType> OCCLUDING_ARMOR = new ConcurrentHashMap<>();
    private static final Set<RenderType> OCCLUDING_TYPES = ConcurrentHashMap.newKeySet();

    private static @Nullable RenderPipeline occludingPipeline;
    private static @Nullable RenderType lenientGlint;

    private HiddenEquipmentRendering() {
    }

    static void rebuild(String modId) {
        OCCLUDING_ARMOR.clear();
        OCCLUDING_TYPES.clear();

        occludingPipeline = HiddenRendering.depthOnlyPipeline(modId, "pipeline/occluding_armor",
                        "core/entity", BindGroupLayouts.SAMPLER0_SAMPLER2,
                        DepthStencilState.DEFAULT)
                .withShaderDefine("NO_OVERLAY")
                .withShaderDefine("PER_FACE_LIGHTING")
                .withCull(false)
                .build();

        // Not a depth-only pipeline: this one is the glint, so it draws colour and writes no depth.
        RenderPipeline glintPipeline = RenderPipeline.builder()
                .withLocation(Identifier.fromNamespaceAndPath(modId, "pipeline/lenient_glint"))
                .withBindGroupLayout(BindGroupLayouts.GLOBALS)
                .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
                .withBindGroupLayout(BindGroupLayouts.FOG)
                .withVertexShader("core/glint")
                .withFragmentShader("core/glint")
                .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                .withCull(false)
                .withColorTargetState(RenderPipelines.GLINT.getColorTargetState())
                .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .withDepthStencilState(
                        new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                .build();

        lenientGlint = HiddenRendering.create("lenient_armor_glint",
                RenderSetup.builder(glintPipeline)
                        .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
                        .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .createRenderSetup());
    }

    /**
     * What the plain armor layer should draw with — the redirect target for
     * {@code RenderTypes#armorCutoutNoCull}. Occluding styles get the depth-only twin of
     * {@code vanilla}; every other case gets {@code vanilla} straight back.
     */
    public static RenderType armorLayerFor(ItemStack stack, Identifier texture,
            RenderType vanilla) {
        HiddenEquipmentStyle style = HideEquipmentModelEnchantments.styleFor(stack);
        return style != null && style.occluding() ? occludingArmor(texture) : vanilla;
    }

    /**
     * What a {@code submitModel} call inside {@code renderLayers} should draw with, or null to drop
     * the call. Anything that is neither the glint nor an occluding plain layer gets dropped, which
     * is also how the armor trim disappears.
     */
    public static @Nullable RenderType submitTypeFor(ItemStack stack, RenderType incoming) {
        HiddenEquipmentStyle style = HideEquipmentModelEnchantments.styleFor(stack);
        if (style == null) {
            return incoming;
        }
        if (incoming == RenderTypes.armorEntityGlint()) {
            if (!style.glinting()) {
                return null;
            }
            // An occluding style wrote real depth, so vanilla's EQUAL-testing glint still matches.
            return style.occluding() ? incoming : HiddenRendering.require(lenientGlint);
        }
        return OCCLUDING_TYPES.contains(incoming) ? incoming : null;
    }

    // Keeps the texture: its alpha cutout is what confines the mesh to the piece's own shape, so a
    // textureless version would punch out the whole humanoid instead of one armor piece.
    private static RenderType occludingArmor(Identifier texture) {
        return OCCLUDING_ARMOR.computeIfAbsent(texture, key -> {
            RenderType type = HiddenRendering.create("occluding_armor",
                    RenderSetup.builder(HiddenRendering.require(occludingPipeline))
                            .withTexture("Sampler0", key)
                            .useLightmap()
                            .useOverlay()
                            .createRenderSetup());
            OCCLUDING_TYPES.add(type);
            return type;
        });
    }
}
