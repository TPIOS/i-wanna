if (instance_number(objBullet) < 4)
{
    instance_create(x,y,objBullet);
    FMODSoundPlay(sndShoot,0);
}
