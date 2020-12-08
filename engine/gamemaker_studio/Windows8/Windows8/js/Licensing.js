var g_licensingSandbox = false;

function setSandboxLicense(_licenseString) {

    // Set to denote that we're going to check the app simulator for licensing information
    g_licensingSandbox = true;

    // Change GM new line hashes to actual new line indicators
    _licenseString = _licenseString.replace(/#/g, "\r\n");

    var applicationData = Windows.Storage.ApplicationData.current;
    var localFolder = applicationData.localFolder;

    // Create a temporary file to dump the license string data to
    localFolder.createFileAsync("yoyoLicenseTemp.xml", Windows.Storage.CreationCollisionOption.replaceExisting).then(
        function (file) {
            return Windows.Storage.FileIO.writeTextAsync(file, _licenseString, Windows.Storage.Streams.UnicodeEncoding.utf16BE).then(function () {
                localFolder.getFileAsync("yoyoLicenseTemp.xml").done(
                    function (file) {
                        var currentApp = Windows.ApplicationModel.Store.CurrentAppSimulator;
                        currentApp.reloadSimulatorAsync(file).done(
                            function () {
                            },
                            function (error) {
                                alert("Set licensing failed: " + error);
                            });
                    });
            });
        });
}

function checkTrialLicense() {

    var currentApp;
    if (g_licensingSandbox) {
        currentApp = Windows.ApplicationModel.Store.CurrentAppSimulator;
    }
    else {
        currentApp = Windows.ApplicationModel.Store.CurrentApp;
    }
    var licenseInformation = currentApp.licenseInformation;
    return licenseInformation.isTrial;
}