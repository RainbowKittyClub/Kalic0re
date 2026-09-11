package club.rainbowkitty.rkcore.common.armor;

/** How a piece is drawn on its wearer. */
public enum ArmorBackend {
    /**
     * A custom equipment asset: vanilla's armour geometry wearing this set's textures. Works in
     * every slot and on every wearer vanilla has a layer for.
     */
    LAYER,
    /**
     * The item's own model, drawn on the head. Only the head slot has this — a client draws a head
     * item's model exactly when the stack's {@code equippable} names no asset — and only on
     * entities whose renderer carries a head layer, which is players, humanoid mobs, villagers,
     * illagers, piglins, copper golems and armour stands.
     */
    ITEM_MODEL
}
