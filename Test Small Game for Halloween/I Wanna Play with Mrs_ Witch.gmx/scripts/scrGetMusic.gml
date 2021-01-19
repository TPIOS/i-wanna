///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
    case rStage01:
    case rMainHub:
    //case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
        roomSong = musMain;
        break;                      //make sure to always put a break after setting the song
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;
    case rEnd:
        //roomSong = -1;              //play nothing
        roomSong = musEnd;
        break;
    
    case rZeroyumeRoom_1:
    case rZeroyumeRoom_2:
    case rZeroyumeRoom_3:
    case rZeroyumeRoom_4:
    case rZeroyumeRoom_5:
    case rZeroyumeRoom_6:
    case rZeroyumeRoom_7:
    case rZeroyumeRoom_8:
    case rZeroyumeRoom_9:
    case rZeroyumeRoom_10:
    case rZeroyumeHub:
        roomSong = musZeroyume;
        break;
                
    case rJushiW:
    case rJushiI:
    case rJushiT:
    case rJushiC:
    case rJushiH:
        //roomSong = -1;
        roomSong = musMC;
        break;
    case rJushiBig:
        roomSong = musRyo;
        break;
    case rJushiMath:
        //roomSong = -1;
        roomSong = musThatday;
        break;
    
    case rJushiRestSnake:
    case rJushiSnake0:
        roomSong = musArcaeatitle;
        break;
    case rJushiSnake1:
        roomSong = musArcaeaworld;
        break;
    case rJushiSnake2:
        roomSong = musLucifer;
        break;
    case rJushiHub:
        roomSong = musLucifer;
        break;
    
        
    case rRong:
        roomSong = musLostColoring;
        if(global.secretItem[8]) roomSong = musBloodyTears;
        if(global.secretItem[10]) roomSong = musVampireKiller;
        break;
    case rRongHub:
        roomSong = musVampireKiller;
        break;
    case rRongInit:
        roomSong = musPrayer;
        break;
            
        
    //wujian stage
    case rWJRoom_1:
    case rWJRoom_2:
    case rWJRoom_3:
    case rWJRoom_4:
    case rWJRoom_5:
        roomSong = musWJ;
        break;
        
    //qinchui stage
    case rQ3:
    case rQ31:
    case room174:
    case rQ42:
    case room175:
    case rQ52:
    case room176:
    case rQ62:
    case rQCHub:
        roomSong = musQC;
        break;        
    
    //taoshu stage
    case rTaoshuRoom_1:
    case rTaoshuRoom_2:
    case rTaoshuRoom_3:
    case rTaoshuRoom_4:
    case rTaoshuRoom_5:
    case rTaoshuHub:
        roomSong = musTaoshu;
        break;
        
    default:                        //default option in case the room does not have a song set
        scrStopMusic();
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
