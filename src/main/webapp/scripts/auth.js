
(function() {
	// === utility functions & constants ===
	const REGEX_VALIDATE_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	
	function isEmailValid(email) {
		var regex = new RegExp(REGEX_VALIDATE_EMAIL);
		return regex.test(email);	
	}
	
	function isPasswordValid(password) {
		return password.length >= 8;
	}

	document.getElementById("loginButton").addEventListener("click", (e) => {
		var form = e.target.closest("form");

		if (form.checkValidity()) {
			makeCall("POST", "login", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								var user = JSON.parse(message);
								sessionStorage.setItem("username", user.username);
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
				}, false);
		} else {
			form.reportValidity();
		}
	});
	
	document.getElementById("signupButton").addEventListener("click", (e) => {
		var form = e.target.closest("form");
		
		// take form input
		let email = form.elements["email"].value;
		let password = form.elements["password"].value;
		let pwConfirmation = form.elements["passwordConfirmation"].value;

		// null or empty fields will be signaled by the server
		let validEmail = isEmailValid(email);
		let validPassword = isPasswordValid(password);
		let passwordsMatch = (password === pwConfirmation);
		
		if (form.checkValidity() && validEmail && validPassword && passwordsMatch) {
			makeCall("POST", "register", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem("username", message);
								document.getElementById("signupSuccess").textContent = message;
								document.getElementById("signupErrors").textContent = "";
								document.getElementById("duplicateUsernameError").textContent = "";
								document.getElementById("invalidEmailError").textContent = "";
								document.getElementById("passwordMismatchError").textContent = "";
								break;
							case 400: // bad request
								document.getElementById("signupSuccess").textContent = "";
								document.getElementById("signupErrors").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("signupSuccess").textContent = "";
								document.getElementById("signupErrors").textContent = message;
								break;
							case 500: // internal server error
								var errorBean = JSON.parse(message);
								document.getElementById("signupSuccess").textContent = "";
								document.getElementById("signupErrors").textContent = errorBean.missingEntries;
								document.getElementById("duplicateUsernameError").textContent = errorBean.notUniqueUsername;
								document.getElementById("invalidEmailError").textContent = errorBean.invalidEmail;
								document.getElementById("passwordMismatchError").textContent = errorBean.passwordMismatch;
								break;
						}
					}
				}, false);
		} else if (!validEmail) {
			document.getElementById("invalidEmailError").textContent = "malformed email address";
		} else if(!validPassword) {
			document.getElementById("passwordMismatchError").textContent = "password missing or too short (at least 8 characters)";
		} else if(!passwordsMatch) {
			document.getElementById("passwordMismatchError").textContent = "passwords don't match";
		} else {
			form.reportValidity();
		}
	});
})();