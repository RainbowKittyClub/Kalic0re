# The Create Firework dialog's Save: its boxes read into storage kalic0re:firework edited (editor/read), then $(save)
# run with {context:"$(context)"} to keep it there; a box that doesn't read is said so, and nothing is kept.
$data modify storage kalic0re:firework input set value $(firework)
scoreboard players set #saveRead kalic0re.firework.tmp 0
execute store result score #saveRead kalic0re.firework.tmp run function kalic0re:firework/editor/read
data remove storage kalic0re:firework input
execute unless score #saveRead kalic0re.firework.tmp matches 1 run return run data remove storage kalic0re:firework edited
$function $(save) {context:"$(context)"}
data remove storage kalic0re:firework edited
