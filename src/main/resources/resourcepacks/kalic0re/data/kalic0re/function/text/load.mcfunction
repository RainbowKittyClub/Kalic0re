scoreboard objectives add kalic0re.text.tmp dummy
# The characters each class of text may hold (text/only): coordinates, comma-separated numbers, digits, a resource id
# such as a sound's or particle's, and an id's plain part.
data modify storage kalic0re:text classes set value { \
    coordinates:{"0":1b,"1":1b,"2":1b,"3":1b,"4":1b,"5":1b,"6":1b,"7":1b,"8":1b,"9":1b,".":1b,"-":1b,"~":1b,"^":1b," ":1b}, \
    color_list:{"0":1b,"1":1b,"2":1b,"3":1b,"4":1b,"5":1b,"6":1b,"7":1b,"8":1b,"9":1b,",":1b," ":1b}, \
    digits:{"0":1b,"1":1b,"2":1b,"3":1b,"4":1b,"5":1b,"6":1b,"7":1b,"8":1b,"9":1b}, \
    identifier:{"0":1b,"1":1b,"2":1b,"3":1b,"4":1b,"5":1b,"6":1b,"7":1b,"8":1b,"9":1b,"_":1b, \
        a:1b,b:1b,c:1b,d:1b,e:1b,f:1b,g:1b,h:1b,i:1b,j:1b,k:1b,l:1b,m:1b,n:1b,o:1b,p:1b,q:1b,r:1b,s:1b,t:1b,u:1b,v:1b,w:1b, \
        x:1b,y:1b,z:1b}}
data modify storage kalic0re:text classes.resource_id set from storage kalic0re:text classes.identifier
data modify storage kalic0re:text classes.resource_id merge value {".":1b,"-":1b,"/":1b,":":1b}
