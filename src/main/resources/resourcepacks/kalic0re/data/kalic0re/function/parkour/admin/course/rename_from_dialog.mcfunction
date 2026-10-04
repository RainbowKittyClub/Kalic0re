# The Rename dialog's button: rename $(course) to the texts' new_name, exactly as typed, unless it holds " or \.
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
data modify storage kalic0re:parkour tmp.textCheck set value {rest:""}
data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.new_name
execute unless function kalic0re:parkour/admin/text/no_quote_or_backslash run return run function kalic0re:parkour/admin/text/refuse_name
data remove storage kalic0re:parkour tmp.textCheck
$data modify storage kalic0re:parkour tmp.dialogTexts.course set value "$(course)"
function kalic0re:parkour/admin/course/rename with storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.dialogTexts
