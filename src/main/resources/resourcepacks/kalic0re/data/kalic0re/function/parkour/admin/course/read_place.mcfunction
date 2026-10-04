# 1 when $(position) and $(rotation) read as a place, before leaderboard_sized takes them, whose own macro would fail
# whole on text that doesn't. Alone in a function of its own, so such text fails only this, which returns nothing.
$execute positioned $(position) rotated $(rotation) run return 1
