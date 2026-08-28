package club.rainbowkitty.rkcore.common.label;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/** Labels entities of one kind for {@link HoverScanner}. */
public interface EntityLabels {
    /**
     * Whether this is an entity worth aiming at. The scanner unions this across every provider to
     * narrow its pick, so it must not depend on the entity's current contents.
     */
    boolean handles(Entity entity);

    /** The label for the entity, or null when it has nothing to show right now. */
    LabelTarget target(ServerPlayer viewer, ServerLevel level, Entity entity);
}
