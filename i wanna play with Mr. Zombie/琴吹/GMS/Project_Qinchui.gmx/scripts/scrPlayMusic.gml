///scrPlayMusic(soundid,loops)
///plays a song if it's not already playing
///argument0 - song to play (-1 plays nothing and stops anything currently playing)
///argument1 - whether or not to loop the song

var songID;
songID = argument0;
var loopSong;
loopSong = argument1;

if (!global.muteMusic)  //check if music is supposed to be muted
{
    if (global.currentMusicID != songID)  //checks if the song to play is already playing
    {
        global.currentMusicID = songID;
        
        FMODSoundSetGroup(global.currentMusicID,1);
        
        FMODGroupStop(1);
        
        if (songID != -1)
        {    
            if(loopSong)
            {
                global.currentMusic = FMODSoundLoop(global.currentMusicID,0);
            }
            else
            {
                global.currentMusic = FMODSoundPlay(global.currentMusicID,0)
            }
        }
    }
}
