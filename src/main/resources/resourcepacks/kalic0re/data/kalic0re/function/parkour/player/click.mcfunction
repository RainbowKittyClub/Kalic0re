# Main hand only: a carrot in each hand would count one click twice.
execute if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkour:{item:"checkpoint"}}}] \
    run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/checkpoint"}
execute if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkour:{item:"restart"}}}] \
    run function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/item/restart"}
execute if items entity @s weapon.mainhand *[custom_data~{kalic0re:{parkour:{item:"quit"}}}] \
    run scoreboard players set @s kalic0re.parkour.isActive 0
scoreboard players reset @s kalic0re.parkour.click
