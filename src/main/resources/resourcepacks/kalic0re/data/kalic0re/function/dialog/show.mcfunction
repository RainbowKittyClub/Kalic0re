# /function kalic0re:dialog/show {name:"<name>"} shows @s the dialog built from <name>.json, then
# clears what building it left in storage kalic0re:dialog.
$data modify storage kalic0re:dialog show.dialog set from storage kalic0re:dialog built.$(name)
function kalic0re:dialog/show_stored with storage kalic0re:dialog show
data remove storage kalic0re:dialog built
data remove storage kalic0re:dialog args
data remove storage kalic0re:dialog show
