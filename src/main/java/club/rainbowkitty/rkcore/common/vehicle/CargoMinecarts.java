package club.rainbowkitty.rkcore.common.vehicle;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Registers cart entity types and their cargo component on behalf of a mod, and composes the names
 * both wear. The namespace is a parameter rather than this mod's own id, since callers are other
 * mods that shade this class in.
 */
public final class CargoMinecarts {
    /**
     * Registry path of the cargo component, so a consumer's is at {@code <namespace>:cart_cargo}.
     * One name for every consumer keeps a cart's data readable without knowing which mod made it.
     */
    public static final String CARGO_COMPONENT_PATH = "cart_cargo";

    /** English fallback for {@link #cartName}, matching vanilla's own phrasing. */
    private static final String CART_NAME_FALLBACK = "Minecart with %s";

    // Vanilla's container-cart hitbox and tracking range; every one of chest, hopper, furnace and
    // their kin is built with exactly these.
    private static final float WIDTH = 0.98f;
    private static final float HEIGHT = 0.7f;
    private static final int TRACKING_RANGE = 8;

    private CargoMinecarts() {
    }

    /**
     * Registers the component a cart item carries its cargo in, at
     * {@code <namespace>:cart_cargo}.
     *
     * <p>A block holder rather than a block id, so the value is resolved once at decode and every
     * read is a field access. Persistent codec only, and marked unsynced: the cargo decides the
     * stack's name and model server-side, and both of those reach a client already resolved, so the
     * component itself never has to. Polymer strips it at the component codec, which keeps a
     * vanilla client from being asked to decode a type it does not have.
     *
     * @param namespace the consuming mod's id
     */
    public static DataComponentType<Holder<Block>> registerCargoComponent(String namespace) {
        DataComponentType<Holder<Block>> type = DataComponentType.<Holder<Block>>builder()
                .persistent(BuiltInRegistries.BLOCK.holderByNameCodec())
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(namespace, CARGO_COMPONENT_PATH), type);
        PolymerComponent.registerDataComponent(type);
        return type;
    }

    /**
     * Lets a dispenser place this cart on a rail, as it does every vanilla one.
     *
     * <p>Vanilla registers its behaviour against each cart item by name
     * ({@code DispenseItemBehavior:399-404}), so a mod's own cart item has none and a dispenser
     * loaded with one shoots it out as a dropped item instead. That is invisible until a mod's cart
     * starts arriving where a vanilla one used to — a redirected recipe is enough — and then it is
     * a contraption that quietly stops working.
     *
     * <p>The stack is handed to {@code AbstractMinecart.createMinecart}, so a cart dispensed this
     * way reads its cargo off the item exactly as a hand-placed one does.
     *
     * @param item the cart item, which must already be registered
     * @param type the cart entity type it places
     */
    public static void registerDispenseBehaviour(Item item,
            EntityType<? extends AbstractMinecart> type) {
        DispenserBlock.registerBehavior(item, new MinecartDispenseItemBehavior(type));
    }

    /**
     * A cargo block's own name, e.g. "Birch Chest".
     *
     * <p>Takes the block's description id, so a block this mod's consumer already gives a name to
     * needs no second entry anywhere. The fallback is the registry path title-cased, which is what
     * that name almost always is in English — so a client that declined the server's resource pack
     * still reads something sensible rather than a raw key.
     */
    public static Component cargoName(Holder<Block> cargo) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(cargo.value());
        return Component.translatableWithFallback(
                cargo.value().getDescriptionId(), titleCase(id.getPath()));
    }

    /**
     * A cart's name from its cargo's, e.g. "Minecart with Birch Chest".
     *
     * <p>Vanilla qualifies the cargo rather than the vehicle — {@code chest_minecart} is "Minecart
     * with Chest" — so this composes rather than naming each cargo's cart separately, and a
     * consumer needs one lang key however many cargoes it has.
     *
     * @param patternKey a lang key whose value is a format string taking the cargo name, e.g.
     *     {@code item.<namespace>.minecart_with}
     */
    public static Component cartName(String patternKey, Holder<Block> cargo) {
        return Component.translatableWithFallback(
                patternKey, CART_NAME_FALLBACK, cargoName(cargo));
    }

    // "birch_chest" to "Birch Chest". Only ever a fallback, so it does not have to be right for
    // every path a block could have — just for the ordinary ones.
    private static String titleCase(String path) {
        StringBuilder out = new StringBuilder(path.length());
        boolean startOfWord = true;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '_' || c == '/') {
                out.append(' ');
                startOfWord = true;
            } else {
                out.append(startOfWord ? Character.toUpperCase(c) : c);
                startOfWord = false;
            }
        }
        return out.toString();
    }

    /**
     * A builder shaped like vanilla's container carts.
     *
     * <p>Both sides have to agree on these numbers. A client positions a mounted cargo element from
     * the passenger attachment point of whichever entity type it thinks it is watching — the
     * vanilla cart this one is presented as — so a server type built to a different shape would put
     * the cargo somewhere the client does not.
     */
    public static <T extends AbstractMinecart> EntityType.Builder<T> containerCartBuilder(
            EntityType.EntityFactory<T> factory) {
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .noLootTable()
                .sized(WIDTH, HEIGHT)
                .passengerAttachments(CargoDisplayHolder.PASSENGER_ATTACHMENT_Y)
                .clientTrackingRange(TRACKING_RANGE);
    }

    /**
     * Registers a cart entity type at {@code <namespace>:<path>} which every client is told is
     * {@code clientType}.
     *
     * <p>The substitution is not cosmetic: a server-side-only entity type is absent from a vanilla
     * client's registry, so its spawn packet would be undecodable. Pick the closest vanilla cart,
     * since its model, hitbox and interaction feel are what every client gets.
     *
     * <p>Closest, but not the matching one. A cart that draws its cargo through a
     * {@link CargoDisplayHolder} wants {@code minecart} whatever it carries: a client told it is a
     * container cart draws that cart's own block underneath the stand-in, and blanking the cargo
     * slot to air does not stop it — a plain vanilla chest minecart summoned with an air
     * {@code DisplayState} still shows its chest, so this is vanilla's behaviour rather than
     * anything Polymer or a consumer does, and it is not explained by the render path as written.
     * Nothing is given up by saying minecart: every cart type shares one renderer, one body mesh
     * ({@code LayerDefinitions.java:190}) and one texture, so the substitution costs no fidelity.
     *
     * <p>What it does cost is interaction, and {@link CargoMinecartChest#interact} pays for it —
     * see there for why a plain minecart makes a client send every click twice.
     */
    public static <T extends AbstractMinecart> EntityType<T> register(String namespace, String path,
            EntityType.Builder<T> builder, EntityType<?> clientType) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(
                Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(namespace, path));
        EntityType<T> type =
                Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        PolymerEntityUtils.registerType(type, (registered, context) -> clientType);
        return type;
    }
}
