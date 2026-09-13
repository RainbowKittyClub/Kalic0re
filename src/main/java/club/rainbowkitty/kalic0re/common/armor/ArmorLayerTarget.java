package club.rainbowkitty.kalic0re.common.armor;

/**
 * Which entity model a set's layers are drawn on, mirroring {@code EquipmentClientInfo.LayerType}
 * by serialized name.
 *
 * <p>The vanilla enum is {@code @Environment(CLIENT)} and a dedicated server has no such class to
 * load, so a declaration that the registration path reads cannot name it. Datagen maps these onto
 * the vanilla constants, and is the only code that may.
 */
public enum ArmorLayerTarget {
    /** Humanoid body, arms and head — every armour slot but leggings. */
    HUMANOID("humanoid", true),
    /** The inner humanoid model vanilla draws leggings with. */
    HUMANOID_LEGGINGS("humanoid_leggings", true),
    /** Humanoid babies, which use this for every slot including leggings. */
    HUMANOID_BABY("humanoid_baby", true),
    /** Elytra. */
    WINGS("wings", false),
    /** Wolf body armour. */
    WOLF_BODY("wolf_body", false),
    /** Horse body armour. */
    HORSE_BODY("horse_body", false),
    /** Llama and trader llama carpets. */
    LLAMA_BODY("llama_body", false),
    /** Happy ghast harnesses. */
    HAPPY_GHAST_BODY("happy_ghast_body", false),
    /** Nautilus body armour. */
    NAUTILUS_BODY("nautilus_body", false),
    /** Pig saddle. */
    PIG_SADDLE("pig_saddle", false),
    /** Strider saddle. */
    STRIDER_SADDLE("strider_saddle", false),
    /** Camel saddle. */
    CAMEL_SADDLE("camel_saddle", false),
    /** Husk camel saddle. */
    CAMEL_HUSK_SADDLE("camel_husk_saddle", false),
    /** Horse saddle. */
    HORSE_SADDLE("horse_saddle", false),
    /** Donkey saddle. */
    DONKEY_SADDLE("donkey_saddle", false),
    /** Mule saddle. */
    MULE_SADDLE("mule_saddle", false),
    /** Zombie horse saddle. */
    ZOMBIE_HORSE_SADDLE("zombie_horse_saddle", false),
    /** Skeleton horse saddle. */
    SKELETON_HORSE_SADDLE("skeleton_horse_saddle", false),
    /** Nautilus saddle. */
    NAUTILUS_SADDLE("nautilus_saddle", false);

    private final String serializedName;
    private final boolean humanoid;

    ArmorLayerTarget(String serializedName, boolean humanoid) {
        this.serializedName = serializedName;
        this.humanoid = humanoid;
    }

    /** The vanilla enum constant's serialized name, which is also its texture directory. */
    public String serializedName() {
        return serializedName;
    }

    /** Whether this target is drawn on a humanoid model, as opposed to a mount or pet. */
    public boolean humanoid() {
        return humanoid;
    }
}
