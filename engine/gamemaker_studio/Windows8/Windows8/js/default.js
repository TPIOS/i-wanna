var g_snapMaintainAspectRatio = true;
var g_touchScreenPresent = 1;
var g_cmdLineArgs = null;

// alert is not defined under Windows8
function alert(message) {

    // Create the message dialog and set its content
    try {
        var msg = new Windows.UI.Popups.MessageDialog(message);
        msg.commands.append(new Windows.UI.Popups.UICommand("Close", function () {            
        }));
        msg.defaultCommandIndex = 0;
        msg.showAsync();
    }
    catch (e) {
    }
}


function win8AsyncMessageDialog(_message, _cbOk, _cbDone) {

    // Create the message dialog and set its content
    try {
        var msg = new Windows.UI.Popups.MessageDialog(_message);
        msg.commands.append(new Windows.UI.Popups.UICommand("Ok", _cbOk));
        msg.defaultCommandIndex = 0;
        msg.showAsync().done(_cbDone);
    }
    catch (e) {
        // Multiple dialogs in flight at a time will throw an exception
    }
}

function win8AsyncQuestionDialog(_message, _cbYes, _cbNo, _cbDone) {

    // Create the message dialog and set its content
    try {
        var msg = new Windows.UI.Popups.MessageDialog(_message);
        msg.commands.append(new Windows.UI.Popups.UICommand("Yes", _cbYes));
        msg.commands.append(new Windows.UI.Popups.UICommand("No", _cbNo));
        msg.defaultCommandIndex = 0;
        msg.showAsync().done(_cbDone);
    }
    catch (e) {
        // Multiple dialogs in flight at a time will throw an exception
    }
}

function win8AsyncInputDialog(_message, _cb, _cbDone) {

    // Create the message dialog and set its content
    try {
        var msg = new Windows.UI.Popups.MessageDialog(_message);
        msg.commands.append(new Windows.UI.Popups.UICommand("[NOT SUPPORTED]", _cb));
        msg.defaultCommandIndex = 0;
        msg.showAsync().done(_cbDone);
    }
    catch (e) {
        // Multiple dialogs in flight at a time will throw an exception
    }
}

function win8AsyncLoginDialog(_cbDone) {

    // Create the message dialog and set its content
    try {
        var options = new Windows.Security.Credentials.UI.CredentialPickerOptions();
        options.message = "Enter Details";
        options.caption = "Login";
        options.targetName = "GMLogin";
        options.alwaysDisplayDialog = false;
        options.callerSavesCredential = false;
        options.authenticationProtocol = Windows.Security.Credentials.UI.AuthenticationProtocol.negotiate;
        options.credentialSaveOption = Windows.Security.Credentials.UI.CredentialSaveOption.hidden;

        Windows.Security.Credentials.UI.CredentialPicker.pickAsync(options).then(function (results) {
            
            if (results.errorCode == 0x0) {
                _cbDone(results.credentialUserName, results.credentialPassword);
            }
            else {
                _cbDone("cancelled", "cancelled");
            }
        });
    }
    catch (e) {
    }
}

function launchBrowser(_url) {
    
    var uri = new Windows.Foundation.Uri(_url);
    Windows.System.Launcher.launchUriAsync(uri).then(
       function (success) {
           if (success) {
               // URI launched
           }
           else {
               // URI launch failed
           }
       });
}


var page = WinJS.UI.Pages.define("Windows8Runner.html", {

    ready: function (element, options) {

        window.addEventListener("resize", gse("resize"));
        document.getElementById("cmdClose").addEventListener("click", onClickClose, false);

        settingsCharmInitialise();
        multiTouchInitialise();

        var dataTransferManager = Windows.ApplicationModel.DataTransfer.DataTransferManager.getForCurrentView();
        dataTransferManager.addEventListener("datarequested", dataRequested);

        // Disable the app bar by default and remove the placeholder button
        (function () {
            var appBar = document.getElementById("gm4html5_app_bar");
            var appBarButton = document.getElementById("cmdClose");
            appBar.removeChild(appBarButton);
            appBar.winControl.disabled = true;
        })();
    },
    
    unload: function () {
        var dataTransferManager = Windows.ApplicationModel.DataTransfer.DataTransferManager.getForCurrentView();
        dataTransferManager.removeEventListener("datarequested", dataRequested);
    }
});

function addOnLoadEvent(func) {

    if (typeof (window.onload) != 'function') {
        window.onload = func;
    }
    else {
        var onload_func = window.onload;
        window.onload = function () {            
            onload_func();
            func();
        };
    }
}

(function () {
    "use strict";

    var app = WinJS.Application;
    var activation = Windows.ApplicationModel.Activation;
    var splash = null;
    WinJS.strictProcessing();

    // Start logging (check release/debug)
    // WinJS.Utilities.startLog({type: "info", tags: "custom" });
    
    addOnLoadEvent(function () {

        WinJS.Utilities.startLog({ type: "info", tags: "yoyo" });

        gse("console");
        gse("onloaded", function () {
            ExtendedSplash.remove();
            window.removeEventListener("resize", onSplashResize);
            splash = null;
        });
        if (gse("loading", function () { }) == false) {
            ExtendedSplash.remove();
        }
        win8_ad_data = gse("ads", win8_ads_enable, win8_ads_disable, win8_ads_move);
        win8_analytics_data = gse("analytics", win8_analytics_event, win8_analytics_event_ext);
        gse("iap");        
        gse("browser", launchBrowser);
        gse("async", Array(win8AsyncMessageDialog, win8AsyncQuestionDialog, win8AsyncInputDialog, win8AsyncLoginDialog));        
        gse("cmd_line", g_cmdLineArgs);
        gse("dpi", Windows.Graphics.Display.DisplayProperties.logicalDpi);
        gse("resize")();        
        gse("language", function () {
            var firstLanguageEntry = Windows.System.UserProfile.GlobalizationPreferences.languages.first();
            var language = "en";
            if (firstLanguageEntry.hasCurrent) {
                language = firstLanguageEntry.current;
            }
            // returns BCP-47 and we only provide the ISO 639 code for now so strip extraneous characters
            var n = language.indexOf('-');
            return language.substring(0, (n > 0) ? n : language.length);
        });

        win8_analytics_enable();
    });

    app.onactivated = function (args) {        

        var canv = document.getElementById("canvas");
        if (args.detail.kind === activation.ActivationKind.launch) {
            if (args.detail.previousExecutionState !== activation.ApplicationExecutionState.terminated) {
                // This application has been newly launched
            }
            else {
                // This application has been reactivated from suspension
            }
            args.setPromise(WinJS.UI.processAll().done(canv.focus()));
        }
        g_cmdLineArgs = args.detail.arguments;
    };

    app.oncheckpoint = function (args) {
        // TODO: This application is about to be suspended. Save any state
        // that needs to persist across suspensions here. You might use the
        // WinJS.Application.sessionState object, which is automatically
        // saved and restored across suspension. If you need to complete an
        // asynchronous operation before your application is suspended, call
        // args.setPromise()
    };
    
    function onSplashResize() {
        // Safely update the extended splash screen image coordinates. This function will be fired in response to snapping, unsnapping, rotation, etc...
        if (splash) {
            // Update the coordinates of the splash screen image.            
            ExtendedSplash.updateImageLocation(splash);
        }
    }

    function activated(eventObject) {

        if (eventObject.detail.kind === Windows.ApplicationModel.Activation.ActivationKind.launch) {

            if (eventObject.detail.previousExecutionState === Windows.ApplicationModel.Activation.ApplicationExecutionState.notRunning) {

                // Retrieve splash screen object
                splash = eventObject.detail.splashScreen;

                // Create and display the extended splash screen using the splash screen object and the same image specified for the system splash screen.
                ExtendedSplash.show(splash);

                // Listen for window resize events to reposition the extended splash screen image accordingly.
                // This is important to ensure that the extended splash screen is formatted properly in response to snapping, unsnapping, rotation, etc...
                window.addEventListener("resize", onSplashResize, false);
            }
            else {
                // Launching from a secondary tile when the app is already running will cause onactivated/activated to be hit
                // but the onload event won't and therefore the command line arguments need to propogate to GM at this point.
                gse("cmd_line", g_cmdLineArgs);
            }
        }
    }

    function blurHandler() {

        // WinJS.log("Lost focus", "custom", "info");

        // Make sure the app bar drops down regardless
        var appBar = document.getElementById("gm4html5_app_bar");
        appBar.winControl.hide();

        gse("pause", true);
    }

    function focusHandler() {
        // document.activeElement        
        gse("pause", false);
    }

    function onAfterHide() {
        var canv = document.getElementById("canvas");
        canv.focus();
    }


    g_touchScreenPresent = Windows.Devices.Input.TouchCapabilities().touchPresent;
    if (!g_snapMaintainAspectRatio) {
        gse("mousescale");
    }

    // addEventListener(type, listener =>, useCapture)
    // if useCapture is true all events of the specified type will be dispatched to the registered listener 
    // before being dispatched to any EventTarget beneath it in the DOM tree.

    // focusin is sent to an element when it *or any element inside of it* gains focus
    // the ads are a law unto themselves as far as main window focus is concerned    
    window.addEventListener("focus", focusHandler, false);
    window.addEventListener("blur", blurHandler, false);    

    // Hook up the event to make sure that when the app bar is hidden the main canvas regains focus
    document.addEventListener("afterhide", onAfterHide, false);

    app.addEventListener("activated", activated, false);
    app.start();
})();
