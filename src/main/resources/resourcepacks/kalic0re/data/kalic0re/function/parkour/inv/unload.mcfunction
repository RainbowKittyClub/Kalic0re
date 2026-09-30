# Runs as the transient display, created and killed within one call so no client ever sees it.
function kalic0re:parkour/inv/unload_next
function kalic0re:parkour/inv/equip {key:"head",slot:"armor.head"}
function kalic0re:parkour/inv/equip {key:"chest",slot:"armor.chest"}
function kalic0re:parkour/inv/equip {key:"legs",slot:"armor.legs"}
function kalic0re:parkour/inv/equip {key:"feet",slot:"armor.feet"}
function kalic0re:parkour/inv/equip {key:"offhand",slot:"weapon.offhand"}
kill @s
