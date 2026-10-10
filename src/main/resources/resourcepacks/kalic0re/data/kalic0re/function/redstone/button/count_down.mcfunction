# As a pushed button's marker, at it: gone, and nothing more, once the button is broken or let up some
# other way; otherwise a tick off, and the button let up when none are left.
execute unless block ~ ~ ~ #minecraft:buttons[powered=true] run return run kill @s
scoreboard players remove @s kalic0re.redstone.buttonTicksLeft 1
execute if score @s kalic0re.redstone.buttonTicksLeft matches ..0 run function kalic0re:redstone/button/release
