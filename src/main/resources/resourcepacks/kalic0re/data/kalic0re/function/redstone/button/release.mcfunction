# As a pushed button's marker, at it, its ticks up: the button let up, with its click, and the marker gone.
execute unless function kalic0re:redstone/button/read run return run kill @s
data modify storage kalic0re:redstone button merge value {powered:"false",click:"off"}
function kalic0re:redstone/button/set with storage kalic0re:redstone button
data remove storage kalic0re:redstone button
kill @s
