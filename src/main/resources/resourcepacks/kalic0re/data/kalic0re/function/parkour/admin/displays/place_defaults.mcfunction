# As a player opening the Place dialog: Position and Rotation's starting text in kalic0re:dialog args, on the face
# they look at within 64 (place_face), else ahead of them (place_ahead).
scoreboard players set #placeBlockFound kalic0re.parkour.tmp 0
scoreboard players set #rayStepsLeft kalic0re.parkour.tmp 640
execute anchored eyes positioned ^ ^ ^ anchored feet run \
    function kalic0re:parkour/admin/look_ray/step {hit:"kalic0re:parkour/admin/displays/place_ray_hit"}
execute if score #placeBlockFound kalic0re.parkour.tmp matches 1 if function kalic0re:parkour/admin/displays/place_face run return 0
function kalic0re:parkour/admin/displays/place_ahead
