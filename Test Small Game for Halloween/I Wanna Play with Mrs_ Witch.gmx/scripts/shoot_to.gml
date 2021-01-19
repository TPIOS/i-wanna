//dir0,num,dir,spd,ins
var dir,spd,a;
dir=argument0-(argument1-1)*argument2/2
if(argument0=-1){dir=point_direction(x,y,plx,ply)-(argument1-1)*argument2/2}
spd=argument3
repeat(argument1){a=instance_create(x,y,argument4)
    a.direction=dir
    a.image_angle+=dir
    a.speed=spd
    dir+=argument2
}
