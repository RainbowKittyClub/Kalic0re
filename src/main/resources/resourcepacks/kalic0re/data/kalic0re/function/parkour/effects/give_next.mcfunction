execute unless data storage kalic0re:parkour tmp.chain[0] run return 0
# Defaults first: the saved format omits amplifier 0 and shown particles. Hidden entries carry no
# id, so every level takes the top-level effect's.
data modify storage kalic0re:parkour tmp.give set value {amplifier:0b,show_particles:1b,hide:"false"}
data modify storage kalic0re:parkour tmp.give merge from storage kalic0re:parkour tmp.chain[0]
data modify storage kalic0re:parkour tmp.give.id set from storage kalic0re:parkour tmp.effects[0].id
function kalic0re:parkour/effects/give
data remove storage kalic0re:parkour tmp.chain[0]
function kalic0re:parkour/effects/give_next
