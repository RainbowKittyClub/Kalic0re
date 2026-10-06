# 1 when storage kalic0re:text place's position and rotation are coordinates alone and read as a place something can be
# summoned at, as positioned and rotated read them (~ from where this runs); place is then cleared.
scoreboard players set #textPlace kalic0re.text.tmp 0
execute store result score #textPlace kalic0re.text.tmp run function kalic0re:text/place_checks
data remove storage kalic0re:text place
return run scoreboard players get #textPlace kalic0re.text.tmp
