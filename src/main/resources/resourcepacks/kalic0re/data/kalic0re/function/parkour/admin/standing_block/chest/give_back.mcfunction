# At the chest's owner: $(item), the stack they put in, picked up at once. Summoned whole, as `execute summon`
# makes an item with no stack, which is removed before anything can fill it; the item codec ignores its Slot.
$summon minecraft:item ~ ~ ~ {Item:$(item),PickupDelay:0s}
