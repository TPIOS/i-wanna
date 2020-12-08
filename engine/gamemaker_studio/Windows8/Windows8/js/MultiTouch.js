function multiTouchInitialise() {

    var canv = document.getElementById("canvas");

    // if (window.navigator.msPointerEnabled) {
    canv.addEventListener("MSPointerMove", handleTouchMove, false);
    canv.addEventListener("MSPointerDown", handleTouchDown, false);
    canv.addEventListener("MSPointerUp", handleTouchUp, false);
    canv.addEventListener("MSPointerCancel", handleTouchCancel, false);
    // }
}

function scaleTouchPosition(_evt) {

    var scaleX = 1.0;
    var scaleY = 1.0;
    if (!g_snapMaintainAspectRatio) {
        scaleX = canvas.width / window.outerWidth;
        scaleY = canvas.height / window.outerHeight;
    }

    // var dpiScale = 96.0 / Windows.Graphics.Display.DisplayProperties.logicalDpi;
    // scaleX *= dpiScale;
    // scaleY *= dpiScale;
    
    return { x: _evt.pageX * scaleX, y: _evt.pageY * scaleY };
}

function handleTouchMove(_evt) {

    switch (_evt.pointerType) {
        case _evt.MSPOINTER_TYPE_MOUSE:
            // Just allow it to pass through to onMouseDown
            break;
        default:
            var touchPos = scaleTouchPosition(_evt);
            gse("ontouchmove", _evt.pointerId, touchPos.x, touchPos.y);
            break;
    }
}

function handleTouchDown(_evt) {

    // if the appbar is not disabled then if it's a right-click 
    // from the mouse then we need to make the appbar appear
    var appBar = document.getElementById("gm4html5_app_bar");
    if (!appBar.disabled) {
        if (appBar.winControl.hidden) {
            if (_evt.pointerType == _evt.MSPOINTER_TYPE_MOUSE && _evt.button == 2) {
                // Make it sticky else ui.js spuriously kills it
                appBar.winControl.sticky = true;
                appBar.winControl.show();
            }
        }
        else {
            appBar.winControl.hide();
        }
    }

    switch (_evt.pointerType) {
        case _evt.MSPOINTER_TYPE_MOUSE:
            // Just allow it to pass through to onMouseDown
            break;
        default:
            var touchPos = scaleTouchPosition(_evt);
            gse("ontouchdown", _evt.pointerId, touchPos.x, touchPos.y);
            break;
    }
}

function handleTouchUp(_evt) {

    switch (_evt.pointerType) {
        case _evt.MSPOINTER_TYPE_MOUSE:
            // Just allow it to pass through to onMouseDown
            break;
        default:
            var touchPos = scaleTouchPosition(_evt);
            gse("ontouchup", _evt.pointerId, touchPos.x, touchPos.y);
            break;
    }
}

function handleTouchCancel(_evt) {

    switch (_evt.pointerType) {
        case _evt.MSPOINTER_TYPE_MOUSE:
            // Just allow it to pass through to onMouseDown
            break;
        default:
            var touchPos = scaleTouchPosition(_evt);
            gse("ontouchcancel", _evt.pointerId, touchPos.x, touchPos.y);
            break;
    }
}
