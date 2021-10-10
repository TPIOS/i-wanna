if (place_meeting(x,y+(global.grav),objBlock) || place_meeting(x,y+(global.grav),objQCFreeBlock) ||onPlatform || place_meeting(x,y+(global.grav),objWater))
{
    vspeed = -jump;
    djump = 1;
    FMODSoundPlay(sndJump,0);
}
else if (djump == 1 || place_meeting(x,y+(global.grav),objWater2) || (global.infJump || global.debugInfJump))
{
    vspeed = -jump2;
    sprite_index = sprPlayerJump;
    FMODSoundPlay(sndDJump,0);
    
    if (!place_meeting(x,y+(global.grav),objWater3))
        djump = 0;  //take away the player's double jump
    else
        djump = 1;  //replenish djump if touching water3
}
