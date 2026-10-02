# /function kalic0re:parkour/admin/course_boundary/give_wand {course:"<name>"}
# piercing_weapon makes left-click a jab, which breaks nothing; with reach 0 it hits nobody.
$execute unless data storage kalic0re:parkour courses[{name:"$(course)"}] run return run \
    tellraw @s {translate:"kalic0re.parkour.chat.no_such_course",fallback:"There is no parkour course named \"%s\".",with:["$(course)"],color:"red"}
$give @s minecraft:warped_fungus_on_a_stick[custom_data={kalic0re:{parkourCourseBoundaryWand:{course:"$(course)"}}}, \
    item_model="minecraft:wooden_axe", \
    item_name={translate:"kalic0re.parkour.item.course_boundary_wand",fallback:"Parkour Boundary Wand"}, \
    lore=[{translate:"kalic0re.parkour.item.course_boundary_wand.course",fallback:"Course: %s",with:["$(course)"],color:"gray",italic:false}, \
        {translate:"kalic0re.parkour.item.course_boundary_wand.left",fallback:"Left-click a block: first corner",color:"gray",italic:false}, \
        {translate:"kalic0re.parkour.item.course_boundary_wand.right",fallback:"Right-click a block: second corner",color:"gray",italic:false}, \
        {translate:"kalic0re.parkour.item.course_boundary_wand.sneak",fallback:"Sneak while clicking: exclusion boundary",color:"gray",italic:false}], \
    piercing_weapon={deals_knockback:false}, \
    attack_range={max_reach:0,max_creative_reach:0,hitbox_margin:0}, \
    enchantments={"kalic0re:parkour/course_boundary_wand":1},enchantment_glint_override=false, \
    tooltip_display={hidden_components:["minecraft:enchantments"]}]
