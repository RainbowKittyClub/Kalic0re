package club.rainbowkitty.kalic0re.common.label;

import java.util.UUID;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import org.joml.Vector3f;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;

// One label, reused for a player's whole session and shown only while they look at a target.
final class HoverLabel {
    private static final int BLANK = 0;

    private final OwnedHolder holder;
    private final TextDisplayElement heading;
    private final TextDisplayElement subheading;

    private LabelStyle style;
    private ServerLevel level;
    private Vec3 anchor;
    private boolean shown;

    HoverLabel(UUID ownerId) {
        this.holder = new OwnedHolder(ownerId);
        this.heading = this.holder.addElement(new TextDisplayElement());
        this.subheading = this.holder.addElement(new TextDisplayElement());
    }

    // Points the label at a target, re-anchoring only when it is a different one.
    void show(ServerPlayer viewer, LabelTarget target) {
        applyStyle(target.style());

        if (this.anchor == null || !this.anchor.equals(target.anchor())
                || this.level != target.level()) {
            this.hide();
            this.level = target.level();
            this.anchor = target.anchor();
            ChunkAttachment.of(this.holder, this.level, this.anchor);
        }

        layout(target);

        // Flush before the first spawn packet, otherwise the label appears for a tick as it was
        // last shown.
        this.holder.tick();

        if (!this.shown) {
            this.holder.visible = true;
            this.holder.startWatching(viewer);
            this.shown = true;
        }
    }

    // Removes the label from the world, leaving the holder and elements reusable.
    void hide() {
        if (this.anchor == null) {
            return;
        }

        this.holder.visible = false;
        this.holder.destroy();
        this.anchor = null;
        this.level = null;
        this.shown = false;
    }

    // Placement rides in the display transformation, not the element's position, so it travels in
    // the same packet as the text. A position packet lands immediately and would split the two,
    // leaving the label a tick showing the old text at the new place.
    private void layout(LabelTarget target) {
        LabelText text = target.text();
        Component below = text.subheading();
        Vec3 offset = target.offset();

        this.heading.setText(text.heading());
        this.subheading.setText(below == null ? Component.empty() : below);

        // With nothing to say under the heading the lower plate has to drop its backing square as
        // well as its text, or an empty box hangs there.
        this.subheading.setBackground(
                below == null ? BLANK : Display.TextDisplay.INITIAL_BACKGROUND);

        double rise = below == null ? 0.0
                : LabelMetrics.height(this.style, below) + LabelMetrics.gap(this.style);
        place(this.subheading, target.yaw(), offset, offset.y);
        place(this.heading, target.yaw(), offset, offset.y + rise);
    }

    private static void place(TextDisplayElement plate, float yaw, Vec3 offset, double y) {
        plate.setRotation(0.0F, yaw);
        plate.setTranslation(new Vector3f((float) offset.x, (float) y, (float) offset.z));
    }

    // One label serves every kind of target a player looks at, so the style is whatever the
    // current one asked for rather than something fixed at construction.
    private void applyStyle(LabelStyle style) {
        if (style.equals(this.style)) {
            return;
        }

        this.style = style;
        apply(this.heading, style);
        apply(this.subheading, style);
    }

    private static void apply(TextDisplayElement plate, LabelStyle style) {
        plate.setBillboardMode(style.billboard());
        plate.setTextAlignment(style.alignment());
        plate.setBrightness(style.brightness());
        plate.setShadow(style.shadow());
        plate.setSeeThrough(style.seeThrough());
        plate.setScale(new Vector3f(style.scale(), style.scale(), style.scale()));
        plate.setLineWidth(LabelMetrics.lineWidth(style));
    }

    // Polymer pushes every chunk watcher at a holder on chunk send and on attachment, so simply
    // never calling startWatching is not enough to keep a label private.
    private static final class OwnedHolder extends ElementHolder {
        private final UUID ownerId;
        private boolean visible;

        private OwnedHolder(UUID ownerId) {
            this.ownerId = ownerId;
        }

        @Override
        public boolean startWatching(ServerGamePacketListenerImpl connection) {
            if (!this.visible || !connection.getPlayer().getUUID().equals(this.ownerId)) {
                return false;
            }
            return super.startWatching(connection);
        }
    }
}
