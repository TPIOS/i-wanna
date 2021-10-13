//heart(x,y,obj,n,dir,speed)
dire=0

k=1
repeat(argument3){
x2 = argument0+(16*sin(degtorad(dire))*sin(degtorad(dire))*sin(degtorad(dire)))
y2 = argument1+(-13*cos(degtorad(dire))+5*cos(degtorad(2*dire))+2*cos(degtorad(3*dire))+cos(degtorad(4*dire)))

i[k]= instance_create(argument0,argument1,argument2)
i[k].direction = point_direction(argument0,argument1,x2,y2)+argument4
i[k].speed = point_distance(x2,y2,argument0,argument1)/40*argument5
dire+=360/argument3
i[k].I=k
k+=1

}

