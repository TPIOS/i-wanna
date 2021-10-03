if(global.secretItem[BOMB] and releasable){
    if(keyboard_check_pressed(ord('Z'))){
        instance_create(x,y,objBomb);
        releasable = false;
        bombcd = 100;
    }
}
if (!releasable){
    bombcd -= 1;
}
if (bombcd<=0) releasable=true;
