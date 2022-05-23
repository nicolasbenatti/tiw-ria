
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

	let attempts = 0;
	document.getElementById("inviteButton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		console.log(form);
		if (form.checkValidity()) {
			console.log("YOUR SELECTION", form.elements["users"]);
			var maxP = form.elements["maxParticipants"].value;
			var numberOfParticipants = 0;
			for(let i = 0; i < form.elements["users"].length; i++) {
				if(form.elements["users"][i].checked) {
					numberOfParticipants++;
				}
			}

			attempts++;
			console.log("YOU HAVE DONE ", attempts, " INVITATION ATTEMPTS");
			if (numberOfParticipants > maxP) {
				numberOfDeselection = numberOfParticipants - maxP;
				
				var alert = document.getElementById("tooManyGuestsAlert");
				alert.textContent = "DESELEZIONARE " + numberOfDeselection + " UTENTI";

				if (attempts >= 3)
					console.log("GO HOME");
			}
			else {
				//TUTTO BENE -> inserisco riunione nel DB
				attempts = 0;
				makeCall("POST", "inviteToMeeting", form,
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							let message = x.responseText;
							switch (x.status) {
								case 200:
									console.log("ALL GOOD");
									var modal = document.getElementById("invitationModal");
									modal.style.display = "none";
									break;
								case 400: // bad request
									document.getElementById("tooManyGuestsAlert").textContent = "Server: " + message;
									break;
								case 401: // unauthorized
									document.getElementById("tooManyGuestsAlert").textContent = "Server: " + message;
									break;
								case 500: // server error
									document.getElementById("tooManyGuestsAlert").textContent = "Server: " + message;
									break;
							}
						}
				}, false);
			}
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("hostButton").addEventListener('click', (e) => {
		let form = e.target.closest("form");
		console.log(form);
		if (form.checkValidity()) {
			makeCall("POST", "createMeeting", form,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						let message = x.responseText;
						switch (x.status) {
							case 200:
								console.log("ALL GOOD");
								console.log(form);
								var maxP = form.elements["maxParticipants"].value;
								var meetingDuration = form.elements["meetingDuration"].value;
								var meetingTime = form.elements["meetingTime"].value;
								var meetingDate = form.elements["meetingDate"].value;
								var meetingTitle = form.elements["meetingTitle"].value;

								console.log(maxP, meetingDuration, meetingTitle, meetingDate, meetingTime);

								var invitationForm = document.getElementById("invitationForm");

								var input = document.createElement("input");
								input.setAttribute("type", "hidden");
								input.setAttribute("name", "maxParticipants");
								input.setAttribute("value", maxP);

								var input2 = document.createElement("input");
								input2.setAttribute("type", "hidden");
								input2.setAttribute("name", "meetingDuration ");
								input2.setAttribute("value", meetingDuration);

								var input3 = document.createElement("input");
								input3.setAttribute("type", "hidden");
								input3.setAttribute("name", "meetingTime");
								input3.setAttribute("value", meetingTime);

								var input4 = document.createElement("input");
								input4.setAttribute("type", "hidden");
								input4.setAttribute("name", "meetingDate");
								input4.setAttribute("value", meetingDate);

								var input5 = document.createElement("input");
								input5.setAttribute("type", "hidden");
								input5.setAttribute("name", "meetingTitle");
								input5.setAttribute("value", meetingTitle);

								invitationForm.appendChild(input);
								invitationForm.appendChild(input2);
								invitationForm.appendChild(input3);
								invitationForm.appendChild(input4);
								invitationForm.appendChild(input5);

								var modal = document.getElementById("invitationModal");
								modal.style.display = "block";
								break;
							case 400: // bad request
								document.getElementById("meetingErrorMessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("meetingErrorMessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("meetingErrorMessage").textContent = message;
								break;
						}
					}
				}, false);
		} else {
			form.reportValidity();
		}
	});

})();