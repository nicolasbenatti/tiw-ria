
(function() { // create a scope

	let pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "register.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	function HostedMeetingList(alert, listcontainer, listcontainerbody) {
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getHostedMeetings", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						if (req.status == 200) {
							var meetingToShow = JSON.parse(req.responseText);
							if (meetingToShow.length == 0) {
								self.alert.textContent = "No Hosted Meeting yet!";
								return;
							}
							self.update(meetingToShow); // self visible by closure
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		this.update = fillMeetingTable;
	}

	function AttendedMeetingList(alert, listcontainer, listcontainerbody) {
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getAttendedMeetings", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						if (req.status == 200) {
							var meetingsToShow = JSON.parse(req.responseText);
							if (meetingsToShow.length == 0) {
								self.alert.textContent = "No Attendances yet!";
								return;
							}
							self.update(meetingsToShow); // self visible by closure
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		this.update = fillMeetingTable;
	}

	function GuestList(listcontainer, listcontainerbody) {
		this.alert = alert;
		this.listcontainer = listcontainer;
		this.listcontainerbody = listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getUsers", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						if (req.status == 200) {
							var users = JSON.parse(req.responseText);
							if (users.length == 0) {
								self.alert.textContent = "No registered users";
								return;
							}
							self.update(users); // self visible by closure
						} else if (req.status == 403) { // forbidden
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		// fill the list with user data
		this.update = function(usersArray) {
			this.listcontainerbody.innerHTML = ""; // empty the body
			var self = this;

			usersArray.forEach(function(user) {
				row = document.createElement("div");

				checkbox = document.createElement("input");
				checkbox.setAttribute("type", "checkbox");
				checkbox.setAttribute("name", "users");
				checkbox.setAttribute("value", user.id);
				checkbox.setAttribute("id", user.id);

				label = document.createElement("label");
				label.textContent = user.username;
				label.setAttribute("for", user.id);

				row.appendChild(checkbox);
				row.appendChild(label);

				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";
		}
	}

	function PageOrchestrator() {
		var hostingsAlertContainer = document.getElementById("hostedMeetingsAlert");
		var attendancesAlertContainer = document.getElementById("attendancesMeetingsAlert");

		this.start = function() {
			hostedList = new HostedMeetingList(
				hostingsAlertContainer,
				document.getElementById("hostingsTable"),
				document.getElementById("hostingsTableBody"));

			attendedList = new AttendedMeetingList(
				attendancesAlertContainer,
				document.getElementById("attendancesTable"),
				document.getElementById("attendancesTableBody"));

			guestList = new GuestList(
				document.getElementById("invitationForm"),
				document.getElementById("guestList")
			);

			/*wizard = new Wizard(document.getElementById("id_createmissionform"), alertContainer);
			wizard.registerEvents(this);*/  // the orchestrator passes itself --this-- so that the wizard can call its refresh function after creating a mission

			/*document.querySelector("a[href='Logout']").addEventListener('click', () => {
			window.sessionStorage.removeItem('username');
			})*/
		};

		this.refresh = function(currentMission) { // currentMission initially null at start
			hostingsAlertContainer.textContent = ""; // not null after creation of status change
			attendancesAlertContainer.textContent = "";
			hostedList.reset();
			attendedList.reset();
			guestList.reset();
			hostedList.show(); // closure preserves visibility of this
			attendedList.show();
			guestList.show();
			//wizard.reset();
		};
	}

	// fills the table with meeting data
	function fillMeetingTable(arrayMeetings) {
		var elem, i, row, titlecell, datecell, durationcell, maxcell;
		this.listcontainerbody.innerHTML = ""; // empty the table body
		// build updated list
		var self = this;
		arrayMeetings.forEach(function(meeting) { // self visible here, not this
			row = document.createElement("tr");

			titlecell = document.createElement("td");
			titlecell.textContent = meeting.title;
			row.appendChild(titlecell);

			datecell = document.createElement("td");
			datecell.textContent = meeting.date;
			row.appendChild(datecell);

			durationcell = document.createElement("td");
			durationcell.textContent = meeting.duration;
			row.appendChild(durationcell);

			maxcell = document.createElement("td");
			maxcell.textContent = meeting.maxParticipants;
			row.appendChild(maxcell);

			self.listcontainerbody.appendChild(row);
		});
		this.listcontainer.style.visibility = "visible";
	}


	document.getElementById("inviteButton").addEventListener('click', (e) => {
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

})();