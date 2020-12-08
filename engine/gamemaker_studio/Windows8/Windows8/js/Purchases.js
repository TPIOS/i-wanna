// Whether or not we're testing purchases
var g_sandboxMode = true;

// The set of products exposed by the store
var g_storeListings = null;

// definitions for purchase results
var PurchaseSucceeded = 0,
    PurchaseFailed = -3;

// Based on Android's general error code
var ResultError = 6;

// Determine which version of CurrentApp we should be using
function getCurrentApp() {
    if (g_sandboxMode) {
        return Windows.ApplicationModel.Store.CurrentAppSimulator;
    }
    return Windows.ApplicationModel.Store.CurrentApp;
}

// Build up a list of product license active states
function getLicensesState() {

    var activeLicenses = [];
    var licenses = getCurrentApp().licenseInformation.productLicenses;
    for (var l in licenses) {

        var license = licenses[l];
        if (license.productId) {
            activeLicenses[license.productId] = license.isActive;
        }
    }
    return activeLicenses;
}

// Actually write out a store proxy file
function writeStoreProxyFile(_licenseFilename, _products, _createProxyFileFn, _onComplete, _onError) {

    var applicationData = Windows.Storage.ApplicationData.current;
    var localFolder = applicationData.localFolder;
    localFolder.createFileAsync(_licenseFilename, Windows.Storage.CreationCollisionOption.replaceExisting).done(
        function (file)
        {
            var xmlData = _createProxyFileFn(_products, getLicensesState());
            Windows.Storage.FileIO.writeTextAsync(file, xmlData, Windows.Storage.Streams.UnicodeEncoding.utf16BE).done(_onComplete, _onError);                
        },
        function (e) {
            _onError();
        });
}

// Write out a replacement to the local device for Sandbox purchasing
function buildStoreProxyFile(_products, _createProxyFileFn, _onComplete, _onError) {

    if (g_sandboxMode) {
        
        // To prevent race conditions with writing out changes to the license file whilst trying to load the setup one we use two different files
        var storedLicenseFilename = "WindowsStoreProxy.xml"
        var licenseFilename = "PurchaseLicenceProxy.xml";

        // Attach to the license changed event to keep the proxy file updated 
        var products = _products.slice();
        getCurrentApp().licenseInformation.onlicensechanged = function () {
            writeStoreProxyFile(storedLicenseFilename, products, _createProxyFileFn, function () { }, function () { });
        };
        

        var applicationData = Windows.Storage.ApplicationData.current;
        var localFolder = applicationData.localFolder;

        // Check for stored licenses that might contain license information that we can get loaded up and re-use
        // but be aware that we still write out a new store proxy file to make sure any product listing changes in gml are reflected
        localFolder.getFileAsync(storedLicenseFilename).done(
            function (file) {
                getCurrentApp().reloadSimulatorAsync(file).done(
                    function () {
                        writeStoreProxyFile(licenseFilename, _products, _createProxyFileFn, _onComplete, _onError);
                    },
                    function (e) {
                        writeStoreProxyFile(licenseFilename, _products, _createProxyFileFn, _onComplete, _onError);
                    });
            },
            function () {
                writeStoreProxyFile(licenseFilename, _products, _createProxyFileFn, _onComplete, _onError);
            });
    }
    else {
        _onComplete();
    }
}

// Activate the windows store communication
function activateStore(_onComplete, _onError) {

    if (g_sandboxMode) {
                                
        loadInAppPurchaseProxyFile(
            function() {
                g_storeListings = getCurrentApp().loadListingInformationAsync().done(
                    function (listings) {
                        g_storeListings = listings;
                        _onComplete();
                    },
                    function (err) { 
                        _onError();
                    });
            },
            _onError);        
    }
    else {
        getCurrentApp().loadListingInformationAsync().done(
            function (listings) {
                g_storeListings = listing;
                _onComplete();
            },
            _onError);
    }
}

// Attempt to acquire a product
function purchaseContent(_id, _purchaseIndex, _complete, _error) {
    
    try {
        getCurrentApp().requestProductPurchaseAsync(_id, true).then(
            function (_receipt) {
                
                // Don't bother with a token... you can't actually consume on 8.0 anyway
                // var receipt = _receipt.replaceAll("\"", "'");
                var receipt = _receipt.replace(/\"/g, "'");
                var json = "{ \"productId\":\"" + _id + "\"" +
                            ", \"receipt\":\"" + receipt + "\"" +                            
                            ", \"purchaseIndex\":" + _purchaseIndex +
                            ", \"purchaseState\":" + ((receipt == "") ? PurchaseFailed : PurchaseSucceeded) + " }";
                _complete(json);
            },
            function (e) {
                var json = "{ \"productId\":\"" + _id + "\"" +
                            ", \"purchaseIndex\":" + _purchaseIndex +
                            ", \"purchaseState\":" + PurchaseFailed + " }";
                _error(json);
            });
    }
    catch (e) {        
        var json = "{ \"productId\":\"" + _id + "\"" +
                            ", \"purchaseIndex\":" + _purchaseIndex +
                            ", \"purchaseState\":" + PurchaseFailed + " }";
        _error(json);        
    }
}

// Attempt to consume a product
function consumeContent(_id, _complete, _error) {
    
    // Windows 8.0 does not support consumable content
    var json = "{ \"productId\":\"" + _id + "\"" +
				", \"consumed\":\"false\"" +
                ", \"response\":" + ResultError + " }";
    _complete(json);
}

function restorePurchases(_complete) {

    // We don't currently support this for Windows8
    _complete(0);
}

// Notify the title of any products/purchases found
function synchroniseProducts(_onProductFound, _onPurchaseFound, _onComplete, _onError) {    

    // Enumerate all known products    
    for (var l in g_storeListings.productListings) {
        if (!g_storeListings.productListings.hasOwnProperty(l)) continue;

        var listing = g_storeListings.productListings[l];
        if (typeof (listing) == "object") {

            var json = "{ \"productId\":\"" + listing.productId + "\"" +
                   ", \"price\":\"" + listing.formattedPrice + "\"" +
                   ", \"description\":\"" + listing.name + "\"}";
            _onProductFound(json);
        }
    }

    // Enumerate all known purchases... I appreciate this could be done in the same loop as above,
    // but for consistency across platforms I'm separating the two    
    for (var l in g_storeListings.productListings) {
        if (!g_storeListings.productListings.hasOwnProperty(l)) continue;

        var listing = g_storeListings.productListings[l];
        var license = getCurrentApp().licenseInformation.productLicenses[listing.productId];
        if (license && license.isActive)
        {            
            var json = "{ \"productId\":\"" + listing.productId + "\"" +                        
                ", \"purchaseState\":" + PurchaseSucceeded + " }";
            _onPurchaseFound(json);
        }    
    }    
    _onComplete();
}

// Load up the WindowsStoreProxy.xml replacement for Sandbox purchasing
function loadInAppPurchaseProxyFile(_onComplete, _onError) {

    var applicationData = Windows.Storage.ApplicationData.current;
    var localFolder = applicationData.localFolder;
    localFolder.getFileAsync("PurchaseLicenceProxy.xml").done(
        function (file) {
            
            getCurrentApp().reloadSimulatorAsync(file).done(
                function () {
                    var licenseInformation = getCurrentApp().licenseInformation;
                    if (!licenseInformation.isActive || licenseInformation.IsTrial) {
                        _onError();
                    }
                    else {
                        _onComplete();
                    }
                },
                function (err) {
                    _onError();
                });
        },
        _onError);
}

// --------------------------------------------------------------------------------------------------------------------
function downloadFile(_contentURL, _targetFilename, _complete, _error, _progress) {

    // Asynchronously create the file in the local folder.
    var applicationData = Windows.Storage.ApplicationData.current;
    var localFolder = applicationData.localFolder;


    // Convert forward slashes to back slashes on the local path and ensure there are no double backslashes present
    var targetFilename = _targetFilename.replace(/\//g, '\\').replace(/\\\\/g, '\\');    
    localFolder.createFileAsync(targetFilename, Windows.Storage.CreationCollisionOption.replaceExisting).done(

        function (targetFile) {

            // Create a new download operation
            try {
                var uri = Windows.Foundation.Uri(_contentURL);
                var downloader = new Windows.Networking.BackgroundTransfer.BackgroundDownloader();            
                download = downloader.createDownload(uri, targetFile);
                download.startAsync().done(
                    function (result) {
                        _complete(targetFilename);
                    },
                    _error);
            }
            catch (e) {
                alert("FATAL ERRROR " + e.message);
            }
        },
        _error);
}


function unzipContent(_filename, _localPath, _complete, _error) {

    // Asynchronously create the file in the local folder.
    var applicationData = Windows.Storage.ApplicationData.current;
    var localFolder = applicationData.localFolder;

    // Convert forward slashes to back slashes on the local path and ensure there are no double backslashes present
    var filename = _filename.replace(/\//g, '\\').replace(/\\\\/g, '\\');
    var localPath = _localPath.replace(/\//g, '\\').replace(/\\\\/g, '\\');
    localFolder.getFileAsync(filename).done(

        function (file) {
            file.openAsync(Windows.Storage.FileAccessMode.read).done(function (stream) {
                var blob = MSApp.createBlobFromRandomAccessStream(file.contentType, stream);
                extractFilesFromZip(blob, localPath, _complete, _error);
            });
        });
    return 0;
}

function extractFilesFromZip(_blob, _localPath, _complete, _error) {
    
    zip.createReader(new zip.BlobReader(_blob), 
        function (zipReader) {
            zipReader.getEntries(
                function (entries) {
                    unzipEntries(entries, _localPath, _complete, _error);
                },
                function () {
                    _error();
                });
        });
}

function unzipEntries(entries, _localPath, _complete, _error) {

    var fileList = [];
    var errored = false;
    var completionCount = 0;
    for (var n = 0; n < entries.length; n++) {

        var applicationData = Windows.Storage.ApplicationData.current;
        var localFolder = applicationData.localFolder;

        var entry = entries[n];
        if (entry.directory) {
            // Again, replace forward slashes with backslashes and remove any doubling up going on and replace spaces with %20 
            var folderName = entry.filename.replace(/\//g, '\\').replace(/\\\\/g, '\\').replace(/ /g, "%20");
            localFolder.createFolderAsync(_localPath + '\\' + folderName, Windows.Storage.CreationCollisionOption.replaceExisting).done(
                function () {
                    fileList[fileList.length] = folderName;
                    completionCount = checkUnzipComplete(completionCount, entries.length, errored, fileList, _complete, _error);
                },
                function () {
                    // This might not actually be that bad since all the operations are async and if this operation is beaten
                    // to the punch by a file writing into this folder we'll end up here. I do want to let the user reflect
                    // the entirey of their zip hierarchy in case they intend to use this sub folder later on
                    errored = true;
                    completionCount = checkUnzipComplete(completionCount, entries.length, errored, fileList, _complete, _error);
                },
                function () {});
        }
        else {
            // write out the entry's contents to a local file accordingly doing the usual with slashes
            var fileName = entry.filename.replace(/\//g, '\\').replace(/\\\\/g, '\\').replace(/ /g, "%20");
            localFolder.createFileAsync(_localPath + '\\' + fileName, Windows.Storage.CreationCollisionOption.replaceExisting).done(
                function (file) {
                    var writer = new zip.BlobWriter();
                    entry.getData(writer,
                        function (blob) {
                            fileList[fileList.length] = file.name;
                            saveBlobToFile(file, blob,
                                function () {
                                    completionCount = checkUnzipComplete(completionCount, entries.length, errored, fileList, _complete, _error);
                                },
                                function () {
                                    errored = true;
                                    completionCount = checkUnzipComplete(completionCount, entries.length, errored, fileList, _complete, _error);
                                });
                        },
                        function () { // getData progress
                        });
                },
                function (err) {
                    errored = true;
                    completionCount = checkUnzipComplete(completionCount, entries.length, errored, fileList, _complete, _error);
                });
        }
    }
}

function saveBlobToFile(_file, _blob, _oncomplete, _onerror) {

    try {
        _file.openAsync(Windows.Storage.FileAccessMode.readWrite).then(
            function (output) {
                // Get the IInputStream stream from the blob object
                var input = _blob.msDetachStream();
                // Copy the stream from the blob to the File stream                
                Windows.Storage.Streams.RandomAccessStream.copyAsync(input, output).then(function () {
                    output.flushAsync().done(
                        function () {
                            input.close();
                            output.close();
                            _oncomplete();
                        },
                        function (err) {
                            _onerror();
                        });
                },
                function (err) {
                    _onerror();
                });
            }
        );
    }
    catch (e) {
        _onerror();
    }
}

// Check to see if we're reached our target count of zip entries and handle success/failure
function checkUnzipComplete(_count, _targetCount, _errored, _fileEntries, _oncomplete, _onerror) {

    _count++;
    if (_count == _targetCount) {
        if (_errored) {
            _onerror(_count, _fileEntries);
        }
        else {
            _oncomplete(_count, _fileEntries);
        }
    }
    return _count;
}


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------

function checkLicense(_id) {

    var currentApp = getCurrentApp();
    var licenseInformation = currentApp.licenseInformation;
    var license = licenseInformation.productLicenses.lookup(_id);

    return license.isActive;
}

function setLocalSetting(_key, _flag) {

    var applicationData = Windows.Storage.ApplicationData;
    var localSettings = applicationData.current.localSettings;
    localSettings.values[_key] = _flag;
}

function getLocalSetting(_key) {

    var applicationData = Windows.Storage.ApplicationData;
    var localSettings = applicationData.current.localSettings;
    if (localSettings.values[_key] != undefined) {
        return localSettings.values[_key];
    }
    return null;
}

function GetHardwareId() {    

    var hardwareToken = Windows.System.Profile.HardwareIdentification.getPackageSpecificToken(null);
    var idReader = Windows.Storage.Streams.DataReader.fromBuffer(hardwareToken.id);
    var idArray = new Array(hardwareToken.id.length);
    idReader.readBytes(idArray);

    var udid = "";
    for (var i = 0; i < idArray.length; i++) {
        udid = udid + idArray[i].toString();
    }    
    return udid;
}