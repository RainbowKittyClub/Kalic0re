package club.rainbowkitty.kalic0re.common.positiondata;

/** What {@link PositionMarks#presence} knows about one position. */
public enum Presence {
    /** The position is marked, and its block still holds the kind. */
    PRESENT,
    /** The position is loaded and not marked. */
    ABSENT,
    /**
     * Nothing can be said: the position is on a client, in a chunk that is not loaded, or the call
     * was not made on the server thread.
     */
    UNKNOWN
}
