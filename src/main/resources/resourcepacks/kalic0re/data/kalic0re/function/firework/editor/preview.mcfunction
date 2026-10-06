# The Create Firework dialog's Preview: its boxes read as a firework (editor/read) and set off from where you stand,
# nothing kept; the dialog stays open to change it and try again.
$data modify storage kalic0re:firework input set value $(firework)
scoreboard players set #previewRead kalic0re.firework.tmp 0
execute store result score #previewRead kalic0re.firework.tmp run function kalic0re:firework/editor/read
data remove storage kalic0re:firework input
execute unless score #previewRead kalic0re.firework.tmp matches 1 run return run data remove storage kalic0re:firework edited
data modify storage kalic0re:firework launch set from storage kalic0re:firework edited
execute at @s run function kalic0re:firework/launch
data remove storage kalic0re:firework launch
data remove storage kalic0re:firework edited
