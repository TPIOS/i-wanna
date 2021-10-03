///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
        roomSong = musOP;
        break;
        
    case rStage01:
    case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
        //roomSong = musGuyRock;
        roomSong = -1;
        break;                      //make sure to always put a break after setting the song
    /*
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    */
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;
    
    case rHub:
        roomSong = musHub;
        break;
          
    case rChance_1:
    case rChance_2:
    case rChance_3:
    case rChance_4:
    case rChance_5:
    case rChanceHub:
        roomSong = musChance;
        break;
    case rJushi_1:
    case rJushi_2:
    case rJushi_3:
    case rJushi_4:
    case rJushi_5:
    case rJushiHub:
        roomSong = musJushi;
        break;
    case rQc_1:
    case rQc_2:
    case rQc_3:
    case rQc_4:
    case rQc_5:
    case rQcHub:
        roomSong = musQc;
        break;
    case rSn_1:
    case rSn_2:
    case rSn_3:
    case rSn_4:
    case rSn_5:
    case rSnHub:
        roomSong = musSn;
        break;
    case rSummer_1:
    case rSummer_2:
    case rSummer_3:
    case rSummer_4:
    case rSummer_5:
    case rSummerHub:
        roomSong = musSummer;
        break;
    case rSx_1:
    case rSx_2:
    case rSx_3:
    case rSx_4:
    case rSx_5:
    case rSxHub:
        roomSong = musSx;
        break;
    case rWifie_1:
    case rWifie_2:
    case rWifie_3:
    case rWifie_4:
    case rWifie_5:
    case rWifieHub:
        roomSong = musWifie;
        break;
        
    case rEnd:
        roomSong = musED;
        break;
        
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
