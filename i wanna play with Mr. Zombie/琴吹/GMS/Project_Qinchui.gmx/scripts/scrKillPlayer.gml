//kills the player

if (instance_exists(objPlayer) && (!global.noDeath && !global.debugNoDeath))
{
    if (global.gameStarted) //normal death
    {
        global.deathSound = FMODSoundPlay(sndDeath,0);
        
        if (!global.muteMusic)  //play death music
        {
            if (global.deathMusicMode == 1) //instantly pause the current music
            {
                FMODGroupSetPaused(1,1);
                
                global.gameOverMusic = FMODSoundPlay(musOnDeath,0);
            }
            else if (global.deathMusicMode == 2)    //fade out the current music
            {                
                with (objWorld)
                    event_user(0);  //fades out and stops the current music
                
                global.gameOverMusic = FMODSoundPlay(musOnDeath,0);
            }
        }
        
        with (objPlayer)
        {
            instance_create(x,y,objBloodEmitter);
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
