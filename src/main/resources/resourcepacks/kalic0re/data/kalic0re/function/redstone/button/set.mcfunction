# At the button $(name): it set to powered $(powered) by way of $(other), and its click_$(click). A
# setblock that only flips powered updates the blocks around the button alone, so redstone it powers
# through the block it's on never hears; one that replaces a powered button with another block also
# updates around that block, as a press or a release does.
$setblock ~ ~ ~ $(other)[face=$(face),facing=$(facing),powered=$(powered)]
$setblock ~ ~ ~ $(name)[face=$(face),facing=$(facing),powered=$(powered)]
$playsound minecraft:block.$(sound).click_$(click) block @a
