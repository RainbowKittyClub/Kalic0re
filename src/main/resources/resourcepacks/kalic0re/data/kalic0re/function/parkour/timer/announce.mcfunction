# Tells the runner their time and best, with a link to the leaderboard. /trigger needs no
# permission, so the link works for anyone; the trigger's value is the course id.
function kalic0re:parkour/timer/split_best
execute store result score #ticks kalic0re.parkour.tmp run data get storage kalic0re:parkour tmp.record.ticks
function kalic0re:parkour/timer/split
# Resting at -1: see leaderboard/requested.
scoreboard players set @s kalic0re.parkour.leaderboard -1
scoreboard players enable @s kalic0re.parkour.leaderboard
$execute if score #newBest kalic0re.parkour.tmp matches 1 run tellraw @s [{text:"Finished \"$(course)\" in ",color:"gold"}, \
    {text:"",color:"yellow",extra:[ \
        {score:{name:"#tensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfMinutes",objective:"kalic0re.parkour.tmp"}},{text:":"}, \
        {score:{name:"#tensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfSeconds",objective:"kalic0re.parkour.tmp"}},{text:"."}, \
        {score:{name:"#tenthsOfSecond",objective:"kalic0re.parkour.tmp"}}]}, \
    {text:". New best! "}, \
    {text:"Click here to see the leaderboard.",underlined:true, \
        click_event:{action:"run_command",command:"/trigger kalic0re.parkour.leaderboard set $(id)"}, \
        hover_event:{action:"show_text",value:"/trigger kalic0re.parkour.leaderboard set $(id)"}}]
$execute if score #newBest kalic0re.parkour.tmp matches 0 run tellraw @s [{text:"Finished \"$(course)\" in ",color:"gold"}, \
    {text:"",color:"yellow",extra:[ \
        {score:{name:"#tensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfMinutes",objective:"kalic0re.parkour.tmp"}},{text:":"}, \
        {score:{name:"#tensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#onesOfSeconds",objective:"kalic0re.parkour.tmp"}},{text:"."}, \
        {score:{name:"#tenthsOfSecond",objective:"kalic0re.parkour.tmp"}}]}, \
    {text:". Your best is "}, \
    {text:"",color:"yellow",extra:[ \
        {score:{name:"#bestTensOfMinutes",objective:"kalic0re.parkour.tmp"}},{score:{name:"#bestOnesOfMinutes",objective:"kalic0re.parkour.tmp"}},{text:":"}, \
        {score:{name:"#bestTensOfSeconds",objective:"kalic0re.parkour.tmp"}},{score:{name:"#bestOnesOfSeconds",objective:"kalic0re.parkour.tmp"}},{text:"."}, \
        {score:{name:"#bestTenthsOfSecond",objective:"kalic0re.parkour.tmp"}}]}, \
    {text:". "}, \
    {text:"Click here to see the leaderboard.",underlined:true, \
        click_event:{action:"run_command",command:"/trigger kalic0re.parkour.leaderboard set $(id)"}, \
        hover_event:{action:"show_text",value:"/trigger kalic0re.parkour.leaderboard set $(id)"}}]
