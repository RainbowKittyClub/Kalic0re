# Direction markers: text displays tagged kalic0re.parkour.directionMarker, chevrons showing runners the way, move
# back and forth along the way they point, 0.2 either side on a sine with a period of 1 s. directionMarkerSine is
# one period, a tick an entry, in ten-thousandths of a block: its head is this tick's offset, and it turns a place
# a tick, so no time is read. Moving, turning or copying a marker needs nothing; to change its translation, run
# direction_marker/stop as it first, then tag it again.
execute store result score #directionMarkerOffset kalic0re.parkour.tmp run data get storage kalic0re:parkour directionMarkerSine[0]
data modify storage kalic0re:parkour directionMarkerSine append from storage kalic0re:parkour directionMarkerSine[0]
data remove storage kalic0re:parkour directionMarkerSine[0]
data modify storage kalic0re:parkour tmp.directionMarkerTranslation set value [0.0f,0.0f,0.0f]
execute as @e[type=minecraft:text_display,tag=kalic0re.parkour.directionMarker] run function kalic0re:parkour/direction_marker/move
data remove storage kalic0re:parkour tmp.directionMarkerTranslation
