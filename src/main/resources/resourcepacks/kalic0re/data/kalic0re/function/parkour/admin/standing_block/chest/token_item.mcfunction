# $(block)'s own item, marked as a token. Fails to instantiate if $(block) has no item, or is a tag.
$item replace entity @s container.$(slot) with $(block)[custom_data={kalic0re:{standingBlockChestItem:1b,standingBlock:"$(block)"}}]
