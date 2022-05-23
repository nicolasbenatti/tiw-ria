
(function() { // avoid variables ending up in the global scope

	// === utility functions & constants ===
	
	const REGEX_VALIDATE_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	
	function isEmailValid(email) {
		var regex = new RegExp(REGEX_VALIDATE_EMAIL);
		return regex.test(email);	
	}

	document.getElementById("loginButton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		console.log(form);
		if (form.checkValidity()) {
			makeCall("POST", 'login', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "home.html";
								break;
							case 400: // bad request
								document.getElementById("errorMessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errorMessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errorMessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});
	
	document.getElementById("signupButton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		console.log(form);
		
		// take form input
		let email = form.elements["email"].value;
		let password = form.elements["password"].value;
		let pwConfirmation = form.elements["passwordConfirmation"].value;
		
		console.log(email);
		console.log(password);
		console.log(pwConfirmation);
		
		let validEmail = (email.length != 0) ? isEmailValid(email) : true;
		let passwordsMatch = (password === pwConfirmation);
		
		if (form.checkValidity() && validEmail && passwordsMatch) {
			makeCall("POST", 'register', form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								//window.location.href = "register.html";
								document.getElementById("signupSuccess").textContent = message;
								break;
							case 400: // bad request
								document.getElementById("signupErrors").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("signupErrors").textContent = message;
								break;
							case 500: // server error
								document.getElementById("signupErrors").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
			if(!validEmail) {
				document.getElementById("signupErrors").textContent = "malformed email address";
			} else if(!passwordsMatch) {
				document.getElementById("signupErrors").textContent = "passwords don't match";
			}
		}
	});
	
})();