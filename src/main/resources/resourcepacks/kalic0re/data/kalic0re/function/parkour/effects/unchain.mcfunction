# Flattens tmp.level and its hidden_effect chain into tmp.chain, deepest first.
data modify storage kalic0re:parkour tmp.chain prepend from storage kalic0re:parkour tmp.level
execute unless data storage kalic0re:parkour tmp.level.hidden_effect run return 0
data modify storage kalic0re:parkour tmp.level set from storage kalic0re:parkour tmp.level.hidden_effect
function kalic0re:parkour/effects/unchain
