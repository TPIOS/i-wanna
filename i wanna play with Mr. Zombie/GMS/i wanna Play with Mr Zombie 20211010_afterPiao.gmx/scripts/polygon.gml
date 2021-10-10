//polygon(n,number,speed,object,x,y,dir)
var dirm, obj, i, k;
dirm = argument6;
obj = argument3;
for (k = 0; k < argument0; k += 1)
{
    COS[k] = cos(degtorad(k*360/argument0));
    SIN[k] = sin(degtorad(k*360/argument0));
    COS[k+1] = cos(degtorad((k+1)*360/argument0));
    SIN[k+1] = sin(degtorad((k+1)*360/argument0));
    for (i = 0; i < argument1; i += 1)
    {
        idx[k,i] = instance_create(argument4,argument5,obj);
        idx[k,i].direction = dirm + point_direction(argument4,argument5,argument4+COS[k]+(COS[k+1]-COS[k])*i/argument1,argument5-SIN[k]-(SIN[k+1]-SIN[k])*i/argument1);
        idx[k,i].gravity_direction=180+direction 
        idx[k,i].speed = argument2*point_distance(argument4,argument5,argument4+COS[k]+(COS[k+1]-COS[k])*i/argument1,argument5-SIN[k]-(SIN[k+1]-SIN[k])*i/argument1);
        //idx[k,i].kind=argument7
      idx[k,i].I=i
   idx[k,i].K=k
    }
}
