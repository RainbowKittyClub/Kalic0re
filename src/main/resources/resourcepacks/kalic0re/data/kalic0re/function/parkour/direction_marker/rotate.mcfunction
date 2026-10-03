# #rotated turned by the quaternion [x,y,z,w] in tmp.directionMarkerTransformation.rotation, all in
# ten-thousandths: v + w t + q × t, where t = 2 (q × v).
execute store result score #rotationX kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.directionMarkerTransformation.rotation[0] 10000
execute store result score #rotationY kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.directionMarkerTransformation.rotation[1] 10000
execute store result score #rotationZ kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.directionMarkerTransformation.rotation[2] 10000
execute store result score #rotationW kalic0re.parkour.tmp run \
    data get storage kalic0re:parkour tmp.directionMarkerTransformation.rotation[3] 10000
# t = 2 (q × v)
scoreboard players operation #rotationTX kalic0re.parkour.tmp = #rotationY kalic0re.parkour.tmp
scoreboard players operation #rotationTX kalic0re.parkour.tmp *= #rotatedZ kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationZ kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotatedY kalic0re.parkour.tmp
scoreboard players operation #rotationTX kalic0re.parkour.tmp -= #rotationTerm kalic0re.parkour.tmp
scoreboard players operation #rotationTY kalic0re.parkour.tmp = #rotationZ kalic0re.parkour.tmp
scoreboard players operation #rotationTY kalic0re.parkour.tmp *= #rotatedX kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationX kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotatedZ kalic0re.parkour.tmp
scoreboard players operation #rotationTY kalic0re.parkour.tmp -= #rotationTerm kalic0re.parkour.tmp
scoreboard players operation #rotationTZ kalic0re.parkour.tmp = #rotationX kalic0re.parkour.tmp
scoreboard players operation #rotationTZ kalic0re.parkour.tmp *= #rotatedY kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationY kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotatedX kalic0re.parkour.tmp
scoreboard players operation #rotationTZ kalic0re.parkour.tmp -= #rotationTerm kalic0re.parkour.tmp
scoreboard players operation #rotationTX kalic0re.parkour.tmp *= #2 kalic0re.parkour.tmp
scoreboard players operation #rotationTX kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #rotationTY kalic0re.parkour.tmp *= #2 kalic0re.parkour.tmp
scoreboard players operation #rotationTY kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #rotationTZ kalic0re.parkour.tmp *= #2 kalic0re.parkour.tmp
scoreboard players operation #rotationTZ kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
# v += (w t + q × t); t is already taken from v, so each axis adds in place.
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationW kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotationTX kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationY kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTZ kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp += #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationZ kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTY kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp -= #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #rotatedX kalic0re.parkour.tmp += #rotationTerm kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationW kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotationTY kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationZ kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTX kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp += #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationX kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTZ kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp -= #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #rotatedY kalic0re.parkour.tmp += #rotationTerm kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp = #rotationW kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp *= #rotationTZ kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationX kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTY kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp += #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp = #rotationY kalic0re.parkour.tmp
scoreboard players operation #rotationCross kalic0re.parkour.tmp *= #rotationTX kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp -= #rotationCross kalic0re.parkour.tmp
scoreboard players operation #rotationTerm kalic0re.parkour.tmp /= #10000 kalic0re.parkour.tmp
scoreboard players operation #rotatedZ kalic0re.parkour.tmp += #rotationTerm kalic0re.parkour.tmp
