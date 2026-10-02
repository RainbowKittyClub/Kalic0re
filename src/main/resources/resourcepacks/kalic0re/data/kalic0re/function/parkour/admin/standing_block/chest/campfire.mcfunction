# The slot beside the pane shows burning: a soul campfire when runners may burn on, a campfire when catching
# fire sends them back. Set every tick, which sends nothing while it's unchanged.
execute if data storage kalic0re:parkour tmp.chest{burning:"allow"} run item replace entity @s container.25 with minecraft:soul_campfire[ \
    item_name={translate:"kalic0re.parkour.item.burning_allow",color:"green"}, \
    lore=[{translate:"kalic0re.parkour.item.burning_allow.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"burning"}}]
execute unless data storage kalic0re:parkour tmp.chest{burning:"allow"} run item replace entity @s container.25 with minecraft:campfire[ \
    item_name={translate:"kalic0re.parkour.item.burning_deny",color:"red"}, \
    lore=[{translate:"kalic0re.parkour.item.burning_deny.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"burning"}}]
data modify storage kalic0re:parkour tmp.chest.record.burningToggleShown set value 1b
