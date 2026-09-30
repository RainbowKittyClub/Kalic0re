$execute unless data storage kalic0re:parkour tmp.saved.equipment.$(key) run return 0
$data modify entity @s item set from storage kalic0re:parkour tmp.saved.equipment.$(key)
$item replace entity @a[tag=kalic0re.parkour.restoring,limit=1] $(slot) from entity @s contents
