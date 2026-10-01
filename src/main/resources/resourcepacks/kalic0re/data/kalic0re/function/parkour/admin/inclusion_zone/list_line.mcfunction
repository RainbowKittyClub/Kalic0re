$tellraw @s [{translate:"kalic0re.parkour.chat.inclusion_zone_line",fallback:"%s: from %s to %s in %s.",with:["$(zone)", \
        {nbt:"tmp.inclusionZoneList.zones[0].min[]",storage:"kalic0re:parkour",separator:" "}, \
        {nbt:"tmp.inclusionZoneList.zones[0].max[]",storage:"kalic0re:parkour",separator:" "}, \
        "$(dim)"]},{text:" "}, \
    {translate:"kalic0re.parkour.chat.remove_it",fallback:"Click here to remove it.",underlined:true, \
        click_event:{action:"run_command",command:"/function kalic0re:parkour/admin/inclusion_zone/remove {course:\"$(course)\",zone:$(zone)}"}, \
        hover_event:{action:"show_text",value:"/function kalic0re:parkour/admin/inclusion_zone/remove {course:\"$(course)\",zone:$(zone)}"}}]
