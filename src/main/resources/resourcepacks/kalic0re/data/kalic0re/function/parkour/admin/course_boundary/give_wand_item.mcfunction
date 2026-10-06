# give_wand's wand for $(course), with wand/lore's $(lore).
# piercing_weapon makes left-click a jab, which breaks nothing; with reach 0 it hits nobody.
$give @s minecraft:warped_fungus_on_a_stick[custom_data={kalic0re:{parkourCourseBoundaryWand:{course:"$(course)"}}}, \
    item_model="minecraft:wooden_axe", \
    item_name={translate:"kalic0re.parkour.item.course_boundary_wand"}, \
    lore=$(lore), \
    piercing_weapon={deals_knockback:false}, \
    attack_range={max_reach:0,max_creative_reach:0,hitbox_margin:0}, \
    enchantments={"kalic0re:parkour/course_boundary_wand":1},enchantment_glint_override=false, \
    tooltip_display={hidden_components:["minecraft:enchantments"]}]
