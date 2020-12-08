var g_querySuggestionsFn = null;
var enablesearch = function (_selectionCallback) {

    Windows.ApplicationModel.Search.SearchPane.getForCurrentView().onsuggestionsrequested = function (eventObject) {

        if (g_querySuggestionsFn != null && g_querySuggestionsFn != undefined) {
            g_querySuggestionsFn(eventObject);
        }
    };

    Windows.ApplicationModel.Search.SearchPane.getForCurrentView().onquerysubmitted = function (eventObject) {
        try {
            // _inst, _other, _parameters
            _selectionCallback(null, null, eventObject.queryText);
        }
        catch (e) {
            alert("Search callback failed: " + e.message);
        }
    };
};

var disablesearch = function () {

    Windows.ApplicationModel.Search.SearchPane.getForCurrentView().onsuggestionsrequested = null;
    Windows.ApplicationModel.Search.SearchPane.getForCurrentView().onquerysubmitted = null;

    // Make sure local content suggestions are off
    var localSuggestionSettings = new Windows.ApplicationModel.Search.LocalContentSuggestionSettings();
    localSuggestionSettings.enabled = false;
    Windows.ApplicationModel.Search.SearchPane.getForCurrentView().setLocalContentSuggestionSettings(localSuggestionSettings);
};

var searchsuggestions = function (_suggestions) {

    // Make sure these values can be queried
    if (g_querySuggestionsFn == null || g_querySuggestionsFn == undefined) {

        g_querySuggestionsFn = function (eventObject) {

            var queryText = eventObject.queryText, suggestionRequest = eventObject.request;
            var query = queryText.toLowerCase();
            var maxNumberOfSuggestions = 5;
            for (var i = 0, len = _suggestions.length; i < len; i++) {
                if (_suggestions[i].substr(0, query.length).toLowerCase() === query) {
                    suggestionRequest.searchSuggestionCollection.appendQuerySuggestion(_suggestions[i]);
                    if (suggestionRequest.searchSuggestionCollection.size === maxNumberOfSuggestions) {
                        break;
                    }
                }
            }
        };
    }
};