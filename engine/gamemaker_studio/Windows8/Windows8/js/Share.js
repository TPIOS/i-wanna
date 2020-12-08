function shareData(_cmd) {
        
    this.filename = "";
    this.fileData = undefined;    
    this.textData = undefined; // can be URL/Text/JSON
    this.title = "";
    this.description = "";
    this.immediate = true;
    this.cmd = _cmd;
}

// Specific forms of data share objects
var g_shareImageData = null;
var g_shareFileData = null;
var g_shareURLData = null;
var g_shareTextData = null;

// Callback routine used when dataRequested is hit
var g_shareDataCallback = null;

// Self-executing anonymous function to kick things into gear
(function () {

    // Initialize the activation handler for receiving data
    // WinJS.Application.addEventListener("activated", activatedHandler, false);
    // WinJS.Application.addEventListener("shareready", shareReady, false);
    // WinJS.Application.start();
    g_shareImageData = new shareData(function () {
                
        g_shareDataCallback = shareImageData;
        if (g_shareImageData.immediate) {
            try {
                Windows.ApplicationModel.DataTransfer.DataTransferManager.showShareUI();
            }
            catch (e) {
                // Probably a "The message filter indicated that the application is busy." exception - safe to carry on
            }
        }
    });

    g_shareFileData = new shareData(function () {

        g_shareDataCallback = shareFileData;
        if (g_shareFileData.immediate) {
            try {
                Windows.ApplicationModel.DataTransfer.DataTransferManager.showShareUI();
            }
            catch (e) {
                // Probably a "The message filter indicated that the application is busy." exception - safe to carry on
            }
        }
    });

    g_shareURLData = new shareData(function () {

        g_shareDataCallback = shareURLData;
        if (g_shareURLData.immediate) {
            try {
                Windows.ApplicationModel.DataTransfer.DataTransferManager.showShareUI();
            }
            catch (e) {
                // Probably a "The message filter indicated that the application is busy." exception - safe to carry on
            }
        }
    });

    g_shareTextData = new shareData(function () {

        g_shareDataCallback = shareTextData;
        if (g_shareTextData.immediate) {
            try {
                Windows.ApplicationModel.DataTransfer.DataTransferManager.showShareUI();
            }
            catch (e) {
                // Probably a "The message filter indicated that the application is busy." exception - safe to carry on
            }
        }
    });

}());

function dataRequested(e) {

    if ((g_shareDataCallback != null) && (g_shareDataCallback != undefined)) {
        g_shareDataCallback(e);
    }
}

function completeImageShare(request) {

    var imageFile = g_shareImageData.fileData;
    if (imageFile != null) {
        var streamReference = Windows.Storage.Streams.RandomAccessStreamReference.createFromFile(imageFile);
        request.data.properties.thumbnail = streamReference;

        // It's recommended to always use both setBitmap and setStorageItems for sharing a single image
        // since the Target app may only support one or the other

        // Put the image file in an array and pass it to setStorageItems
        request.data.setStorageItems([imageFile]);

        // The setBitmap method requires a RandomAccessStreamReference
        request.data.setBitmap(streamReference);
    }
    else {
        request.failWithDisplayText("No valid image was selected for sharing");
    }
}

function shareImageData(e) {

    var request = e.request;
    try {
        var dataPackageTitle = g_shareImageData.title;
        if ((typeof dataPackageTitle === "string") && (dataPackageTitle !== "")) {

            request.data.properties.title = dataPackageTitle;

            // The description is optional.
            var dataPackageDescription = g_shareImageData.description;
            if ((typeof dataPackageDescription === "string") && (dataPackageDescription !== "")) {
                request.data.properties.description = dataPackageDescription;
            }
            //if grabbing from the screen without using an intermediate temporary file we can do it like this
            //request.data.setBitmap(Windows.Storage.Streams.RandomAccessStreamReference.createFromStream(canvas.msToBlob().msDetachStream()));

            // Determine whether or not we're doing a screengrab
            if (g_shareImageData.filename == null || g_shareImageData.filename == undefined) {

                var deferral = request.getDeferral();

                var dataURI = canvas.toDataURL();
                var byteString = dataURI.split(',')[1];
                var buf = Windows.Security.Cryptography.CryptographicBuffer.decodeFromBase64String(byteString);
                var applicationData = Windows.Storage.ApplicationData.current;
                var localFolder = applicationData.localFolder;

                // set promise to make sure this completes prior to a share operation data request?       
                localFolder.createFileAsync("screengrab.png", Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {
                    return Windows.Storage.FileIO.writeBufferAsync(file, buf).then(function () {
                        localFolder.getFileAsync("screengrab.png").done(
                            function (imageFile) {
                                g_shareImageData.fileData = imageFile;
                                completeImageShare(request);
                                deferral.complete();
                            },
                            function (err) {
                                deferral.complete();
                            })
                    });
                });
            }
            else {
                var filename = g_shareImageData.filename.replace(/\//g, '\\');
                var deferral = request.getDeferral();
                Windows.ApplicationModel.Package.current.installedLocation.getFileAsync(filename).done(
                    function (imageFile) {
                        g_shareImageData.fileData = imageFile;
                        completeImageShare(request);
                        deferral.complete();
                    },
                    function (err) {
                        request.failWithDisplayText("Missing file: " + filename);
                        deferral.complete();                        
                    });
            }
        }
        else {
            request.failWithDisplayText(SdkSample.missingTitleError);
        }
    }
    catch (e) {
        request.failWithDisplayText(e.message);
    }
}

function shareFileData(e) {
        
    var request = e.request;
    try {
        // Title is required
        var dataPackageTitle = g_shareFileData.title;
        if ((typeof dataPackageTitle === "string") && (dataPackageTitle !== "")) {            
            request.data.properties.title = dataPackageTitle;

            // The description is optional.
            var dataPackageDescription = g_shareFileData.description;
            if ((typeof dataPackageDescription === "string") && (dataPackageDescription !== "")) {
                request.data.properties.description = dataPackageDescription;
            }

            // Turn filename into file streams and store after deferring until async operations are complete
            var deferral = request.getDeferral();
            try {
                if (g_shareFileData.filename != null && g_shareFileData.filename != undefined) {

                    var filename = g_shareFileData.filename.replace(/\//g, '\\');
                    Windows.ApplicationModel.Package.current.installedLocation.getFileAsync(filename).done(
                        function (_file) {
                            request.data.setStorageItems([_file]);
                            deferral.complete();
                        },
                        function (_err) {
                            deferral.complete();                        
                        });
                }
            }
            catch (e) {
                request.failWithDisplayText(e.message);                
            }
        }
        else {
            request.failWithDisplayText(SdkSample.missingTitleError);
        }
    }
    catch (e) {
        request.failWithDisplayText(e.message);
    }
}

function shareURLData(e) {

    var request = e.request;        
    // Title is required
    var dataPackageTitle = g_shareURLData.title;
    if ((typeof dataPackageTitle === "string") && (dataPackageTitle !== "")) {
        var dataPackageLink = g_shareURLData.textData;
        if ((typeof dataPackageLink === "string") && (dataPackageLink !== "")) {
            request.data.properties.title = dataPackageTitle;

            // The description is optional.
            var dataPackageDescription = g_shareURLData.description;
            if ((typeof dataPackageDescription === "string") && (dataPackageDescription !== "")) {
                request.data.properties.description = dataPackageDescription;
            }
            try {
                request.data.setUri(new Windows.Foundation.Uri(dataPackageLink));
                WinJS.log && WinJS.log("", "sample", "error");
            }
            catch (ex) {
                WinJS.log && WinJS.log("Exception occured: the uri provided " + dataPackageLink + " is not well formatted.", "sample", "error");
            }
        }
        else {
            request.failWithDisplayText("Enter the text you would like to share and try again.");
        }
    }
    else {
        request.failWithDisplayText(SdkSample.missingTitleError);
    }
}

function shareTextData(e) {

    var request = e.request;
    try {
        // Title is required
        var dataPackageTitle = g_shareTextData.title;
        if ((typeof dataPackageTitle === "string") && (dataPackageTitle !== "")) {
            var dataPackageText = g_shareTextData.textData;
            if ((typeof dataPackageText === "string") && (dataPackageText !== "")) {
                request.data.properties.title = dataPackageTitle;

                // The description is optional.
                var dataPackageDescription = g_shareTextData.description;
                if ((typeof dataPackageDescription === "string") && (dataPackageDescription !== "")) {
                    request.data.properties.description = dataPackageDescription;
                }
                request.data.setText(dataPackageText);
            }
            else {
                request.failWithDisplayText("Missing text in share operation");
            }
        }
        else {
            request.failWithDisplayText(SdkSample.missingTitleError);
        }
    }
    catch (e) {
        request.failWithDisplayText(e.message);
    }
}

/*
// var shareOperation = null;
// var customFormatName = "JSON";

/// <summary>
/// Handler executed on activation of the target
/// </summary>
/// <param name="eventArgs">
/// Arguments of the event. In the case of the Share contract, it has the ShareOperation
/// </param>
function activatedHandler(eventObject) {

    // In this sample we only do something if it was activated with the Share contract
    if (eventObject.detail.kind === Windows.ApplicationModel.Activation.ActivationKind.shareTarget) {
        eventObject.setPromise(WinJS.UI.processAll());

        // We receive the ShareOperation object as part of the eventArgs
        shareOperation = eventObject.detail.shareOperation;

        // We queue an asychronous event so that working with the ShareOperation object does not
        // block or delay the return of the activation handler.
        WinJS.Application.queueEvent({ type: "shareready" });
    }
}

/// <summary>
/// Handler executed when ready to share; handling the share operation should be performed
/// outside the activation handler
/// </summary>
function shareReady(eventArgs) {

    var title = shareOperation.data.properties.title;
    var description = shareOperation.data.properties.description;

    // If this app was activated via a QuickLink, display the QuickLinkId
    if (shareOperation.quickLinkId !== "") {
        alert("Quick link sent to app");
    }

    // Display a thumbnail if available
    if (shareOperation.data.properties.thumbnail) {
        shareOperation.data.properties.thumbnail.openReadAsync().done(function (thumbnailStream) {
            var thumbnailBlob = MSApp.createBlobFromRandomAccessStream(thumbnailStream.contentType, thumbnailStream);
            var thumbnailUrl = URL.createObjectURL(thumbnailBlob, { oneTimeOnly: true });
            // document.getElementById("thumbnailImage").src = thumbnailUrl;
            // document.getElementById("thumbnailArea").className = "unhidden";
        });
    }

    // Display the data received based on data type
    if (shareOperation.data.contains(Windows.ApplicationModel.DataTransfer.StandardDataFormats.text)) {
        alert("Received shared content: Text");
    }
    if (shareOperation.data.contains(Windows.ApplicationModel.DataTransfer.StandardDataFormats.uri)) {
        alert("Received shared content: URI");
    }
    if (shareOperation.data.contains(Windows.ApplicationModel.DataTransfer.StandardDataFormats.storageItems)) {
        shareOperation.data.getStorageItemsAsync().done(function (storageItems) {
            for (var i = 0; i < storageItems.size; i++) {
                alert("Received shared content: Storage Item " + storageItems.getAt(i).name);
            }
        });
    }
    if (shareOperation.data.contains(Windows.ApplicationModel.DataTransfer.StandardDataFormats.bitmap)) {
        shareOperation.data.getBitmapAsync().done(function (bitmapStreamReference) {
            bitmapStreamReference.openReadAsync().done(function (bitmapStream) {
                if (bitmapStream) {
                    alert("Received shared content: Bitmap stream");
                }
            });
        });
    }
    if (shareOperation.data.contains(customFormatName)) {
        shareOperation.data.getTextAsync(customFormatName).done(function (customFormatString) {

            var customFormatObject = JSON.parse(customFormatString);
            if (customFormatObject) {
                // This sample expects the custom format to be of type JSON
                if (customFormatObject.type === customFormatName) {
                    alert("Received shared content: Custom " + customFormatString);
                }
            }
        });
    }
}
*/