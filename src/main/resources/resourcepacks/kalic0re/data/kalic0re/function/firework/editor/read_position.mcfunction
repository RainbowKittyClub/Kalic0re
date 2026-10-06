# For read: the Position box, a place a firework can be set off (text/place_readable, ~ counting from where it's
# checked), as edited.position.
data modify storage kalic0re:text place set value {rotation:"~ ~"}
data modify storage kalic0re:text place.position set from storage kalic0re:firework input.texts.position
execute unless function kalic0re:text/place_readable run return fail
data modify storage kalic0re:firework edited.position set from storage kalic0re:firework input.texts.position
return 1
