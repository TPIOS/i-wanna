//n_stars(n,number,speed,object,x,y,dir)
//�ڶ��ַ������ε�Ļ�Ľű�
//n: ���εĶ�����������СΪ3��3�������Σ�4��ʮ�֣�5������ǣ�6����â�ǣ�8�ǰ˽��ǣ�
//number: ����ÿ���ߵ�Ļ����
//speed: ��Ļ�ٶ�
//object: ������Ļ��obj
//x: �����x����
//y: �����Y����
//dir: ���εķ���Ƕ�
var dirm, obj, i, k;
dirm = argument6;
obj = argument3;
for (k = 0; k < argument0; k += 1)
{
    COS[k] = cos(degtorad(k*360/argument0));
    SIN[k] = sin(degtorad(k*360/argument0));
    COS[k+2] = cos(degtorad((k+2)*360/argument0));
    SIN[k+2] = sin(degtorad((k+2)*360/argument0));
    for (i = 0; i < argument1; i += 1)
    {
        idx[k,i] = instance_create(argument4,argument5,obj);
        idx[k,i].direction = dirm + point_direction(argument4,argument5,argument4+COS[k]+(COS[k+2]-COS[k])*i/argument1,argument5-SIN[k]-(SIN[k+2]-SIN[k])*i/argument1);
        idx[k,i].speed = argument2*point_distance(argument4,argument5,argument4+COS[k]+(COS[k+2]-COS[k])*i/argument1,argument5-SIN[k]-(SIN[k+2]-SIN[k])*i/argument1);
   idx[k,i].I=i
   idx[k,i].K=k
     }
}
