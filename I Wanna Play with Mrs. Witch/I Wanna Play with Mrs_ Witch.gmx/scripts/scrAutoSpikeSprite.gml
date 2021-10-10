//from _1 to _6
//Zeroyume - JuShi - RongRong - WuJian - QinChui - Zoulv

//format
//big spike - spr  + (name see above) + Spike + direction
//e.g. Zeroyume + big spike + spike up = sprZeroyumeSpikeUp

/*
SpikeUp
SpikeLeft
SpikeRight
SpikeDown
MiniUp
MiniLeft
MiniRight
MiniDown
*/





switch(object_index){
    case objSpikeUp : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeSpikeUp; break;}
            case 2:{sprite_index = sprJushiSpikeUp;break;}
            case 3: {sprite_index = sprRongSpikeUp;break;}
            case 4:{sprite_index = sprWJSpikeUp;break;}
            case 5:{sprite_index = sprQCSpikeUp;break;}
            case 6:{sprite_index = sprZoulvSpikeUp;break;}
            default:break;
        }
        break;
    }
    case objSpikeRight : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeSpikeRight;break;}
            case 2:{sprite_index = sprJushiSpikeRight;break;}
            case 3: {sprite_index = sprRongSpikeRight;break;}
            case 4:{sprite_index = sprWJSpikeRight;break;}
            case 5:{sprite_index = sprQCSpikeRight;break;}
            case 6:{sprite_index = sprZoulvSpikeRight;break;}
            default:break;
        }
        break;
    }
    case objSpikeLeft : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeSpikeLeft;break;}
            case 2:{sprite_index = sprJushiSpikeLeft;break;}
            case 3: {sprite_index = sprRongSpikeLeft;break;}
            case 4:{sprite_index = sprWJSpikeLeft;break;}
            case 5:{sprite_index = sprQCSpikeLeft;break;}
            case 6:{sprite_index = sprZoulvSpikeLeft;break;}
            default:break;
        }
        break;
    }
    case objSpikeDown : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeSpikeDown;break;}
            case 2:{sprite_index = sprJushiSpikeDown;break;}
            case 3: {sprite_index = sprRongSpikeDown;break;}
            case 4:{sprite_index = sprWJSpikeDown;break;}
            case 5:{sprite_index = sprQCSpikeDown;break;}
            case 6:{sprite_index = sprZoulvSpikeDown;break;}
            default:break;
        }
        break;
    }
    case objMiniUp : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeMiniUp;break;}
            case 2:{sprite_index = sprJushiMiniUp;break;}
            case 3: {sprite_index = sprRongMiniUp;break;}
            case 4:{sprite_index = sprWJMiniUp;break;}
            case 6:{sprite_index = sprZoulvMiniUp;break;}

            default:break;
        }
        break;
    }
    case objMiniRight : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeMiniRight;break;}
            case 2:{sprite_index = sprJushiMiniRight;break;}
            case 3: {sprite_index = sprRongMiniRight;break;}            
            case 4:{sprite_index = sprWJMiniRight;break;}
            case 6:{sprite_index = sprZoulvMiniRight;break;}
            default:break;
        }
        break;
    }
    case objMiniLeft : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeMiniLeft;break;}
            case 2:{sprite_index = sprJushiMiniLeft;break;}
            case 3: {sprite_index = sprRongMiniLeft;break;}
            case 4:{sprite_index = sprWJMiniLeft;break;}
            case 6:{sprite_index = sprZoulvMiniLeft;break;}
            default:break;
        }
        break;
    }
    case objMiniDown : {
        switch(scrCheckRoom()){
            case 1:{sprite_index = sprZeroyumeMiniDown;break;}
            case 2:{sprite_index = sprJushiMiniDown;break;}
            case 3: {sprite_index = sprRongMiniDown;break;}
            case 4:{sprite_index = sprWJMiniDown;break;}
            case 6:{sprite_index = sprZoulvMiniDown;break;}
            default:break;
        }
        break;
    }        
}

