# Macro args are the saved stack itself; the item codec ignores its Slot key.
data modify entity @s item set from storage kalic0re:parkour tmp.saved.inventory[0]
$item replace entity @a[tag=kalic0re.parkour.restoring,limit=1] container.$(Slot) from entity @s contents
