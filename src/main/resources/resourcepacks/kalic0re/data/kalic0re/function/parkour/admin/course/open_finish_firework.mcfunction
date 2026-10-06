# The management dialog's Edit Finish Firework: the Create Firework dialog for $(course)'s finish firework (its own over
# the pack's), Save keeping its own, Reset to Default clearing it, and Back to the management dialog.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    function kalic0re:parkour/admin/course/gone {course:"$(course)"}
$function kalic0re:parkour/admin/dialog/build_manage {course:"$(course)"}
$data modify storage kalic0re:firework editor set value \
    {save:"kalic0re:parkour/admin/course/save_finish_firework",context:"$(course)",firework:{}}
data modify storage kalic0re:firework editor.firework merge from storage kalic0re:parkour finishFirework
$data modify storage kalic0re:firework editor.firework merge from storage kalic0re:parkour courses[{name:"$(course)"}].finishFirework
function kalic0re:firework/editor/open
data remove storage kalic0re:firework editor
data modify storage kalic0re:dialog built.create_firework.title set value \
    {translate:"kalic0re.parkour.menu.finish_firework.title",bold:true}
$data modify storage kalic0re:dialog built.create_firework.body append value \
    {type:"minecraft:plain_message",contents:{translate:"kalic0re.parkour.menu.finish_firework.details",with:["$(course)"]}}
$data modify storage kalic0re:dialog built.create_firework.actions append value \
    {label:{translate:"kalic0re.parkour.menu.finish_firework.reset"},action:{type:"minecraft:run_command", \
    command:"function kalic0re:parkour/admin/course/reset_finish_firework {course:\"$(course)\"}"}}
data modify storage kalic0re:dialog built.create_firework.exit_action set value \
    {label:{translate:"gui.back"},action:{type:"minecraft:show_dialog"}}
data modify storage kalic0re:dialog built.create_firework.exit_action.action.dialog set from storage kalic0re:dialog built.manage
function kalic0re:dialog/show {name:"create_firework"}
