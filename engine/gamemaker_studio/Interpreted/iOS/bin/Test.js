var target = UIATarget.localTarget();
UIATarget.onAlert = function onAlert(alert) {
	return true;
}
// this should make game last for a year in instruments... that should be long enough...
target.delay( 31536000 )