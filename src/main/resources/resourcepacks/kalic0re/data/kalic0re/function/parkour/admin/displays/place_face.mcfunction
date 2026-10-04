# For place_defaults: the face the look entered, the side of the block the step before lies furthest out of (1 west,
# 2 east, 3 below, 4 above, 5 north, 6 south), then Position 0.1 out from its middle and Rotation facing out of it.
function kalic0re:parkour/admin/displays/place_out {axis:"X"}
function kalic0re:parkour/admin/displays/place_out {axis:"Y"}
function kalic0re:parkour/admin/displays/place_out {axis:"Z"}
scoreboard players set #placeFace kalic0re.parkour.tmp 0
scoreboard players set #placeBest kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/displays/place_try {face:1,out:"LowX"}
function kalic0re:parkour/admin/displays/place_try {face:2,out:"HighX"}
function kalic0re:parkour/admin/displays/place_try {face:3,out:"LowY"}
function kalic0re:parkour/admin/displays/place_try {face:4,out:"HighY"}
function kalic0re:parkour/admin/displays/place_try {face:5,out:"LowZ"}
function kalic0re:parkour/admin/displays/place_try {face:6,out:"HighZ"}
execute if score #placeFace kalic0re.parkour.tmp matches 0 run return 0
function kalic0re:parkour/admin/displays/place_middle {axis:"X",low:1,high:2}
function kalic0re:parkour/admin/displays/place_middle {axis:"Y",low:3,high:4}
function kalic0re:parkour/admin/displays/place_middle {axis:"Z",low:5,high:6}
data modify storage kalic0re:parkour tmp.placeText set value {}
function kalic0re:parkour/admin/displays/place_tenths {axis:"X"}
function kalic0re:parkour/admin/displays/place_tenths {axis:"Y"}
function kalic0re:parkour/admin/displays/place_tenths {axis:"Z"}
function kalic0re:parkour/admin/displays/place_text_into_args with storage kalic0re:parkour tmp.placeText
data remove storage kalic0re:parkour tmp.placeText
execute if score #placeFace kalic0re.parkour.tmp matches 1 run data modify storage kalic0re:dialog args.place_rotation set value "90.0 0.0"
execute if score #placeFace kalic0re.parkour.tmp matches 2 run data modify storage kalic0re:dialog args.place_rotation set value "-90.0 0.0"
execute if score #placeFace kalic0re.parkour.tmp matches 5 run data modify storage kalic0re:dialog args.place_rotation set value "180.0 0.0"
execute if score #placeFace kalic0re.parkour.tmp matches 6 run data modify storage kalic0re:dialog args.place_rotation set value "0.0 0.0"
execute if score #placeFace kalic0re.parkour.tmp matches 3..4 run function kalic0re:parkour/admin/displays/place_flat
