# 1 when tmp.textCheck.rest holds no " or \, either of which would end or escape a course name a macro writes back
# into a command, nor $, which dialog templates would read as the start of $(...); a character a call.
execute if data storage kalic0re:parkour tmp.textCheck{rest:""} run return 1
data modify storage kalic0re:parkour tmp.textCheck.char set string storage kalic0re:parkour tmp.textCheck.rest 0 1
execute if data storage kalic0re:parkour tmp.textCheck{char:'"'} run return fail
execute if data storage kalic0re:parkour tmp.textCheck{char:"\\"} run return fail
execute if data storage kalic0re:parkour tmp.textCheck{char:"$"} run return fail
data modify storage kalic0re:parkour tmp.textCheck.rest set string storage kalic0re:parkour tmp.textCheck.rest 1
return run function kalic0re:parkour/admin/text/no_quote_backslash_or_dollar
