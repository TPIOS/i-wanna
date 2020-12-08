var win8_analytics_data = null;

function win8_analytics_enable() {

    // Initialise MarkedUp analytics if available
    if (win8_analytics_data) {

        try {
            var apiKey = win8_analytics_data();
            MK.initialize(apiKey);
        }
        catch (e) {
            debug(e.message);
        }
    }
}

function win8_analytics_event(_str) {

    try {
        MK.sessionEvent(_str);
    }
    catch (e) {
        debug(e.message);
    }
}

function win8_analytics_event_ext(_eventId) {

    try {
        var eventData = null;        
        if (arguments.length == 1) {
            eventData = _eventId;
        }
        else {
            eventData = _eventId + " {";
            for (var n = 1; n < arguments.length; n += 2) {

                if (n != 1) {
                    eventData = eventData + ", ";                
                }
                eventData = eventData + arguments[n + 0] + " = " + arguments[n + 1];
            }
            eventData = eventData + (" }");                
        }
        MK.sessionEvent(eventData);
    }
    catch (e) {
        debug(e.message);
    }
}