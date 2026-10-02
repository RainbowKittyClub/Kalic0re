# A token for $(block) in slot $(slot): an entry, unless an earlier slot already gave it, when it's spare.
$execute if data storage kalic0re:parkour tmp.chest.entries[{block:"$(block)"}] run return run \
    item replace entity @s container.$(slot) with minecraft:air
$data modify storage kalic0re:parkour tmp.chest.entries append value {block:"$(block)"}
