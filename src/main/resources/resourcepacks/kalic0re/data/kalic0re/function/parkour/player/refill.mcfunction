# Four single-slot checks when the items sit where they were given.
execute if items entity @s hotbar.0 *[custom_data~{kalic0re:{parkour:{item:"checkpoint"}}}] \
    if items entity @s hotbar.4 *[custom_data~{kalic0re:{parkour:{item:"restart"}}}] \
    if items entity @s hotbar.8 *[custom_data~{kalic0re:{parkour:{item:"quit"}}}] \
    if items entity @s armor.body *[custom_data~{kalic0re:{parkour:{item:"invulnerability"}}}] \
    run return 0

# /data can't set a player's Invulnerable, so the body slot, which no screen shows, holds an enchantment
# whose damage_immunity stops the same hits. The slot only takes a body equippable; exit's clear empties it.
execute unless items entity @s armor.body *[custom_data~{kalic0re:{parkour:{item:"invulnerability"}}}] run \
    item replace entity @s armor.body with minecraft:barrier[custom_data={kalic0re:{parkour:{item:"invulnerability"}}}, \
    equippable={slot:"body",equip_sound:"minecraft:intentionally_empty"}, \
    enchantments={"kalic0re:parkour/runner_invulnerability":1}]

# A clear of 0 only counts, and it also sees the cursor, so an item being dragged is not given twice.
execute store result score #have kalic0re.parkour.tmp run \
    clear @s *[custom_data~{kalic0re:{parkour:{item:"checkpoint"}}}] 0
execute if score #have kalic0re.parkour.tmp matches 0 run item replace entity @s hotbar.0 with \
    minecraft:carrot_on_a_stick[custom_data={kalic0re:{parkour:{item:"checkpoint"}}}, \
    item_name={translate:"kalic0re.parkour.item.checkpoint",fallback:"Checkpoint"},item_model="minecraft:ender_pearl"]

execute store result score #have kalic0re.parkour.tmp run \
    clear @s *[custom_data~{kalic0re:{parkour:{item:"restart"}}}] 0
execute if score #have kalic0re.parkour.tmp matches 0 run item replace entity @s hotbar.4 with \
    minecraft:carrot_on_a_stick[custom_data={kalic0re:{parkour:{item:"restart"}}}, \
    item_name={translate:"kalic0re.parkour.item.restart",fallback:"Restart"},item_model="minecraft:clock"]

execute store result score #have kalic0re.parkour.tmp run \
    clear @s *[custom_data~{kalic0re:{parkour:{item:"quit"}}}] 0
execute if score #have kalic0re.parkour.tmp matches 0 run item replace entity @s hotbar.8 with \
    minecraft:carrot_on_a_stick[custom_data={kalic0re:{parkour:{item:"quit"}}}, \
    item_name={translate:"kalic0re.parkour.item.quit",fallback:"Quit"},item_model="minecraft:barrier"]
