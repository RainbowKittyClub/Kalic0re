# 1 when tmp.dialogTexts' position and rotation are coordinates alone and read as a place, as positioned and
# rotated read them, so ~ counts from you and the way you face.
data modify storage kalic0re:parkour tmp.textCheck set value {rest:""}
data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.position
execute unless function kalic0re:parkour/admin/text/only_coordinate_characters run return fail
data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.rotation
execute unless function kalic0re:parkour/admin/text/only_coordinate_characters run return fail
data remove storage kalic0re:parkour tmp.textCheck
scoreboard players set #placeTextReads kalic0re.parkour.tmp 0
execute store result score #placeTextReads kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/text/place_reads with storage kalic0re:parkour tmp.dialogTexts
return run scoreboard players get #placeTextReads kalic0re.parkour.tmp
