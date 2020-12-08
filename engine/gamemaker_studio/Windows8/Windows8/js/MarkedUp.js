/*!
* This file is meant to to be used within WinJS projects in order to ease the use of the MarkedUp SDK
**/

var MK = (function (mk) {

    "use strict";

    var client = mk.AnalyticClient;
    var navHandle = null;

    //error helper
    function serializeError(error) {

        //create the log object
        var ex = new mk.MKLog();

        if (!error) {

            //we got a null error object
            return null;

        }

        if (error && error.status && error.readyState) {

            //we probaly got a network request object
            ex.message = "Network Request with HTTP code " + error.status;
            ex.errorMessage = ex.message;
            ex.errorStackTrace = JSON.stringify(error);

            return ex;
        }
        
        if (error && error.detail && error.detail.errorMessage) {
            
            //we got something that looks like a structured error object
            ex.errorMessage = error.detail.errorMessage;
            ex.errorStackTrace = error.detail.errorStackTrace || JSON.stringify(error);
            return ex;
        }

        //we got an object of indeterminate origin
        ex.message = error.message || "Recived a JavaScript Object (see stack trace for object JSON)";
        ex.errorMessage = error.message;
        ex.errorStackTrace = JSON.stringify(error);

        return ex;
    }

    function createLog(level, message, error) {

        var log;

        if (error) {

            //use helper to create log
            log = serializeError(error) || new mk.MKLog();
            log.level = level;
            log.message = message;
            return log;

        } else {

            //we'll do it manually
            log = new mk.MKLog();
            log.level = level;
            log.message = message;
            return log;

        }

    }

    //return object
    return {

        initialize: function (apiKey) {

            try {
                client.initialize(apiKey);
            } catch (e) {

            }

        },
        
        /* logging functions */

        debug: function(message, error) {

            try {
                var log = createLog(mk.LogLevel.debug, message, error);
                client.log(log);
            } catch (e) {

            }

        },

        trace: function (message, error) {

            try {
                var log = createLog(mk.LogLevel.trace, message, error);
                client.log(log);
            } catch (e) {

            }

        },

        info: function (message, error) {

            try {
                var log = createLog(mk.LogLevel.info, message, error);
                client.log(log);
            } catch (e) {

            }

        },

        error: function (message, error) {

            try {
                var log = createLog(mk.LogLevel.error, message, error);
                client.log(log);
            } catch (e) {

            }

        },

        fatal: function (message, error) {

            try {
                var log = createLog(mk.LogLevel.fatal, message, error);
                client.log(log);
            } catch (e) {

            }

        },

        /* lifecycle */

        appStart: function () {

            try {
                client.appStart();
            } catch (e) {

            }

        },

        appSuspend: function () {

            try {
                client.appSuspend();
            } catch (e) {

            }

        },

        appExit: function () {

            try {
                client.appExit();
            } catch (e) {

            }

        },

        appResume: function () {

            try {
                client.appResume();
            } catch (e) {

            }

        },
        
        /* navigation */

        enterPage: function (page) {

            try {
                client.enterPage(page);
            } catch (e) {

            }

        },

        exitPage: function (page) {

            try {
                client.exitPage(page);
            } catch (e) {

            }

        },
        
        //Internal method for handling automatic navigation events
        _navigatedPage: function (navEvent) {
            try {
                if (navEvent && navEvent.detail && navEvent.detail.location) {
                    client.enterPage(navEvent.detail.location);
                }
            }
            catch (e) {

            }
        },

        //Automatically capture page change events
        registerNavigationFrame: function () {
            try {
                if (navHandle == null) {
                    var nav = WinJS.Navigation;
                    if (nav) {
                        navHandle = this._navigatedPage;
                        nav.addEventListener("navigated", navHandle);
                    }
                }
            }
            catch (e) {

            }
        },
        

        orientationChange: function (page, orientation) {

            try {
                client.orientationChanged(page, orientation);
            } catch (e) {

            }

        },
        
        /* forms */

        inputFieldSelected: function (page, name) {

            try {
                client.inputFieldSelected(page, name);
            } catch (e) {

            }

        },

        inputFieldUnselected: function (page, name) {

            try {
                client.inputFieldUnselected(page, name);
            } catch (e) {

            }

        },

        inputFieldTextEntered: function (page, name) {

            try {
                client.inputFieldTextEntered(page, name);
            } catch (e) {

            }

        },

        inputFormSubmitted: function (page, name) {

            try {
                client.inputFormSubmitted(page, name);
            } catch (e) {

            }

        },
        
        /* commerce */
        
        //in-app purchase

        inAppPurchaseOfferShown: function (productId)
        {
            try {
                client.inAppPurchaseOfferShown(productId);
            } catch (e) {

            }
        },

        inAppPurchaseOfferDismissed: function (productId)
        {
            try {
                client.inAppPurchaseOfferDismissed(productId);
            } catch (e) {

            }
        },

        inAppPurchaseOfferSelected: function (productId)
        {
            try {
                client.inAppPurchaseOfferSelected(productId);
            } catch (e) {

            }
        },

        inAppPurchaseOfferCompleted: function (productId)
        {
            try {
                client.inAppPurchaseOfferCompleted(productId);
            } catch (e) {

            }
        },

        inAppPurchaseOfferCancelled: function (productId)
        {
            try {
                client.inAppPurchaseOfferCancelled(productId);
            } catch (e) {

            }
        },

        inAppPurchaseOfferExpired: function (productId)
        {
            try {
                client.inAppPurchaseOfferExpired(productId);
            } catch (e) {

            }
        },

        //trial

        trialConversionOfferShown: function ()
        {
            try {
                client.trialConversionOfferShown();
            } catch (e) {

            }
        },

        trialConversionOfferDismissed: function ()
        {
            try {
                client.trialConversionOfferDismissed();
            } catch (e) {

            }
        },

        trialConversionOfferSelected: function ()
        {
            try {
                client.trialConversionOfferSelected();
            } catch (e) {

            }
        },

        trialConversionOfferCompleted: function ()
        {
            try {
                client.trialConversionOfferCompleted();
            } catch (e) {

            }
        },

        trialConversionOfferCancelled: function ()
        {
            try {
                client.trialConversionOfferCancelled();
            } catch (e) {

            }
        },

        trialConversionOfferExpired: function ()
        {
            try {
                client.trialConversionOfferExpired();
            } catch (e) {

            }
        },

        //subscriptions

        subscriptionOfferShown: function ( productId)
        {
            try {
                client.subscriptionOfferShown(productId);
            } catch (e) {

            }
        },

        subscriptionOfferDismissed: function (productId)
        {
            try {
                client.subscriptionOfferDismissed(productId);
            } catch (e) {

            }
        },

        subscriptionOfferSelected: function (productId)
        {
            try {
                client.subscriptionOfferSelected(productId);
            } catch (e) {

            }
        },

        subscriptionOfferCompleted: function (productId)
        {
            try {
                client.subscriptionOfferCompleted(productId);
            } catch (e) {

            }
        },

        subscriptionOfferCancelled: function (productId)
        {
            try {
                client.subscriptionOfferCancelled(productId);
            } catch (e) {

            }
        },

        subscriptionOfferExpired: function (productId)
        {
            try {
                client.subscriptionOfferExpired(productId);
            } catch (e) {

            }
        },
        
        /* contracts */
        
        searchRequested: function(page, query)
        {
            try {
                client.searchRequested(page, query);
            } catch (e) {

            }
        },

        shareStarted: function(page)
        {
            try {
                client.shareStarted(page);
            } catch (e) {

            }
        },

        shareCompleted: function(page)
        {
            try {
                client.shareCompleted(page);
            } catch (e) {

            }
        },

        shareCancelled: function(page)
        {
            try {
                client.shareCancelled(page);
            } catch (e) {

            }
        },
        
        /* custom session events */

        sessionEvent: function (eventName) {

            try {
                client.sessionEvent(eventName);
            } catch (e) {

            }

        },
        
        /* last chance exception logging */
        
        logLastChanceException: function (error) {

            try {
                var log = createLog(mk.LogLevel.fatal, "An unhandled error occured in the app", error);
                client.logLastChanceException(log);
            } catch (e) {

            }
        }
    };

}(MarkedUp));