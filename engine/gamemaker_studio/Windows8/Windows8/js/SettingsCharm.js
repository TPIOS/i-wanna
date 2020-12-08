var g_settingsCharmEntries = {};

// Allows the user to launch an external URI (http:// based) from a settings option
function settingsLaunchUri(_url) {

    var uriToLaunch = _url;

    // Create a Uri object from a URI string 
    var uri = new Windows.Foundation.Uri(uriToLaunch);
        
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

// Called during the ready: event for the source html
function settingsCharmInitialise() {

    WinJS.Application.onsettings = function (e) {

        // Populate settings with any html entries
        var map = {};
        for (var key in g_settingsCharmEntries) {
            if (!g_settingsCharmEntries.hasOwnProperty(key)) continue;

            var entry = g_settingsCharmEntries[key];
            if (entry != null) {
                if (entry.cmd == null) {

                    map[entry.id] = {};
                    map[entry.id]["title"] = entry.title;
                    map[entry.id]["href"] = entry.href;
                }
            }
        }
        e.detail.applicationcommands = map;
        WinJS.UI.SettingsFlyout.populateSettings(e);

        // Add any additional commands to the settings charm
        var appSettings = Windows.UI.ApplicationSettings;
        var vector = e.detail.e.request.applicationCommands;

        for (var key in g_settingsCharmEntries) {
            if (!g_settingsCharmEntries.hasOwnProperty(key)) continue;

            var entry = g_settingsCharmEntries[key];
            if (entry != null) {
                if (entry.cmd != null) {
                    var cmd = new appSettings.SettingsCommand(key, entry.title, entry.cmd);
                    vector.append(cmd);
                }
            }
        }
    };
}