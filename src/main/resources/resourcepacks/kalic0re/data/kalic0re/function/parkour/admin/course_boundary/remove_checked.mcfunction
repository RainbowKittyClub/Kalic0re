# The Remove Boundaries dialog's Remove: $(checks) is 1 or 0 for each choice of the question kept under this
# player's UUID, in order; each checked one is found again by its term and removed, as an answer to ask_which.
$data modify storage kalic0re:parkour tmp.boundaryChecks set value {checks:$(checks)}
data modify storage kalic0re:parkour tmp.boundaryChecks.uuid set from entity @s UUID
scoreboard players set #boundaryCheckIndex kalic0re.parkour.tmp 0
scoreboard players set #boundariesChecked kalic0re.parkour.tmp 0
function kalic0re:parkour/admin/course_boundary/remove_checked_next
execute if score #boundariesChecked kalic0re.parkour.tmp matches 0 run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_boundaries_checked",color:"gray"}
data remove storage kalic0re:parkour tmp.boundaryChecks
