execute unless data storage kalic0re:parkour tmp.effects[0] run return 0
# Give the hidden_effect chain weakest-first: /effect give hides a weaker, longer effect under a
# stronger one, which rebuilds the saved nesting.
data modify storage kalic0re:parkour tmp.chain set value []
data modify storage kalic0re:parkour tmp.level set from storage kalic0re:parkour tmp.effects[0]
function kalic0re:parkour/effects/unchain
function kalic0re:parkour/effects/give_next
data remove storage kalic0re:parkour tmp.effects[0]
function kalic0re:parkour/effects/restore_next
