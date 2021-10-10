///scrButtonCheckReleased(buttonArray)
///checks whether a button is being released this frame
///argument0 - array containing the keyboard button in index 0 and the controller button in index 1

var buttonIn;
buttonIn = argument0;

return keyboard_check_released(buttonIn[0]);

