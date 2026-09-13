package club.rainbowkitty.kalic0re.common.enchant.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * A consumer's way in to {@code RenderType.create}, which is package-private to
 * {@code net.minecraft.client.renderer.rendertype}.
 *
 * <p>This indirection exists because the shared code here is relocated into each consumer jar, so
 * a class squatting in that vanilla package cannot live in kalic0re — two consumers would ship
 * the same class name. Each consumer keeps its own mod-named shim and passes it to the
 * {@code init} of whichever of {@link HiddenEquipmentRendering} / {@link HiddenItemRendering}
 * it uses.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface RenderTypeFactory {
    /** Builds a render type under the given debug name. */
    RenderType create(String name, RenderSetup setup);
}
