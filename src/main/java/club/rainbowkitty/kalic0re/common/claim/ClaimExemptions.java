package club.rainbowkitty.kalic0re.common.claim;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * What the loaded claim mods would say about a block if they did not exempt blocks by id. Claim
 * mods commonly make a crafting table public, by id, and that id-based view gets two things wrong
 * about a block that keeps items or stands in for another:
 *
 * <ul>
 *   <li>A player let in only by an exemption should not reach what the block stores:
 *       {@link #refusedWithoutExemption} says when to give them something of their own instead.
 *   <li>A modded block standing in for a vanilla one should be exempt wherever the vanilla one is:
 *       {@link #registerStandIn} opens it for anyone the claim mods would let use the vanilla block
 *       there, ahead of the claim mods' own checks.
 * </ul>
 *
 * <p>One {@link ClaimProbe} per claim mod answers for it: GOML, Flan, Open Parties and Claims,
 * FTB Chunks, and any other implementing the Common Protection API. A probe that stops linking
 * against its mod's API answers as strictly as it can from then on; one whose mod throws answers
 * strictly for that call.
 */
public final class ClaimExemptions {
    private static final Logger LOGGER = LoggerFactory.getLogger("kalic0re/claim");

    // The phases the claim mods check block use in, all of which a stand-in listener runs ahead
    // of. FTB Chunks listens in the default phase.
    private static final List<Identifier> CLAIM_PHASES = List.of(
            Identifier.fromNamespaceAndPath("goml", "protection"),
            Identifier.fromNamespaceAndPath("flan", "events"),
            Identifier.fromNamespaceAndPath("openpartiesandclaims", "protection"),
            Event.DEFAULT_PHASE);

    private static final List<ClaimProbe> PROBES = new CopyOnWriteArrayList<>();
    private static boolean initialized;

    private ClaimExemptions() {
    }

    /** Registers a probe for each claim mod loaded. Safe to call from every consumer. */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerIfLoaded("goml", GomlClaimProbe::new);
        registerIfLoaded("flan", FlanClaimProbe::new);
        registerIfLoaded("openpartiesandclaims", OpacClaimProbe::new);
        registerIfLoaded("ftbchunks", FtbChunksClaimProbe::new);
        registerIfLoaded("common-protection-api", CommonProtectionClaimProbe::new);
    }

    /** Adds a probe for a claim mod not built in. */
    public static void register(ClaimProbe probe) {
        PROBES.add(new GuardedProbe(probe));
    }

    /**
     * Whether any loaded claim mod would refuse this player the block at {@code pos} if it made no
     * exemption for that block. False with no claim mod loaded.
     */
    public static boolean refusedWithoutExemption(ServerPlayer player, BlockPos pos) {
        for (ClaimProbe probe : PROBES) {
            if (probe.refusesWithoutExemption(player, pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether every loaded claim mod would let this player use {@code standIn} at {@code hit}'s
     * position. True with no claim mod loaded.
     */
    public static boolean allowsStandIn(ServerPlayer player, InteractionHand hand,
            BlockHitResult hit, BlockState standIn) {
        for (ClaimProbe probe : PROBES) {
            if (!probe.allowsStandIn(player, hand, hit, standIn)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes the claim mods treat blocks matching {@code block} as {@code standIn}, for use with an
     * empty or ordinary hand. A listener in {@code phase}, ordered ahead of every claim mod's own,
     * uses such a block itself when some claim mod would refuse it but all would allow the
     * stand-in; the claim mods' listeners then never see that use. Otherwise it passes.
     *
     * <p>Only probed claim mods are asked. A protection mod with no probe here, and not on the
     * Common Protection API, is skipped for a use this listener takes over.
     *
     * @param phase a phase of the consumer's own
     * @param block which blocks stand in
     * @param standIn the block the claim mods should judge them as
     */
    public static void registerStandIn(Identifier phase, Predicate<BlockState> block,
            BlockState standIn) {
        for (Identifier claimPhase : CLAIM_PHASES) {
            UseBlockCallback.EVENT.addPhaseOrdering(phase, claimPhase);
        }
        UseBlockCallback.EVENT.register(phase, (player, level, hand, hit) ->
                useAsStandIn(player, level, hand, hit, block, standIn));
    }

    // Uses a stand-in block the claim mods would refuse by its own id but allow as the stand-in.
    private static InteractionResult useAsStandIn(Player player, Level level, InteractionHand hand,
            BlockHitResult hit, Predicate<BlockState> block, BlockState standIn) {
        if (PROBES.isEmpty() || hand != InteractionHand.MAIN_HAND
                || !(player instanceof ServerPlayer serverPlayer) || player.isSpectator()) {
            return InteractionResult.PASS;
        }
        BlockState state = level.getBlockState(hit.getBlockPos());
        // Sneaking with anything in hand uses the item, not the block, in vanilla as here.
        boolean suppressed = player.isSecondaryUseActive()
                && !(player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty());
        if (!block.test(state) || suppressed
                || allowsStandIn(serverPlayer, hand, hit, state)
                || !allowsStandIn(serverPlayer, hand, hit, standIn)) {
            return InteractionResult.PASS;
        }
        return state.useWithoutItem(level, player, hit);
    }

    // Registers a built-in probe when its claim mod is loaded, surviving a probe that cannot link.
    private static void registerIfLoaded(String modId, Supplier<ClaimProbe> probe) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            return;
        }
        try {
            register(probe.get());
        } catch (LinkageError e) {
            LOGGER.error("Could not link the claim probe for {}; its claims are not probed",
                    modId, e);
        }
    }

    // A probe that answers strictly when its mod fails: refuses without an exemption, never allows
    // a stand-in. A link failure lasts; a thrown exception, one call. Logs each kind once.
    private static final class GuardedProbe implements ClaimProbe {
        private final ClaimProbe probe;
        private volatile boolean broken;
        private volatile boolean threw;

        // Wraps one probe.
        private GuardedProbe(ClaimProbe probe) {
            this.probe = probe;
        }

        @Override
        public boolean refusesWithoutExemption(ServerPlayer player, BlockPos pos) {
            if (!broken) {
                try {
                    return probe.refusesWithoutExemption(player, pos);
                } catch (LinkageError e) {
                    fail(e);
                } catch (RuntimeException e) {
                    warn(e);
                }
            }
            return true;
        }

        @Override
        public boolean allowsStandIn(ServerPlayer player, InteractionHand hand,
                BlockHitResult hit, BlockState standIn) {
            if (!broken) {
                try {
                    return probe.allowsStandIn(player, hand, hit, standIn);
                } catch (LinkageError e) {
                    fail(e);
                } catch (RuntimeException e) {
                    warn(e);
                }
            }
            return false;
        }

        // Logs the first exception its mod throws.
        private void warn(RuntimeException e) {
            if (!threw) {
                threw = true;
                LOGGER.warn("Claim probe {} threw; answering strictly for that call",
                        probe.getClass().getSimpleName(), e);
            }
        }

        // Marks the probe broken, logging the first failure.
        private void fail(LinkageError e) {
            if (!broken) {
                broken = true;
                LOGGER.error("Claim probe {} no longer links; answering strictly from now on",
                        probe.getClass().getSimpleName(), e);
            }
        }
    }
}
