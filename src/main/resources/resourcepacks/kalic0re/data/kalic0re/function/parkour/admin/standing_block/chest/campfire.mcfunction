# The slot beside the pane shows burning: a campfire when runners may burn on, a splash water bottle when
# catching fire sends them back. Set every tick, which sends nothing while it's unchanged.
# Neither shows its own tooltip lines: the campfire's empty container, which container-preview mods print
# as "Empty", or the potion's "No Effects".
execute if data storage kalic0re:parkour tmp.chest{burning:"allow"} run item replace entity @s container.25 with minecraft:campfire[ \
    !minecraft:container, \
    item_name={translate:"kalic0re.parkour.item.burning_allow",color:"green"}, \
    lore=[{translate:"kalic0re.parkour.item.burning_allow.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"burning"}}]
execute unless data storage kalic0re:parkour tmp.chest{burning:"allow"} run item replace entity @s container.25 with minecraft:splash_potion[ \
    potion_contents={potion:"minecraft:water"}, \
    tooltip_display={hidden_components:["minecraft:potion_contents"]}, \
    item_name={translate:"kalic0re.parkour.item.burning_deny",color:"red"}, \
    lore=[{translate:"kalic0re.parkour.item.burning_deny.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"burning"}}]
data modify storage kalic0re:parkour tmp.chest.record.burningToggleShown set value 1b
