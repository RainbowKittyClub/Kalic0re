# As the chest: emptied, then a token per entry of tmp.chest.stored from the first slot. The pane comes back after.
data modify entity @s Items set value []
data modify storage kalic0re:parkour tmp.chest.record.shown set from storage kalic0re:parkour tmp.chest.stored
data modify storage kalic0re:parkour tmp.chest.fill set value {slot:0}
data modify storage kalic0re:parkour tmp.chest.fill.entries set from storage kalic0re:parkour tmp.chest.stored
function kalic0re:parkour/admin/standing_block/chest/fill_next
