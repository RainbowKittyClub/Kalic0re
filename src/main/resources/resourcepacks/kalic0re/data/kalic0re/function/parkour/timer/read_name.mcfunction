# As a transient item_display at the runner: fill_player_head reads the loot context's "this",
# which item modify sets to whoever runs it, so the runner modifies this display's head.
item replace entity @s contents with minecraft:player_head
tag @s add kalic0re.parkour.nameProbe
execute as @a[tag=kalic0re.parkour.nameProbeSubject,limit=1] at @s run item modify entity \
    @e[type=item_display,tag=kalic0re.parkour.nameProbe,distance=..1,limit=1] contents \
    {function:"minecraft:fill_player_head",entity:"this"}
data modify storage kalic0re:parkour tmp.record.player set from entity @s item.components."minecraft:profile".name
kill @s
