# The New Course dialog's button: create with the texts' course, the name exactly as typed, unless it holds ", \ or $.
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
data modify storage kalic0re:parkour tmp.textCheck set value {rest:""}
data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.course
execute unless function kalic0re:parkour/admin/text/no_quote_backslash_or_dollar run return run function kalic0re:parkour/admin/text/refuse_name
data remove storage kalic0re:parkour tmp.textCheck
function kalic0re:parkour/admin/create with storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.dialogTexts
