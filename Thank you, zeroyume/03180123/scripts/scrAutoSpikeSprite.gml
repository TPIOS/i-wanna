//format
//big spike - spr  + (name see above) + Spike + direction
//e.g. ZeroYume + big spike + spike up = sprZeroYumeSpikeUp

/*
SpikeUp
SpikeRight
SpikeLeft
SpikeDown
MiniUp
MiniRight
MiniLeft
MiniDown
*/



/*
case :{sprite_index = 
;break;}
*/
/*
sprChance
sprJushi
sprQc
sprSn
sprSummer
sprSx
sprWifie
*/




switch(object_index){
    case objSpikeUp : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprSpikeUp;break;}
                        case 1:{sprite_index = sprChanceSpikeUp;break;}
            case 2:{sprite_index = sprJushiSpikeUp;break;}
            case 3:{sprite_index = sprQcSpikeUp;break;}
            case 4:{sprite_index = sprSnSpikeUp;break;}
            case 5:{sprite_index = sprSummerSpikeUp;break;}
            case 6:{sprite_index = sprSxSpikeUp;break;}
            case 7:{sprite_index = sprWifieSpikeUp;break;}
        }
        break;
    }
    case objSpikeRight : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprSpikeRight;break;}
            case 1:{sprite_index = sprChanceSpikeRight;break;}
            case 2:{sprite_index = sprJushiSpikeRight;break;}
            case 3:{sprite_index = sprQcSpikeRight;break;}
            case 4:{sprite_index = sprSnSpikeRight;break;}
            case 5:{sprite_index = sprSummerSpikeRight;break;}
            case 6:{sprite_index = sprSxSpikeRight;break;}
            case 7:{sprite_index = sprWifieSpikeRight;break;}
        }
        break;
    }
    case objSpikeLeft : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprSpikeLeft;break;}
            case 1:{sprite_index = sprChanceSpikeLeft;break;}
            case 2:{sprite_index = sprJushiSpikeLeft;break;}
            case 3:{sprite_index = sprQcSpikeLeft;break;}
            case 4:{sprite_index = sprSnSpikeLeft;break;}
            case 5:{sprite_index = sprSummerSpikeLeft;break;}
            case 6:{sprite_index = sprSxSpikeLeft;break;}
            case 7:{sprite_index = sprWifieSpikeLeft;break;}
        }
        break;
    }
    case objSpikeDown : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprSpikeDown;break;}
            case 1:{sprite_index = sprChanceSpikeDown;break;}
            case 2:{sprite_index = sprJushiSpikeDown;break;}
            case 3:{sprite_index = sprQcSpikeDown;break;}
            case 4:{sprite_index = sprSnSpikeDown;break;}
            case 5:{sprite_index = sprSummerSpikeDown;break;}
            case 6:{sprite_index = sprSxSpikeDown;break;}
            case 7:{sprite_index = sprWifieSpikeDown;break;}
        }
        break;
    }
    case objMiniUp : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprMiniUp;break;}
            case 2:{sprite_index = sprJushiMiniUp;break;}
            case 3:{sprite_index = sprQcMiniUp;break;}
            case 5:{sprite_index = sprSummerMiniUp;break;}
            case 6:{sprite_index = sprSxMiniUp;break;}
            case 7:{sprite_index = sprWifieMiniUp;break;}
        }
        break;
    }
    case objMiniRight : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprMiniRight;break;}
            case 2:{sprite_index = sprJushiMiniRight;break;}
            case 3:{sprite_index = sprQcMiniRight;break;}
            case 5:{sprite_index = sprSummerMiniRight;break;}
            case 6:{sprite_index = sprSxMiniRight;break;}
            case 7:{sprite_index = sprWifieMiniRight;break;}
        }
        break;
    }
    case objMiniLeft : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprMiniLeft;break;}
            case 2:{sprite_index = sprJushiMiniLeft;break;}
            case 3:{sprite_index = sprQcMiniLeft;break;}
            case 5:{sprite_index = sprSummerMiniLeft;break;}
            case 6:{sprite_index = sprSxMiniLeft;break;}
            case 7:{sprite_index = sprWifieMiniLeft;break;}
        }
        break;
    }
    case objMiniDown : {
        switch(scrCheckRoom()){
            case 0:{sprite_index = sprMiniDown;break;}
            case 2:{sprite_index = sprJushiMiniDown;break;}
            case 3:{sprite_index = sprQcMiniDown;break;}
            case 5:{sprite_index = sprSummerMiniDown;break;}
            case 6:{sprite_index = sprSxMiniDown;break;}
            case 7:{sprite_index = sprWifieMiniDown;break;}
        }
        break;
    }        
}
