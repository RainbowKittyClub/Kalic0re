execute unless entity @s[tag=kalic0re.parkour.receivedParkourItems] run \
    function kalic0re:parkour/player/with_uuid {run:"kalic0re:parkour/player/enter"}
# enter refuses a run with no course and clears isActive.
execute unless score @s kalic0re.parkour.isActive matches 1 run return 0
function kalic0re:parkour/player/refill
execute if score @s kalic0re.parkour.click matches 1.. run function kalic0re:parkour/player/click
# Parkour items only exist on active players, so their drops are always near one.
execute as @e[type=item,distance=..8] if items entity @s contents *[custom_data~{kalic0re:{parkour:{}}}] run kill @s
