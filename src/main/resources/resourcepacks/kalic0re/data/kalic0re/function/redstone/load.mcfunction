# Pushed buttons: the ticks each one's marker holds it down for yet; and arithmetic.
scoreboard objectives add kalic0re.redstone.buttonTicksLeft dummy
scoreboard objectives add kalic0re.redstone.tmp dummy
# A push a reload or restart cut into lets its button up when due.
schedule function kalic0re:redstone/button/tick 1t replace
