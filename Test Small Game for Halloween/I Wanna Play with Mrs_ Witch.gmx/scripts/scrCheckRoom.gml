//from _1 to _6
//ZeroYume - JuShi - RongRong - WJ - QinChui - Zoulv
//7 - hub or clear room


if (room == rZeroyumeRoom_1 || room == rZeroyumeRoom_2
 || room == rZeroyumeRoom_3 || room == rZeroyumeRoom_4
 || room == rZeroyumeRoom_5 || room == rZeroyumeRoom_6
  || room == rZeroyumeRoom_7 || room == rZeroyumeRoom_8
   || room == rZeroyumeRoom_9 || room == rZeroyumeRoom_10
 ){
    return 1;
}




//jushi return 2
/*
if(room == rJushiBig or room == rJushiMath or room == rJushiW
    or room == rJushiI or room == rJushiT or room == rJushiC or room == rJushiH
    or room == rJushiHub or room == rJushiRestSnake or room == rJushiSnake0
    or room == rJushiSnake1 or room == rJushiSnake2){
    return 2;
}
*/


if( room == rJushiBig or room == rJushiW or room == rJushiI 
    or room == rJushiT or room == rJushiC or room == rJushiH){
    return 2;
}

if( room == rRongInit or room == rRong or room == rRongHub )return 3;

/*
RongRong return 3;

*/

//QinChui - Zoulv
//else 
else if(room == rWJRoom_1 || room == rWJRoom_2
 || room == rWJRoom_3 || room == rWJRoom_4
 || room == rWJRoom_5 || room == rWJHub
 ){
    return 4;
}

else if(room == rQ3 or room == rQ31 or room == room174
     or room == rQ42 or room == room175 or room == rQ52
     or room == room176 or room == rQ62 or room == rQCHub){
    return 5;
}

/*
else if(room == rQinChui_1 || room == rQinChui_2
 || room == rQinChui_3 || room == rQinChui_4
 || room == rQinChui_5 || room == rQinChui_6
 ){
    return 5;
}
*/


//QinChui - Zoulv
else if(room == rTaoshuRoom_1 || room == rTaoshuRoom_2
 || room == rTaoshuRoom_3 || room == rTaoshuRoom_4
 || room == rTaoshuRoom_5 || room == rTaoshuHub
 ){
    return 6;
}

/*
else if (room == rHub || room == rClear){
    return 7;
}
*/
else if(room == rBoss){
    return 8;
}

else return 0;

