g_secondaryTilePin = function (_id, _shortName, _displayName, _cmdLineArgs, _options, _tileImg, _wideTileImg, _textCol) {

    try {
        var uriLogo = null;
        var uriWideLogo = null;
        if (_tileImg != undefined && _tileImg != "") {
            uriLogo = new Windows.Foundation.Uri(_tileImg);
        }
        if (_wideTileImg != undefined && _wideTileImg != "") {
            uriWideLogo = new Windows.Foundation.Uri(_wideTileImg);
        }
        var tileActivationArguments = _cmdLineArgs;

        // Pull the tile options out of the provided array
        var tileOptions = Windows.UI.StartScreen.TileOptions.none;        
        for (var entry in _options) {
            
            var option = _options[entry];
            if (option == "shownameonlogo") {
                tileOptions |= Windows.UI.StartScreen.TileOptions.showNameOnLogo;
            }
            else if (option == "shownameonwidelogo") {
                tileOptions |= Windows.UI.StartScreen.TileOptions.showNameOnWideLogo;
            }
            else if (option == "copyondeployment") {
                tileOptions |= Windows.UI.StartScreen.TileOptions.copyOnDeployment;
            }
        }

        // Create the secondary tile just like we did in pinTile scenario.
        // Provide a wideLogo since wide tiles have a few more templates available for notifications
        var tile = new Windows.UI.StartScreen.SecondaryTile(_id,
                                                            _shortName,
                                                            _displayName,
                                                            tileActivationArguments,
                                                            tileOptions,
                                                            uriLogo,
                                                            uriWideLogo);
        tile.foregroundText = (_textCol.toLowerCase() == "light")
            ? Windows.UI.StartScreen.ForegroundText.light
            : Windows.UI.StartScreen.ForegroundText.dark;

        tile.requestCreateAsync().done(function (isPinned) {
            if (isPinned) {
                WinJS.log && WinJS.log("Secondary tile was successfully pinned.", "sample", "status");
            } else {
                WinJS.log && WinJS.log("Secondary tile was not pinned.", "sample", "error");
            }
        });
    }
    catch (e) {
        alert(e.message);
    }
};

g_secondaryTileDelete = function (_id) {

    var tileToBeDeleted = new Windows.UI.StartScreen.SecondaryTile(_id);
    tileToBeDeleted.requestDeleteAsync().then(function (isDeleted) {
        if (isDeleted) {
            // Secondary tile successfully deleted.
        }
        else {
            // Secondary tile not deleted.
        }
    });
};