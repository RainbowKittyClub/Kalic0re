package club.rainbowkitty.kalic0re.common.click;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

/**
 * Keeps a right-click the server has taken with one hand from being answered again with the other.
 *
 * <p>A client offers each right-click to its hands in turn, main hand first: to the block or entity
 * under the crosshair, then to the item alone ({@code Minecraft#startUseItem}). It stops at the
 * first attempt it predicts will succeed, and sends the server every attempt up to that one. It
 * predicts from what it knows, though, and it knows neither a Polymer item (only the vanilla item
 * standing in for it) nor anything server-side code does with a click. So a click the server
 * takes with the main hand can still reach the off hand: two items placed on a campfire at once,
 * or a link item trying the same pair twice. The server answers each attempt on its own, since
 * vanilla has no notion of a click there.
 *
 * <p>This rebuilds the click from the order the attempts arrive in, and drops every attempt after
 * the first the server answers with {@linkplain InteractionResult.Success success}, as the client
 * would have stopped there had it known. A failure at a block stops the client too, but not here:
 * that is how a claim mod refuses a block, and a vanilla client has always gone on past it to
 * the item.
 *
 * <p>This holds the logic. Each consumer owns the mixin on {@code ServerGamePacketListenerImpl}
 * that routes through it, since mixins are never shared code:
 * <ul>
 *   <li>{@code handleUseItemOn} wraps its {@code ServerPlayerGameMode#useItemOn} call in
 *       {@link #atBlock};
 *   <li>{@code handleUseItem} wraps its {@code ServerPlayerGameMode#useItem} call in
 *       {@link #withItem};
 *   <li>{@code handleInteract} asks {@link #offerAtEntity} at its {@code packet.hand()} call and
 *       returns if it refuses, then wraps its {@code ServerPlayer#interactOn} call in
 *       {@link #atEntity}.
 * </ul>
 * Fabric's {@code UseBlockCallback} and {@code UseItemCallback} answer inside those wrapped calls,
 * so their answers count. Its {@code UseEntityCallback} does not: Fabric runs it in the packet
 * handler, past {@link #offerAtEntity}, and returns from the handler when one answers. A callback
 * that takes an entity click calls {@link #take} itself.
 *
 * <p>Several consumers installed together each keep a copy of this state, relocated into each, and
 * an attempt any copy drops is dropped. The copies see the same attempts in the same order, except
 * those an earlier copy dropped, and a {@link #take} reaches only the copy it was called on. Either
 * way, the copy that saw the click taken drops the rest of it.
 */
public final class HandClicks {
    // How many ticks one click's attempts may span. The client sends them back to back, so they
    // arrive together, but the server may start a tick's batch of packets (PacketProcessor)
    // between two of them.
    private static final int CLICK_SPAN_TICKS = 1;

    // Where an attempt falls within its hand's turn: first at the block or entity, then the item on
    // its own.
    private static final int AT_TARGET = 0;
    private static final int WITH_ITEM = 1;

    // The latest click on each connection. Weak, so a player leaving drops their entry without
    // anything here being told.
    private static final Map<ServerGamePacketListenerImpl, Click> CLICKS = new WeakHashMap<>();

    // Static-only; blocks instantiation.
    private HandClicks() {}

    /**
     * Makes one hand's attempt at the block under the crosshair, unless its click was already
     * taken, in which case the attempt is dropped and answers {@link InteractionResult#PASS}.
     *
     * @param connection the connection whose packet this is
     * @param hand the hand the attempt is made with
     * @param attempt the server's own handling of the attempt
     */
    public static InteractionResult atBlock(ServerGamePacketListenerImpl connection,
            InteractionHand hand, Supplier<InteractionResult> attempt) {
        return attempt(connection, hand, AT_TARGET, attempt);
    }

    /**
     * Makes one hand's attempt with its item alone, unless its click was already taken, in which
     * case the attempt is dropped and answers {@link InteractionResult#PASS}.
     *
     * @param connection the connection whose packet this is
     * @param hand the hand the attempt is made with
     * @param attempt the server's own handling of the attempt
     */
    public static InteractionResult withItem(ServerGamePacketListenerImpl connection,
            InteractionHand hand, Supplier<InteractionResult> attempt) {
        return attempt(connection, hand, WITH_ITEM, attempt);
    }

    /**
     * Whether one hand's attempt at an entity may go ahead: false if its click was already taken,
     * and the packet handler should return before anything answers it.
     *
     * @param connection the connection whose packet this is
     * @param hand the hand the attempt is made with
     */
    public static boolean offerAtEntity(ServerGamePacketListenerImpl connection,
            InteractionHand hand) {
        return offer(connection, hand, AT_TARGET);
    }

    /**
     * Makes the attempt at an entity that {@link #offerAtEntity} let through and every
     * {@code UseEntityCallback} passed on.
     *
     * @param connection the connection whose packet this is
     * @param attempt the server's own handling of the attempt
     */
    public static InteractionResult atEntity(ServerGamePacketListenerImpl connection,
            Supplier<InteractionResult> attempt) {
        InteractionResult result = attempt.get();
        if (result instanceof InteractionResult.Success) {
            take(connection.player);
        }
        return result;
    }

    /**
     * Marks the player's current click as taken, for code that answers it where no wrapped call
     * sees: a {@code UseEntityCallback} succeeding on the server. Every later attempt in the
     * click is dropped.
     */
    public static void take(ServerPlayer player) {
        Click click = CLICKS.get(player.connection);
        if (click != null) {
            click.taken = true;
        }
    }

    // Makes an attempt the click still allows, and marks the click taken if it succeeds.
    private static InteractionResult attempt(ServerGamePacketListenerImpl connection,
            InteractionHand hand, int phase, Supplier<InteractionResult> attempt) {
        if (!offer(connection, hand, phase)) {
            return InteractionResult.PASS;
        }
        InteractionResult result = attempt.get();
        if (result instanceof InteractionResult.Success) {
            take(connection.player);
        }
        return result;
    }

    // Places an attempt in its click and says whether it goes ahead. An attempt starts a new click
    // unless it follows the last one in the client's order within CLICK_SPAN_TICKS, since every
    // click starts again from the main hand. An attempt in a taken click is dropped. The first
    // drop in a click resends the player's inventory: the client may already have acted out the
    // dropped attempt, a block placed from the stack, and the server sends no correction for a slot
    // it never changed.
    private static boolean offer(ServerGamePacketListenerImpl connection, InteractionHand hand,
            int phase) {
        ServerPlayer player = connection.player;
        Click click = CLICKS.computeIfAbsent(connection, ignored -> new Click());
        int now = player.level().getServer().getTickCount();
        int step = hand.ordinal() * 2 + phase;
        if (step <= click.step || now - click.tick > CLICK_SPAN_TICKS) {
            click.taken = false;
            click.resent = false;
        }
        click.step = step;
        click.tick = now;
        if (!click.taken) {
            return true;
        }
        if (!click.resent) {
            click.resent = true;
            player.inventoryMenu.sendAllDataToRemote();
        }
        return false;
    }

    // One connection's latest click.
    private static final class Click {
        // The tick and order position of its latest attempt. The order position starts past the
        // last, so the first attempt ever starts a click.
        private int tick;
        private int step = Integer.MAX_VALUE;

        // Whether the server has taken the click, and so drops the rest of its attempts.
        private boolean taken;

        // Whether a drop has already resent the inventory.
        private boolean resent;
    }
}
