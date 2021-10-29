switch(room){
    case rTitle:
    case rMenu:
    case rOptions:
    case rDifficultySelect:
    case rHub:
        return "mainroom";
        break;
    case rEbb174_1:
    case rEbb174_2:
    case rEbb174_3:
        return "ebb174";
        break;
    case rQianchou_1:
    case rQianchou_2:
    case rQianchou_3:
        return "qianchou";
        break;
    case rWujian_1:
    case rWujian_2:
    case rWujian_3:
    case rWujian_4:
    case rWujian_5:
        return "wujian";
        break;
    case rYolomany_1:
    case rYolomany_2:
    case rYolomany_3:
    case rYolomany_4:
    case rYolomany_5:
    case rYolomanyHub:
        return "yolomany";
        break;
    case rSol_1:
    case rSol_2:
    case rSol_3:
        return "sol";
        break;
    case rJushiStage01:
    case rJushiStage02:
    case rJushiStage03:
    case rJushiStage04:
    case rJushiStage05:
    case rJushiStage06:
    case rJushiHub:
        return "jushi";
        break;
    case rRCHub:
    case rRCS1:
    case rRCS2:
    case rRCS3:
    case rRCS4:
    case rRCS5:
        return "redcrown";
        break;    
    case rQCstage01:
    case rQCstage02:
    case rQCstage04:
    case rQCstage05:
    case rQinchuiHub:
        return "qinchuinormal";
        break;
    case rQCPenalty:
    case rQCPenalty2:
    case rQCstage03:
        return "qinchuipenalty";
        break;
    case rPiaoHubIn:
        return "piao";
        break;
    default:
        return "null";
        break;
}
