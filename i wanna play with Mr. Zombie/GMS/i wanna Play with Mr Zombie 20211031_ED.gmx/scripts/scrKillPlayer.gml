//kills the player
if(room != rEnding){
if (instance_exists(objPlayer) && (!global.noDeath && !global.debugNoDeath))
{
    if (global.gameStarted) //normal death
    {
        global.deathSound = audio_play_sound(sndDeath,0,false);
        if(room == rPiaoStage) {
            audio_play_sound(sndDeath,0,false);
            audio_stop_sound(musPiaoBGM);
        }
        if(room == rEnding) {
            audio_play_sound(sndDeath,0,false);
            audio_stop_sound(musEndingBGM);
        }
        if (!global.muteMusic)  //play death music
        {
            if (global.deathMusicMode == 1) //instantly pause the current music
            {
                audio_pause_sound(global.currentMusic);
                
                global.gameOverMusic = audio_play_sound(musOnDeath,1,false);
            }
            else if (global.deathMusicMode == 2)    //fade out the current music
            {                
                with (objWorld)
                    event_user(0);  //fades out and stops the current music
                
                global.gameOverMusic = audio_play_sound(musOnDeath,1,false);
            }
        }
        
        with (objPlayer)
        {
            /*if(room!=rRong)*/ instance_create(x,y,objBloodEmitter);
            instance_destroy();
        }
        
        instance_create(0,0,objGameOver);
        
        global.death += 1; //increment deaths
            
        scrSaveGame(false); //save death/time
    }
    else    //death in the difficulty select room, restart the room
    {
        with(objPlayer)
            instance_destroy();
            
        room_restart();
    }
}
}
if(room == rEnding and objEnding.godgod == 0){
    global.deathSound = audio_play_sound(sndDeath,0,false)
    with(objEnding){
        EndingDeath += 1
        }
    objEnding.godgod = 1
    }