package club.rainbowkitty.rkcore.common.label;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * What a label says. The two parts get a plate each, so a name reads as a name rather than as the
 * first line of whatever the thing has to say about itself.
 *
 * @param heading     what the thing is called
 * @param subheadings what it says about itself, one line each
 */
public record LabelText(Component heading, List<Component> subheadings) {

    /** The subheadings as the one component a text display takes, or null when there are none. */
    public Component subheading() {
        if (this.subheadings.isEmpty()) {
            return null;
        }

        // Siblings of an empty parent rather than of each other, so no line's colour bleeds down.
        MutableComponent text = Component.empty().append(this.subheadings.getFirst());
        for (Component line : this.subheadings.subList(1, this.subheadings.size())) {
            text.append("\n").append(line);
        }
        return text;
    }
}
