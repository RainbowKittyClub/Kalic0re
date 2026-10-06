# 1 when tmp.dialogTexts' position and rotation read as a place (text/place_readable), so ~ counts from you and the way
# you face.
data modify storage kalic0re:text place set value {}
data modify storage kalic0re:text place.position set from storage kalic0re:parkour tmp.dialogTexts.position
data modify storage kalic0re:text place.rotation set from storage kalic0re:parkour tmp.dialogTexts.rotation
return run function kalic0re:text/place_readable
