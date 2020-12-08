function LiveTileNotification(_tileXml, _expiryTime, _tag, _secondaryTileID) {

    var tileNotification = new Windows.UI.Notifications.TileNotification(_tileXml);
    if (_expiryTime != -1) {
        tileNotification.expirationTime = _expiryTime;
    }
    tileNotification.tag = _tag;
    if ((_secondaryTileID != null) && (_secondaryTileID != undefined)) {
        Windows.UI.Notifications.TileUpdateManager.createTileUpdaterForSecondaryTile(_secondaryTileID).update(tileNotification);
    }
    else {
        Windows.UI.Notifications.TileUpdateManager.createTileUpdaterForApplication().update(tileNotification);
    }
}

function ltif() {

    switch (arguments[0]) {

        case "getTileTemplateType":
            return Windows.UI.Notifications.TileTemplateType[arguments[1]];
        case "getBadgeTemplateType":
            return Windows.UI.Notifications.BadgeTemplateType[arguments[1]];
        case "getTileTemplateContent":
            return Windows.UI.Notifications.TileUpdateManager.getTemplateContent(arguments[1]);
        case "getBadgeTemplateContent":
            return Windows.UI.Notifications.BadgeUpdateManager.getTemplateContent(arguments[1]);

        case "newTileNotification":
            LiveTileNotification(arguments[1], arguments[2], arguments[3], arguments[4]);
            break;

        case "newBadgeNotification":
            var badgeNotification = new Windows.UI.Notifications.BadgeNotification(arguments[1]);
            if ((arguments[2] != null) && (arguments[2] != undefined)) {
                Windows.UI.Notifications.BadgeUpdateManager.createBadgeUpdaterForSecondaryTile(arguments[2]).update(badgeNotification);
            }
            else {
                Windows.UI.Notifications.BadgeUpdateManager.createBadgeUpdaterForApplication().update(badgeNotification);
            }
            break;

        case "clearLiveTileNotification":
            Windows.UI.Notifications.TileUpdateManager.createTileUpdaterForApplication().clear();
            break;

        case "clearLiveTileBadge":
            Windows.UI.Notifications.BadgeUpdateManager.createBadgeUpdaterForApplication().clear();
            break;

        case "enableLiveTileQueue":
            Windows.UI.Notifications.TileUpdateManager.createTileUpdaterForApplication().enableNotificationQueue(arguments[1]);
            break;

        case undefined:
        case null:
            break;
    }
}
