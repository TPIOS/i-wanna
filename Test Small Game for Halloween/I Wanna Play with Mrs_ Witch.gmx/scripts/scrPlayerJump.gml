//hallo only
if(frozen2)exit;
/*
var e;

if (place_meeting(x,y+(global.grav),objBlock) || onPlatform || place_meeting(x,y+(global.grav),objWater))
{
    vspeed = -jump;
    djump = 1;
    audio_play_sound(sndJump,0,false);
}

else{

    //if touch objWater2 double jump
    //if global.infJump double jump
    //if global.debugInfJump double jump
    
    //if touch objWater3 gain another jump

    if(djump > 0 or (global.infJump || global.debugInfJump) || place_meeting(x,y+global.grav,objWater2)){
        vspeed = -jump2;
        sprite_index = sprPlayerJump;
        audio_play_sound(sndDJump,0,false);
        
        if(!place_meeting(x,y+global.grav,objWater3))djump = 0;
        else djump = 1;
    }
    else{
        if global.triple and djump == 0{
            djump = -1;
            vspeed = -jump2;
            sprite_index = sprPlayerJump;
            audio_play_sound(sndDJump,0,false);
            
            e = part_emitter_create(global.ps);
            part_emitter_region(global.ps,e,x-5,x+5,y+4,y+4,ps_shape_rectangle,ps_distr_linear);
            part_emitter_burst(global.ps,e,global.partTriple,5);
            part_emitter_destroy(global.ps,e);
            
        }
    }


}
*/

if (place_meeting(x,y+(global.grav),objBlock) || onPlatform || place_meeting(x,y+(global.grav),objWater))
{
    vspeed = -jump;
    djump = 1;
    audio_play_sound(sndJump,0,false);
}
else if(global.secretItem[10]){
    if(place_meeting(x,y+(global.grav),objWater2)){
        vspeed = -jump2;
        sprite_index = sprPlayerJump;
        audio_play_sound(sndDJump,0,false);
    }
    
    
    
    
    if (!place_meeting(x,y+(global.grav),objWater3))
        djump = 0;  //take away the player's double jump
    else
        djump = 1;  //replenish djump if touching water3
}
//else if (djump == 1 || place_meeting(x,y+(global.grav),objWater2) || (global.infJump || global.debugInfJump))
else if (
    (djump == 1 || place_meeting(x,y+(global.grav),objWater2) || (global.infJump || global.debugInfJump)))
{
    vspeed = -jump2;
    sprite_index = sprPlayerJump;
    audio_play_sound(sndDJump,0,false);
    
    if (!place_meeting(x,y+(global.grav),objWater3))
        djump = 0;  //take away the player's double jump
    else
        djump = 1;  //replenish djump if touching water3
}

//if(global.secretItem[10]) djump=0
