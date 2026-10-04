# The wand's lore for course $(course), at tmp.wandLore.lore: give_wand gives it, and refresh_lore puts it on a
# wand held since, so one given before the wand changed catches up. Gray unless a part says otherwise.
$data modify storage kalic0re:parkour tmp.wandLore set value {lore:[ \
    {translate:"kalic0re.parkour.item.course_boundary_wand.course",color:"gray",italic:false,with:[ \
        {translate:"kalic0re.parkour.item.course_boundary_wand.course_label",color:"yellow"}, \
        {text:"$(course)",color:"white",bold:true}]}, \
    {text:""}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.select",color:"white",italic:false}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.up",color:"gray",italic:false,with:[ \
        {translate:"kalic0re.parkour.item.course_boundary_wand.up_word",color:"aqua"}, \
        {translate:"kalic0re.parkour.item.course_boundary_wand.add_word",color:"green"}]}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.down",color:"gray",italic:false,with:[ \
        {translate:"kalic0re.parkour.item.course_boundary_wand.down_word",color:"#F9801D"}, \
        {translate:"kalic0re.parkour.item.course_boundary_wand.remove_word",color:"red"}]}, \
    {text:""}, \
    {translate:"kalic0re.parkour.item.course_boundary_wand.display",color:"gray",italic:false}]}
$data modify storage kalic0re:parkour tmp.wandLore.course set value "$(course)"
