# As a bouncing display, at it: a tick's travel along each axis, then the teleport there. The wall count
# holds from one step to the next, so command blocks later this tick and tick functions early next see it.
scoreboard players set @s kalic0re.effect.displayBounceWallHits 0
function kalic0re:effect/display/bounce/axis {axis:"X",key:"x"}
function kalic0re:effect/display/bounce/axis {axis:"Y",key:"y"}
function kalic0re:effect/display/bounce/axis {axis:"Z",key:"z"}
function kalic0re:effect/display/move/teleport with storage kalic0re:effect displayBounceStep
