var win8_ad_data = null;

// #############################################################################################
/// Function:<summary>
///          	These have to match with the ordering in GMùs GGS
///          </summary>
// #############################################################################################
function get_ad_size(_sizeIndex) {

    var sizeIndex = _sizeIndex;
    if (typeof (sizeIndex) != "number") {
        sizeIndex = parseInt(sizeIndex);
    }

    var width = 0;
    var height = 0;
    switch (sizeIndex) {
        case 0: 
        case 1: 
        case 2:
            width = 160; height = 600;
            break;
        case 3:
            width = 250; height = 125;
            break;
        case 4:
        case 5:
        case 6:
        case 7:
            width = 250; height = 250;
            break;
        case 8:
            width = 292; height = 60;
            break;
        case 9:
        case 10:
        case 11:
        case 12:
            width = 300; height = 250;
            break;
        case 13:
            width = 500; height = 130;
            break;
        case 14:
        case 15:
        case 16:
            width = 728; height = 90;
            break;
    }
    return { w : width, h : height };
}

// #############################################################################################
/// Function:<summary>
///          	Enable ad
///          </summary>
// #############################################################################################
function win8_ads_enable(x, y, num) {

    if (win8_ad_data == null) {
        return;
    }
    var adData = win8_ad_data(num);
    if ((adData != null) && (adData != undefined)) {
        var adSize = get_ad_size(adData.size);

        var e = document.createElement('div');        
        e.setAttribute('id', 'gmlad' + num);
        e.style.position = "absolute";
        e.style.top = y + "px";
        e.style.left = x + "px";
        e.style.width = adSize.w + "px";
        e.style.height = adSize.h + "px";
        e.style.zIndex = "1";
        
        var winCtrl = new MicrosoftNSJS.Advertising.AdControl(e, { applicationId: adData.appId, adUnitId: adData.unitId });

        // If the user is backing out from the ad then make sure the focus is correctly set
        winCtrl.onEngagedChanged = function (sender, evt) {            
            if (!sender.isEngaged) {                
                window.focus(); // without we won't get blur messages when leaving the app (why?????)
                canvas.focus(); // without we won't get immediate input focus on the canvas
            }            
        };
        document.body.appendChild(e);
    }
}

// #############################################################################################
/// Function:<summary>
///          	Disable ad
///          </summary>
// #############################################################################################
function win8_ads_disable(num) {
    
    var e = document.getElementById('gmlad' + num);
    if (e) {
        document.body.removeChild(e);
    }    
}

// #############################################################################################
/// Function:<summary>
///          	Move ad location
///          </summary>
// #############################################################################################
function win8_ads_move(x, y, num) {

    var e = document.getElementById('gmlad' + num);
    if (e) {
        e.style.left = x + "px";
        e.style.top = y + "px";        
    }
}