# The held wand's selection: yellow panes tiled along its 12 edges, its corner blocks framed red and
# green. A lone corner outlines its block.
execute unless data storage kalic0re:parkour tmp.wand.held.firstCorner \
    unless data storage kalic0re:parkour tmp.wand.held.secondCorner run return 0
# Only in the selection's dimension; a wand from before wands kept one shows it anywhere.
scoreboard players set #selectionInHolderDimension kalic0re.parkour.tmp 1
execute if data storage kalic0re:parkour tmp.wand.held.dim store result score #selectionInHolderDimension kalic0re.parkour.tmp run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/in_dimension with storage kalic0re:parkour tmp.wand.held
execute if score #selectionInHolderDimension kalic0re.parkour.tmp matches 0 run return 0
# Each corner falls back to the other, so one alone makes a one-block box.
data modify storage kalic0re:parkour tmp.outline.a set from storage kalic0re:parkour tmp.wand.held.secondCorner
data modify storage kalic0re:parkour tmp.outline.a set from storage kalic0re:parkour tmp.wand.held.firstCorner
data modify storage kalic0re:parkour tmp.outline.b set from storage kalic0re:parkour tmp.wand.held.firstCorner
data modify storage kalic0re:parkour tmp.outline.b set from storage kalic0re:parkour tmp.wand.held.secondCorner
execute store result score #x1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.a[0]
execute store result score #y1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.a[1]
execute store result score #z1 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.a[2]
execute store result score #x2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.b[0]
execute store result score #y2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.b[1]
execute store result score #z2 kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.outline.b[2]
# The same min and one-past-max a boundary made from these corners would have.
function kalic0re:parkour/admin/course_boundary/build_boundary
data remove storage kalic0re:parkour tmp.newCourseBoundary
scoreboard players operation #lenX kalic0re.parkour.tmp = #maxX kalic0re.parkour.tmp
scoreboard players operation #lenX kalic0re.parkour.tmp -= #minX kalic0re.parkour.tmp
scoreboard players operation #lenY kalic0re.parkour.tmp = #maxY kalic0re.parkour.tmp
scoreboard players operation #lenY kalic0re.parkour.tmp -= #minY kalic0re.parkour.tmp
scoreboard players operation #lenZ kalic0re.parkour.tmp = #maxZ kalic0re.parkour.tmp
scoreboard players operation #lenZ kalic0re.parkour.tmp -= #minZ kalic0re.parkour.tmp
# Four panes per block of the three side lengths: past 512, a stray corner would summon thousands.
scoreboard players operation #lenSum kalic0re.parkour.tmp = #lenX kalic0re.parkour.tmp
scoreboard players operation #lenSum kalic0re.parkour.tmp += #lenY kalic0re.parkour.tmp
scoreboard players operation #lenSum kalic0re.parkour.tmp += #lenZ kalic0re.parkour.tmp
execute if score #lenSum kalic0re.parkour.tmp matches 513.. run return run tellraw @s \
    {translate:"kalic0re.parkour.chat.wand_outline_too_large",color:"gray"}
data modify storage kalic0re:parkour tmp.outline.args set value {}
execute store result storage kalic0re:parkour tmp.outline.args.minX int 1 run scoreboard players get #minX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.args.minY int 1 run scoreboard players get #minY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.args.minZ int 1 run scoreboard players get #minZ kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.args.topX int 1 run scoreboard players get #maxX kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.args.topY int 1 run scoreboard players get #maxY kalic0re.parkour.tmp
execute store result storage kalic0re:parkour tmp.outline.args.topZ int 1 run scoreboard players get #maxZ kalic0re.parkour.tmp
function kalic0re:parkour/admin/course_boundary/wand/outline/edges with storage kalic0re:parkour tmp.outline.args
# The first corner framed red, the second green; one block for both gets red only, or the two would flicker.
data modify storage kalic0re:parkour tmp.outline.sameCorner set from storage kalic0re:parkour tmp.wand.held.firstCorner
execute store success score #cornersDiffer kalic0re.parkour.tmp run \
    data modify storage kalic0re:parkour tmp.outline.sameCorner set from storage kalic0re:parkour tmp.wand.held.secondCorner
execute if data storage kalic0re:parkour tmp.wand.held.firstCorner run function kalic0re:parkour/admin/course_boundary/wand/outline/corner \
    {corner:"firstCorner",pane:"red_stained_glass_pane",glow:16733525}
execute if data storage kalic0re:parkour tmp.wand.held.secondCorner if score #cornersDiffer kalic0re.parkour.tmp matches 1 run \
    function kalic0re:parkour/admin/course_boundary/wand/outline/corner {corner:"secondCorner",pane:"green_stained_glass_pane",glow:5635925}
