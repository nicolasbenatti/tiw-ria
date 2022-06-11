/**
 * AJAX call wrapper
 */
function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
		cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

/**
 * returns 0 if mins < 60
 */
function minsToHours(mins) {
	return Math.floor(mins / 60);
}

function hoursToMins(hours) {
	return hours * 60;
}