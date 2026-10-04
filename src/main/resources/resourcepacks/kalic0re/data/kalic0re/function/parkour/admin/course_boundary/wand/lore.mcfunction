# The wand's lore for course $(course), at tmp.wandLore.lore: give_wand gives it, and refresh_lore puts it on a
# wand held since, so one given before the wand changed catches up.
$data modify storage kalic0re:parkour tmp.wandLore set value {lore:[ \
    {translate:"kalic0re.parkour.item.course_boundary_wand.course",with:["$(course)"],color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.left",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.right",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.display",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.up",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.down",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.sneak_left",color:"gray",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.sneak_right",color:"gray",italic:false}]}
$data modify storage kalic0re:parkour tmp.wandLore.course set value "$(course)"
