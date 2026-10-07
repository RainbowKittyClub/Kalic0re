# Smooth: progress p eased in and out as 3p² − 2p³, in thousandths, each product scaled down before the
# next so none passes a score's range.
scoreboard players operation #displayMoveEase kalic0re.effect.tmp = #displayMoveProgress kalic0re.effect.tmp
scoreboard players operation #displayMoveEase kalic0re.effect.tmp *= #displayMoveProgress kalic0re.effect.tmp
scoreboard players operation #displayMoveEase kalic0re.effect.tmp /= #1000 kalic0re.effect.tmp
scoreboard players set #displayMoveEaseFactor kalic0re.effect.tmp 3000
scoreboard players operation #displayMoveEaseFactor kalic0re.effect.tmp -= #displayMoveProgress kalic0re.effect.tmp
scoreboard players operation #displayMoveEaseFactor kalic0re.effect.tmp -= #displayMoveProgress kalic0re.effect.tmp
scoreboard players operation #displayMoveEase kalic0re.effect.tmp *= #displayMoveEaseFactor kalic0re.effect.tmp
scoreboard players operation #displayMoveEase kalic0re.effect.tmp /= #1000 kalic0re.effect.tmp
scoreboard players operation #displayMoveProgress kalic0re.effect.tmp = #displayMoveEase kalic0re.effect.tmp
