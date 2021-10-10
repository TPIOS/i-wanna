//shoot_to,obj,spd
var aa , ax , ay , ah , av , ag , b ,dis;
if(instance_exists(argument0)){
aa=instance_nearest(x,y,argument0)
ax=aa.x
ay=aa.y
ah=aa.hspeed/(argument3+1)
av=aa.vspeed/(argument3+1)
//ag=aa.gravity/(argument3+1)
dis=point_distance(x,y,ax,ay)
if( sign(ah)=sign(x-ax) ){xx=0.85}else{xx=1.3}
if( sign(av)=sign(y-ay) ){yy=0.85}else{yy=1.3}
yyy=0.5*ag*sqr(dis/argument2)
if(yyy>100){yyy=120}
}else{exit}
b=instance_create(x,y,argument1)
b.speed=argument2
b.direction=point_direction(x,y,ax+ah*dis/argument2*xx,ay+av*dis/argument2*yy)
b.image_angle=b.direction
