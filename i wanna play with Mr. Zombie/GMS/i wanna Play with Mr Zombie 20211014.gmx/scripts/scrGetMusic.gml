///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
    case rHub:
    //case rStage01:
    //case rStage02:                //this room has a play music object in it so it doesnt need to be included in this script
        roomSong = musHub;
        break;                      //make sure to always put a break after setting the song
    case rCherryBoss:
        roomSong = musMegaman;
        break;
    case rMiku:
        roomSong = -2;              //don't change the music in any way (the Miku object plays it)
        break;
    case rEbb174_1:
    case rEbb174_2:
    case rEbb174_3:
        roomSong = musEbb174;
        break;
    case rYolomany_1:
    case rYolomany_2:
    case rYolomany_3:
    case rYolomany_4:
    case rYolomany_5:
    case rYolomanyHub:
        roomSong = musYolomany;
        break;
    case rQCPenalty:
    case rQCPenalty2:
    case rQCstage03:
        roomSong = musQCpenaltyBgm;
        break;
    case rQCstage01:
    case rQCstage02:
    case rQCstage04:
    case rQCstage05:
    case rQinchuiHub:
        roomSong = musQCstageBgm;
        break;
    case rSol_1:
    case rSol_2:
    case rSol_3:
        roomSong = musSolBGM;
        break;
    case rQianchou_1:
    case rQianchou_2:
    case rQianchou_3:
        roomSong = musQianchou;
        break;
    case rWujian_1:
    case rWujian_2:
    case rWujian_3:
    case rWujian_4:
    case rWujian_5:
    case rNeedleRushHub:
        roomSong = musWujian;
        break;
    case rJushiStage01:
    case rJushiStage02:
    case rJushiStage03:
    case rJushiStage04:
    case rJushiStage05:
    case rJushiStage06:
    case rJushiHub:
        roomSong = musJushiStage;
        break;
    case rRCHub:
        roomSong = musRCBgm;
        break;
    case rEnd:
        roomSong = -1;              //play nothing
        break;
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
