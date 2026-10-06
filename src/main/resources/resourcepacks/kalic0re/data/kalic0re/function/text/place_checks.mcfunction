# For place_readable: each check in turn, failing at the first that doesn't pass.
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:text place.position
execute unless function kalic0re:text/only_coordinates run return fail
data modify storage kalic0re:text check set value ""
data modify storage kalic0re:text check set from storage kalic0re:text place.rotation
execute unless function kalic0re:text/only_coordinates run return fail
scoreboard players set #textPlaceSummons kalic0re.text.tmp 0
execute store result score #textPlaceSummons kalic0re.text.tmp run function kalic0re:text/place_summons with storage kalic0re:text place
return run scoreboard players get #textPlaceSummons kalic0re.text.tmp
