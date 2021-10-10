///scrMakeCircle(x,y,angle,numprojectiles,speed,obj)
///spawns a ring of projectiles
///argument0 - spawn X
///argument1 - spawn Y
///argument2 - starting angle
///argument3 - number of projectiles to spawn
///argument4 - speed
///argument5 - projectile to spawn

var spawnX;
spawnX = argument0;
var spawnY;
spawnY = argument1;
var spawnAngle;
spawnAngle = argument2;
var spawnNum;
spawnNum = argument3;
var spawnSpeed;
spawnSpeed = argument4;
var spawnObj;
spawnObj = argument5;
var a;

var i;
for (i = 0; i < spawnNum; i += 1)
{
    a = instance_create(spawnX,spawnY,spawnObj);
    a.speed = spawnSpeed;
    a.direction = spawnAngle + i * (360 / spawnNum);
}
