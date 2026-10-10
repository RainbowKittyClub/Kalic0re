# At a button: its id, face and facing into storage kalic0re:redstone button, with another button's id
# to set it by way of, and the name its clicks' sounds go by. Fails on a button vanilla lacks.
execute unless function kalic0re:block_state/read run return fail
data modify storage kalic0re:redstone button set value {other:"minecraft:stone_button",sound:"wooden_button"}
data modify storage kalic0re:redstone button.name set from storage kalic0re:block_state state.Name
data modify storage kalic0re:redstone button.face set from storage kalic0re:block_state state.Properties.face
data modify storage kalic0re:redstone button.facing set from storage kalic0re:block_state state.Properties.facing
data remove storage kalic0re:block_state state
execute if block ~ ~ ~ minecraft:stone_button run \
    data modify storage kalic0re:redstone button.other set value "minecraft:oak_button"
execute if block ~ ~ ~ #minecraft:stone_buttons run \
    data modify storage kalic0re:redstone button.sound set value "stone_button"
execute if block ~ ~ ~ minecraft:cherry_button run \
    data modify storage kalic0re:redstone button.sound set value "cherry_wood_button"
execute if block ~ ~ ~ minecraft:bamboo_button run \
    data modify storage kalic0re:redstone button.sound set value "bamboo_wood_button"
execute if block ~ ~ ~ minecraft:crimson_button run \
    data modify storage kalic0re:redstone button.sound set value "nether_wood_button"
execute if block ~ ~ ~ minecraft:warped_button run \
    data modify storage kalic0re:redstone button.sound set value "nether_wood_button"
return 1
