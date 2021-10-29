if (instance_number(objBullet) < 4 and !global.secretItem[BANSHOOT])
{
    instance_create(x,y,objBullet);
    audio_play_sound(sndShoot,0,false);
}
