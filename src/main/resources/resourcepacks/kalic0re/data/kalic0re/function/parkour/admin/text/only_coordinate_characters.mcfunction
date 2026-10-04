# 1 when tmp.textCheck.rest holds only what coordinates are written with, digits . - ~ ^ and spaces, so it can go
# into a command and do nothing else there; a character a call.
execute if data storage kalic0re:parkour tmp.textCheck{rest:""} run return 1
data modify storage kalic0re:parkour tmp.textCheck.char set string storage kalic0re:parkour tmp.textCheck.rest 0 1
execute unless data storage kalic0re:parkour tmp.textCheck{char:"0"} unless data storage kalic0re:parkour tmp.textCheck{char:"1"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"2"} unless data storage kalic0re:parkour tmp.textCheck{char:"3"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"4"} unless data storage kalic0re:parkour tmp.textCheck{char:"5"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"6"} unless data storage kalic0re:parkour tmp.textCheck{char:"7"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"8"} unless data storage kalic0re:parkour tmp.textCheck{char:"9"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"."} unless data storage kalic0re:parkour tmp.textCheck{char:"-"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:"~"} unless data storage kalic0re:parkour tmp.textCheck{char:"^"} \
    unless data storage kalic0re:parkour tmp.textCheck{char:" "} run return fail
data modify storage kalic0re:parkour tmp.textCheck.rest set string storage kalic0re:parkour tmp.textCheck.rest 1
return run function kalic0re:parkour/admin/text/only_coordinate_characters
