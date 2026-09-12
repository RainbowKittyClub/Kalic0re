package club.rainbowkitty.rkcore.common.datagen;

/**
 * Whether this JVM is a Fabric datagen pass rather than a real server boot.
 *
 * <p>Worth knowing at registration time, because a mod that registers content conditionally — on an
 * optional mod being present, say — would otherwise generate assets that depend on what happened to
 * be installed where datagen ran. Registering everything under datagen and gating only at boot
 * keeps the generated pack the same wherever it is built.
 *
 * <p>Shared by relocation, not a mod dependency (see each consuming mod's {@code build.gradle}).
 */
public final class DatagenRun {
    /**
     * Fabric API's own signal for a {@code runDatagen} pass, read from the system property it sets.
     * Verified against {@code FabricDataGenHelper}'s bytecode: no public constant exposes it, so
     * the property name is the only available handle.
     */
    public static final boolean ACTIVE = System.getProperty("fabric-api.datagen") != null;

    private DatagenRun() {
    }
}
