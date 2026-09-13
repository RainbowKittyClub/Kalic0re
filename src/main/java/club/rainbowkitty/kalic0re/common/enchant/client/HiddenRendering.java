package club.rainbowkitty.kalic0re.common.enchant.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import club.rainbowkitty.kalic0re.common.enchant.HiddenEquipmentStyle;

/**
 * The one call a consumer makes to switch {@link HiddenEquipmentStyle} rendering on, plus the
 * depth-only pipeline shape {@link HiddenEquipmentRendering} and {@link HiddenItemRendering} both
 * build from.
 *
 * <p>{@link #init} has to run from a client entrypoint before anything is drawn, and it needs the
 * consumer's own {@link RenderTypeFactory}: {@code RenderType.create} is package-private to
 * {@code net.minecraft.client.renderer.rendertype} with no public alternative, and this package is
 * relocated into each consuming mod rather than shared at runtime, so a class squatting in that
 * vanilla package cannot live here — two consumers would collide on the one class name. Each mod
 * passes a mod-named shim of its own instead. Skipping the call throws at first render, not at
 * boot, so wire it next to the mod's other client setup.
 */
@Environment(EnvType.CLIENT)
public final class HiddenRendering {
    // Draw the mesh, keep its depth behaviour, write none of its colour.
    static final ColorTargetState DEPTH_ONLY = new ColorTargetState(
            ColorTargetState.DEFAULT.blendFunction(), ColorTargetState.DEFAULT.format(),
            ColorTargetState.WRITE_NONE);

    private static String namespace = "kalic0re";
    private static @Nullable RenderTypeFactory factory;

    private HiddenRendering() {
    }

    /** Call once from a client entrypoint, before any equipment or held item is drawn. */
    public static void init(String modId, RenderTypeFactory renderTypes) {
        namespace = modId;
        factory = renderTypes;
        HiddenEquipmentRendering.rebuild(modId);
        HiddenItemRendering.rebuild(modId);
    }

    /** Builds a render type under a consumer-scoped debug name, e.g. {@code artifacts_glint}. */
    static RenderType create(String name, RenderSetup setup) {
        return require(factory).create(namespace + "_" + name, setup);
    }

    /**
     * Vanilla's {@code ENTITY_SNIPPET}/{@code ITEM_SNIPPET} chain rebuilt from public parts — the
     * snippets themselves are private — with colour writes masked off and the same
     * {@code ALPHA_CUTOUT} both originals declare, so the mesh still writes depth in exactly the
     * texture's own shape. Bind group order matters and must match how the snippets nest: the
     * snippet's own layouts first, appended ones after, hence globals first and samplers after the
     * shader calls. The returned builder is unfinished so a caller can append its own state.
     */
    static RenderPipeline.Builder depthOnlyPipeline(String modId, String path, String shader,
            BindGroupLayout samplers, DepthStencilState depth) {
        return RenderPipeline.builder()
                .withLocation(Identifier.fromNamespaceAndPath(modId, path))
                .withBindGroupLayout(BindGroupLayouts.GLOBALS)
                .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
                .withBindGroupLayout(BindGroupLayouts.FOG)
                .withBindGroupLayout(BindGroupLayouts.LIGHTING)
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withBindGroupLayout(samplers)
                .withVertexBinding(0, DefaultVertexFormat.ENTITY)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .withDepthStencilState(depth)
                .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                .withColorTargetState(DEPTH_ONLY);
    }

    /** Reports a skipped {@link #init} plainly, rather than NPEing deep inside a render call. */
    static <T> T require(@Nullable T value) {
        if (value == null) {
            throw new IllegalStateException(
                    "HiddenRendering.init was never called from the client entrypoint");
        }
        return value;
    }
}
