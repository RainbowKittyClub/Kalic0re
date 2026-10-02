# The last slot shows the mode: green for allowing only the blocks, red for forbidding them. Set every tick, which
# sends nothing while it's unchanged.
execute if data storage kalic0re:parkour tmp.chest{mode:"allow"} run item replace entity @s container.26 with minecraft:green_stained_glass_pane[ \
    item_name={translate:"kalic0re.parkour.item.standing_blocks_allow",color:"green"}, \
    lore=[{translate:"kalic0re.parkour.item.standing_blocks_allow.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"mode"}}]
execute unless data storage kalic0re:parkour tmp.chest{mode:"allow"} run item replace entity @s container.26 with minecraft:red_stained_glass_pane[ \
    item_name={translate:"kalic0re.parkour.item.standing_blocks_deny",color:"red"}, \
    lore=[{translate:"kalic0re.parkour.item.standing_blocks_deny.lore",color:"gray",italic:false}], \
    custom_data={kalic0re:{standingBlockChestItem:1b,standingBlockChest:"mode"}}]
data modify storage kalic0re:parkour tmp.chest.record.paneShown set value 1b
