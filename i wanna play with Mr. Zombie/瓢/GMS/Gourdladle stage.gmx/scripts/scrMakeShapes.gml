///scrMakeShapes(x,y,angle,edges,numprojectiles,speed,obj)
///spawns a custom shape
///argument0 - spawn X
///argument1 - spawn Y
///argument2 - starting angle
///argument3 - number of edges (3=triangle, 4=square, etc.)
///argument4 - projectile spawns per edge
///argument5 - speed
///argument6 - projectile to spawn

var spawnX;
spawnX = argument0;
var spawnY;
spawnY = argument1;
var spawnAngle;
spawnAngle = argument2;
var spawnEdges;
spawnEdges = argument3;
var spawnNum;
spawnNum = argument4;
var spawnSpeed;
spawnSpeed = argument5;
var spawnObj;
spawnObj = argument6;
var th, xx, yy, ddx, ddy, dx, dy, a;

th = degtorad(spawnAngle);

var i;
for (i = 0; i < spawnEdges; i += 1)
{
    xx[i] = cos(th + 2*pi * i/spawnEdges);
    yy[i] = sin(th + 2*pi * i/spawnEdges);
}

xx[spawnEdges] = xx[0];
yy[spawnEdges] = yy[0];

var i;
for (i = 0; i < spawnEdges; i += 1)
{
    ddx = xx[i+1]-xx[i];
    ddy = yy[i+1]-yy[i];
    
    var j;
    for(j = 0; j < spawnNum; j += 1)
    {
        dx = xx[i]+ddx*j/spawnNum;
        dy = yy[i]+ddy*j/spawnNum;
        
        a = instance_create(spawnX+dx,spawnY+dy,spawnObj);
        a.direction = point_direction(0,0,dx,dy);
        a.speed = spawnSpeed*point_distance(0,0,dx,dy);
    }
}
