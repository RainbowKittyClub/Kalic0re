# /function kalic0re:effect/display/create_from_blocks {from:"<x y z>",to:"<x y z>",name:"<name>",tags:[<tag>,…]}
# e.g. {from:"10 64 10",to:"12 66 10",name:"Gate",tags:["gate"]}, or name:"" and tags:[] for neither.
# Makes one display of the blocks in the cuboid from–to, leaving the blocks as they are: an invisible
# root display, named and tagged so, at the centre of the cuboid's top layer, with a block display
# riding it for each block. Coordinates read as in a /clone of your own. Every part takes the light at
# the root, so a solid block left there draws the display dark. Returns how many blocks it shows, and
# fails, making nothing, on none. A block vanilla lacks gets no part. Each block takes about 40 commands
# of max_command_sequence_length, 65536 by default: some 1600 a call.
# To clear the blocks after, fill them strict: a plain fill drops a lantern or torch as an item when it
# removes what holds it up first. To remove a display: execute as <display> on passengers run kill @s,
# then kill the display.
data modify storage kalic0re:effect displayCreate set value {translation:[0.0f,0.0f,0.0f]}
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
