# For a tag or a block with no item of its own: a paper named for it, if token_item left slot $(slot) empty.
$execute unless items entity @s container.$(slot) * run item replace entity @s container.$(slot) with minecraft:paper[ \
    item_name={text:"$(block)"},custom_data={kalic0re:{standingBlockChestItem:1b,standingBlock:"$(block)"}}]
