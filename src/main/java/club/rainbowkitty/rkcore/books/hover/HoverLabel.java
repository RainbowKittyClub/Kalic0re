package club.rainbowkitty.rkcore.books.hover;

import java.util.UUID;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import org.joml.Vector3f;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;

// One text label, reused for a player's whole session and shown only while they look at a slot.
final class HoverLabel {
    private final OwnedHolder holder;
    private final TextDisplayElement element;

    private ServerLevel level;
    private Vec3 anchor;
    private boolean shown;

    HoverLabel(UUID ownerId) {
        this.holder = new OwnedHolder(ownerId);
        this.element = this.holder.addElement(new TextDisplayElement());
        // FIXED rather than a billboard: a billboarded label pivots about its own centre, so at
        // oblique angles its ends swing through the shelf. Pinned to the face it never can.
        this.element.setBillboardMode(Display.BillboardConstraints.FIXED);
        this.element.setTextAlignment(Display.TextDisplay.Align.CENTER);
        this.element.setBrightness(Brightness.FULL_BRIGHT);
        this.element.setShadow(true);
        this.element.setScale(new Vector3f(LabelPlacement.SCALE, LabelPlacement.SCALE,
                LabelPlacement.SCALE));
        this.element.setLineWidth(LabelPlacement.LINE_WIDTH);
    }

    // Points the label at a book, re-anchoring only when it is a different one.
    void show(ServerPlayer viewer, HoverTitles.Target target) {
        if (this.anchor == null || !this.anchor.equals(target.anchor())
                || this.level != target.level()) {
            this.hide();
            this.level = target.level();
            this.anchor = target.anchor();
            ChunkAttachment.of(this.holder, this.level, this.anchor);
        }

        this.element.setText(target.title());
        this.element.setOffset(target.offset());

        // Matches the orientation a billboard would pick for a viewer stood square to the face:
        // FIXED renders at -yaw, and a camera in front sits at facing.getOpposite().toYRot().
        this.element.setRotation(0.0F, target.yaw());

        // Flush text and position before the first spawn packet, otherwise the label appears
        // for a tick at wherever it was last shown.
        this.holder.tick();

        if (!this.shown) {
            this.holder.visible = true;
            this.holder.startWatching(viewer);
            this.shown = true;
        }
    }

    // Removes the label from the world, leaving the holder and element reusable.
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
