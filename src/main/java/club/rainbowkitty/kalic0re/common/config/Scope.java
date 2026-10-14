package club.rainbowkitty.kalic0re.common.config;

import net.minecraft.server.permissions.PermissionLevel;

// Whose value a setting is, with that scope's default gate from the contract.
enum Scope {
    PLAYER("player", PermissionLevel.ALL, false),
    SERVER("server", PermissionLevel.GAMEMASTERS, true);

    private final String name;
    private final PermissionLevel defaultFallback;
    private final boolean hiddenByDefault;

    private Scope(String name, PermissionLevel defaultFallback, boolean hiddenByDefault) {
        this.name = name;
        this.defaultFallback = defaultFallback;
        this.hiddenByDefault = hiddenByDefault;
    }

    // The scope's name in a declaration, a node and a default page.
    public String serializedName() {
        return name;
    }

    // The level that stands in for the node when no permission provider defines it.
    public PermissionLevel defaultFallback() {
        return defaultFallback;
    }

    // Whether a denied setting of this scope is hidden rather than read-only.
    public boolean hiddenByDefault() {
        return hiddenByDefault;
    }
}
