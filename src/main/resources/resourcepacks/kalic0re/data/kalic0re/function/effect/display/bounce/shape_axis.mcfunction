# Along $(axis): where this shape starts and ends, widening the bounce's box, or setting it if first.
$execute store result score #displayBounceShapeLow kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayBounceShape.translation[$(index)] 1024
$execute store result score #displayBounceShapeSize kalic0re.effect.tmp run \
    data get storage kalic0re:effect displayBounceShape.scale[$(index)] 1024
scoreboard players operation #displayBounceShapeHalf kalic0re.effect.tmp = #displayBounceShapeSize kalic0re.effect.tmp
scoreboard players operation #displayBounceShapeHalf kalic0re.effect.tmp *= #displayBounceCentred kalic0re.effect.tmp
scoreboard players operation #displayBounceShapeHalf kalic0re.effect.tmp /= #2 kalic0re.effect.tmp
scoreboard players operation #displayBounceShapeLow kalic0re.effect.tmp -= #displayBounceShapeHalf kalic0re.effect.tmp
scoreboard players operation #displayBounceShapeHigh kalic0re.effect.tmp = #displayBounceShapeLow kalic0re.effect.tmp
scoreboard players operation #displayBounceShapeHigh kalic0re.effect.tmp += #displayBounceShapeSize kalic0re.effect.tmp
$execute if score #displayBounceShapes kalic0re.effect.tmp matches 0 run \
    scoreboard players operation #displayBounceLow$(axis) kalic0re.effect.tmp = #displayBounceShapeLow kalic0re.effect.tmp
$execute if score #displayBounceShapes kalic0re.effect.tmp matches 0 run \
    scoreboard players operation #displayBounceHigh$(axis) kalic0re.effect.tmp = #displayBounceShapeHigh kalic0re.effect.tmp
$scoreboard players operation #displayBounceLow$(axis) kalic0re.effect.tmp < #displayBounceShapeLow kalic0re.effect.tmp
$scoreboard players operation #displayBounceHigh$(axis) kalic0re.effect.tmp > #displayBounceShapeHigh kalic0re.effect.tmp
