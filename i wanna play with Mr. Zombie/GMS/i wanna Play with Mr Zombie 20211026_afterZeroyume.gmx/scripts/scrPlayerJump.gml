if (place_meeting(x,y+(global.grav),objBlock) || place_meeting(x,y+(global.grav),objQCFreeBlock) || onPlatform || place_meeting(x,y+(global.grav),objWater))
{
    vspeed = -jump;
    djump = 1;
    audio_play_sound(sndJump,0,false);
    // ADDED: Jump spike movement
    with objJushiJumpSpike {
        event_user(0);
    }
}
else if ((djump == 1 || place_meeting(x,y+(global.grav),objWater2) || (global.infJump || global.debugInfJump)) && numJumps != 1)
{
    vspeed = -jump2;
    sprite_index = sprPlayerJump;
    audio_play_sound(sndDJump,0,false);
    
    if (!place_meeting(x,y+(global.grav),objWater3))
        djump = 0;  //take away the player's double jump
    else
        djump = 1;  //replenish djump if touching water3
    // ADDED: Jump spike movement
    with objJushiJumpSpike {
        event_user(0);
    }
}
