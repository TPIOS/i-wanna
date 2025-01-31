///scrDrawButtonInfo(optionsText)
///draws the button control info for menus
///argument0 - sets whether to draw the text for entering the options menu

var optionsText;
optionsText = argument0;

var backButton;
var acceptButton;
var optionsButton;

//check keyboard
backButton = scrGetKeybind(global.menuBackButton[0]);
acceptButton = scrGetKeybind(global.menuAcceptButton[0]);
optionsButton = scrGetKeybind(global.menuOptionsButton[0]);
    
//button info
draw_set_font(fDefault12);
draw_set_halign(fa_left);
draw_text(34,556,"["+backButton+"] Back");
draw_set_halign(fa_right);
draw_text(766,556,"["+acceptButton+"] Accept");

if (optionsText)
{
    //draw button info for the options menu
    
    draw_set_halign(fa_middle);
    draw_text(400,556,"["+optionsButton+"] Options");
}
