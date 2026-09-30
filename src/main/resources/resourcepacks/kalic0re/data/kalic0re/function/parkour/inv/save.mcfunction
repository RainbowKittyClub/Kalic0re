# Never overwrite a saved inventory: saving again before a restore would store the parkour items over it.
$execute if data storage kalic0re:parkour players[{uuid:$(uuid)}].inventory run return 0
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].inventory set from entity @s Inventory
$data modify storage kalic0re:parkour players[{uuid:$(uuid)}].equipment set from entity @s equipment
clear @s
