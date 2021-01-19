var a,i,dir;

//splitobject(splitnumber,speed,createobject,toplayer(1) or randomdirection(0),selfdestroy or not)
//number,speed,obj,direction,destroy

if(argument3=-1){
    dir=point_direction(x,y,plx,ply)
}else if(argument3=-2){
    dir=random(360)
}else{dir=argument3}


for(i=1;i<=argument0;i+=1;){
a=instance_create(x,y,argument2)
a.speed=argument1
a.direction=dir
if(argument5)a.sprite_index = argument5;
a.image_angle=dir
dir+=360/argument0
}

if(argument4)instance_destroy();
