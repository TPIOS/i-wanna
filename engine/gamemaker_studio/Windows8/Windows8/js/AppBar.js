
function abif() {

    switch (arguments[0]) {
        case "addAppBarElement":
            return addAppBarElement(arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6]);
        case "removeAppBarElement":
            removeAppBarElement(arguments[1]);
            break;
        case "appBarEnable":
            enableAppBar(arguments[1]);
            break;
    }
}

function onClickClose() {
    // Close the app bar
    var appBar = document.getElementById("gm4html5_app_bar").winControl;    
    appBar.hide();
}

function generateGUID() {
    var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

function addAppBarElement(_type, _icon, _label, _section, _toolTip, _callback) {
    
    try {
        var element = document.createElement((_type == "button") ? "button" : "hr");
        if (_callback != null && _callback != undefined) {
            element.addEventListener("click", _callback, false);
        }

        var appBar = document.getElementById("gm4html5_app_bar");
        appBar.appendChild(element);

        var id = generateGUID();
        new WinJS.UI.AppBarCommand(element, { type: _type, id: id, icon: _icon, label: _label, section: _section, tooltip: _toolTip });
    }
    catch (e) {
        alert("ERROR: Invalid app bar type: " + e.message);
    }

    return id;
}

function removeAppBarElement(_id) {

    var appBar = document.getElementById("gm4html5_app_bar");    
    var appBarButton = document.getElementById(_id);
    appBar.removeChild(appBarButton);
}

function enableAppBar(_flag) {

    var appBar = document.getElementById("gm4html5_app_bar").winControl;
    appBar.disabled = !_flag;    
}