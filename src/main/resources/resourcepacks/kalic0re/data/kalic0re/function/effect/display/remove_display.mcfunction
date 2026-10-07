# execute as <display> run function kalic0re:effect/display/remove_display
# e.g. execute as @e[tag=gate] run function kalic0re:effect/display/remove_display
# Removes this display and every display riding it, at any depth, such as the blocks of one made by
# create_from_blocks; a move in progress ends with it. Anything else riding it, a player say, is let off
# instead, and the world's blocks stay as they are. Fails, removing nothing, unless run as a display.
execute unless entity @s[type=#kalic0re:effect/displays] run return fail
execute on passengers if entity @s[type=#kalic0re:effect/displays] run \
    function kalic0re:effect/display/remove_display
return run kill @s
