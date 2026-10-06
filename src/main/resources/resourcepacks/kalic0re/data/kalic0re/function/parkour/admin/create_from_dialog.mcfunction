# The New Course dialog's Confirm: the texts' course (no ", \ or $) with its start and lobby, both required and read
# as places (~ from you) before anything is made. A refusal opens the dialog again holding what was typed.
$data modify storage kalic0re:parkour tmp.dialogTexts set value $(texts)
data modify storage kalic0re:parkour tmp.textCheck set value {rest:""}
data modify storage kalic0re:parkour tmp.textCheck.rest set from storage kalic0re:parkour tmp.dialogTexts.course
execute unless function kalic0re:parkour/admin/text/no_quote_backslash_or_dollar run return run \
    function kalic0re:parkour/admin/refuse_new_course {refusal:"kalic0re:parkour/admin/text/refuse_name"}
data remove storage kalic0re:parkour tmp.textCheck
execute if data storage kalic0re:parkour tmp.dialogTexts{startPosition:""} run return run \
    function kalic0re:parkour/admin/refuse_new_course {refusal:"kalic0re:parkour/admin/text/refuse_blank_position"}
execute if data storage kalic0re:parkour tmp.dialogTexts{lobbyPosition:""} run return run \
    function kalic0re:parkour/admin/refuse_new_course {refusal:"kalic0re:parkour/admin/text/refuse_blank_position"}
data modify storage kalic0re:parkour tmp.dialogTexts.position set from storage kalic0re:parkour tmp.dialogTexts.startPosition
data modify storage kalic0re:parkour tmp.dialogTexts.rotation set from storage kalic0re:parkour tmp.dialogTexts.startRotation
execute unless function kalic0re:parkour/admin/text/place_readable run return run \
    function kalic0re:parkour/admin/refuse_new_course {refusal:"kalic0re:parkour/admin/text/refuse_place"}
data modify storage kalic0re:parkour tmp.dialogTexts.position set from storage kalic0re:parkour tmp.dialogTexts.lobbyPosition
data modify storage kalic0re:parkour tmp.dialogTexts.rotation set from storage kalic0re:parkour tmp.dialogTexts.lobbyRotation
execute unless function kalic0re:parkour/admin/text/place_readable run return run \
    function kalic0re:parkour/admin/refuse_new_course {refusal:"kalic0re:parkour/admin/text/refuse_place"}
function kalic0re:parkour/admin/create_at with storage kalic0re:parkour tmp.dialogTexts
data remove storage kalic0re:parkour tmp.dialogTexts
