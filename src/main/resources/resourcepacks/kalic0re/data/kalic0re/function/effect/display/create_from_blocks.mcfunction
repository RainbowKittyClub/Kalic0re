# /function kalic0re:effect/display/create_from_blocks {from:"<x y z>",to:"<x y z>",name:"<name>",tags:[<tag>,…],scale:[<x>,<y>,<z>]}
# e.g. {from:"10 64 10",to:"12 66 10",name:"Gate",tags:["gate"],scale:[1,1,1]}; name:"" and tags:[] for neither.
# Makes one display of the blocks in the cuboid from–to, leaving the blocks as they are: an invisible
# root display, named and tagged so, at the centre of the display's top layer, with a block display
# riding it for each block. scale sizes the blocks along x, y and z and spaces them to match, so they
# still meet, on the cuboid's ground and about its centre across: [1,0.5,1] stands half as tall,
# [1,1,0.125] is a pane an eighth as thick. Coordinates read as in a /clone of your own. Every part
# takes the light at the root, so a solid block left there draws the display dark. Returns how many
# blocks it shows, and fails, making nothing, on none or on a scale under 1/1024. It reads scales to
# 1024ths, and works while the display spans under a million blocks. A block vanilla lacks gets no
# part. Each block takes about 45 commands of max_command_sequence_length, 65536 by default: some 1450
# a call.
# To clear the blocks after, fill them strict: a plain fill drops a lantern or torch as an item when it
# removes what holds it up first. To remove the display, see kalic0re:effect/display/remove_display.
data modify storage kalic0re:effect displayCreate set value {translation:[0.0f,0.0f,0.0f],part:{scale:[1.0f,1.0f,1.0f]}}
$data modify storage kalic0re:effect displayCreate.scale set value $(scale)
function kalic0re:effect/display/create/scale {axis:"X",index:0}
function kalic0re:effect/display/create/scale {axis:"Y",index:1}
function kalic0re:effect/display/create/scale {axis:"Z",index:2}
scoreboard players operation #displayScaleLeast kalic0re.effect.tmp = #displayScaleX kalic0re.effect.tmp
scoreboard players operation #displayScaleLeast kalic0re.effect.tmp < #displayScaleY kalic0re.effect.tmp
scoreboard players operation #displayScaleLeast kalic0re.effect.tmp < #displayScaleZ kalic0re.effect.tmp
execute unless score #displayScaleLeast kalic0re.effect.tmp matches 1.. run \
    return run function kalic0re:effect/display/create/refuse
$data modify storage kalic0re:effect displayCreate.name set value "$(name)"
$data modify storage kalic0re:effect displayCreate.tags set value $(tags)
$execute positioned $(from) align xyz summon minecraft:marker run \
    function kalic0re:effect/display/create/corner {corner:"from"}
$execute positioned $(to) align xyz summon minecraft:marker run \
    function kalic0re:effect/display/create/corner {corner:"to"}
function kalic0re:effect/display/create/measure {axis:"X",key:"x",index:0}
function kalic0re:effect/display/create/measure {axis:"Y",key:"y",index:1}
function kalic0re:effect/display/create/measure {axis:"Z",key:"z",index:2}
function kalic0re:effect/display/create/root_place
scoreboard players set #displayParts kalic0re.effect.tmp 0
function kalic0re:effect/display/create/summon_root with storage kalic0re:effect displayCreate.place
data remove storage kalic0re:effect displayCreate
data remove storage kalic0re:block_state state
execute if score #displayParts kalic0re.effect.tmp matches 0 run return fail
return run scoreboard players get #displayParts kalic0re.effect.tmp
