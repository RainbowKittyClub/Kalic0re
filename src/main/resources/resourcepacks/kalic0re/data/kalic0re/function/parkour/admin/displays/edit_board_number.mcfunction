# For edit_board_read: the title's $(from) as text at tmp.leaderboardDisplayPlace.$(into) (admin/text/number).
data modify storage kalic0re:parkour tmp.numberText set value {}
$data modify storage kalic0re:parkour tmp.numberText.value set from entity @s $(from)
function kalic0re:parkour/admin/text/number
$data modify storage kalic0re:parkour tmp.leaderboardDisplayPlace.$(into) set from storage kalic0re:parkour tmp.numberText.text
data remove storage kalic0re:parkour tmp.numberText
