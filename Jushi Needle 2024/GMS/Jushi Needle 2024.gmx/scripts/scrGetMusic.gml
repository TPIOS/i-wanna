///gets which song is supposed to be playing for the current room and plays it

var roomSong;

switch (room)                       //determines which song to play
{
    case rTitle:                    //add rooms here, if you have several rooms that play the same song they can be put together
    case rMenu:
    case rOptions:
    case rDifficultySelect:
        roomSong = musTitle;
        break;
    case rMapImport_1:
    case rMapImport_2:
    case rMapImport_3:
    case rMapImport_4:
    case rMapImport_5:
        roomSong = musStage01;
        break;
    case rMapImport_6:
    case rMapImport_7:
    case rMapImport_8:
    case rMapImport_9:
    case rMapImport_10:
        roomSong = musStage02;
        break;
    case rMapImport_11:
    case rMapImport_12:
    case rMapImport_13:
    case rMapImport_14:
    case rMapImport_15:
        roomSong = musStage03;
        break;
    case rEnd:
        roomSong = musEnd;              
        break;
    default:                        //default option in case the room does not have a song set
        roomSong = -1;
        break;
}

if (roomSong != -2)
    scrPlayMusic(roomSong,true); //play the song for the current room
