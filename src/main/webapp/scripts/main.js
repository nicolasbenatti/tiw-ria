
(function() {
	let pageOrchestrator = new PageOrchestrator(); // main controller
	let attempts = 0; // state of the interaction

	window.addEventListener("load", () => {
		// redirect to login
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "register.html";
		} else {
			// initialize the components
			pageOrchestrator.start();

			// display initial content
			pageOrchestrator.refresh();
		}
	}, false);

	function WelcomeMessage(usrn, messagecontainer) {
		this.username = usrn;
		this.show = function() {
			messagecontainer.textContent = " " + this.username;
		}
	}

	function HostedMeetingList(alert, listContainer, listContainerBody) {
		this.alert = alert;
		this.listContainer = listContainer;
		this.listContainerBody = listContainerBody;

		this.reset = function() {
			//this.listContainer.style.visibility = "hidden";
			this.listContainer.style.display = "none";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getHostedMeetings", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200: // OK
								var meetingToShow = JSON.parse(req.responseText);
								if (meetingToShow.length == 0) {
									self.alert.textContent = "No currently hosted meetings";
									return;
								}
								self.update(meetingToShow);
								break;
							case 403: // forbidden
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem("username");
								break;
							case 400: // bad request
							case 401: // unauthorized
							case 500: // internal server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);
		};

		this.update = function(meetingsArray) {
			var row, titleCell, dateCell, durationCell, maxCell;
			this.listContainerBody.innerHTML = ""; // empty the table body

			// build list
			var self = this;
			meetingsArray.forEach(function(meeting) {
				row = document.createElement("tr");

				titleCell = document.createElement("td");
				titleCell.textContent = meeting.title;
				titleCell.classList.add("tableCell");
				row.appendChild(titleCell);

				dateCell = document.createElement("td");
				dateCell.textContent = meeting.date;
				dateCell.classList.add("tableCell");
				row.appendChild(dateCell);

				durationCell = document.createElement("td");
				durationCell.textContent = meeting.duration;
				durationCell.classList.add("tableCell");
				row.appendChild(durationCell);

				maxCell = document.createElement("td");
				maxCell.textContent = meeting.maxParticipants;
				maxCell.classList.add("tableCell");
				row.appendChild(maxCell);

				self.listContainerBody.appendChild(row);
			});
			this.listContainer.style.removeProperty("display");
			this.listContainer.style.visibility = "visible";
		}
	}

	function AttendedMeetingList(alert, listContainer, listContainerBody) {
		this.alert = alert;
		this.listContainer = listContainer;
		this.listContainerBody = listContainerBody;

		this.reset = function() {
			//this.listContainer.style.visibility = "hidden";
			this.listContainer.style.display = "none";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getAttendedMeetings", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200: // OK
								var meetingsToShow = JSON.parse(req.responseText);
								if (meetingsToShow.length == 0) {
									self.alert.textContent = "No currently valid invitations";
									return;
								}
								self.update(meetingsToShow);
								break;
							case 403: // forbidden
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem("username");
								break;
							case 400: // bad request
							case 401: // unauthorized
							case 500: // internal server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);
		};

		this.update = function(meetingsArray) {
			var row, titleCell, dateCell, durationCell;
			this.listContainerBody.innerHTML = ""; // empty the table body

			// build list
			var self = this;
			meetingsArray.forEach(function(meeting) {
				row = document.createElement("tr");

				titleCell = document.createElement("td");
				titleCell.textContent = meeting.title;
				titleCell.classList.add("tableCell");
				row.appendChild(titleCell);

				dateCell = document.createElement("td");
				dateCell.textContent = meeting.date;
				dateCell.classList.add("tableCell");
				row.appendChild(dateCell);

				durationCell = document.createElement("td");
				let hours = minsToHours(meeting.duration);
				durationCell.textContent = meeting.duration;
				durationCell.classList.add("tableCell");
				row.appendChild(durationCell);

				self.listContainerBody.appendChild(row);
			});
			this.listContainer.style.removeProperty("display");
			this.listContainer.style.visibility = "visible";
		}
	}

	function GuestList(alert, listContainer, listContainerBody) {
		this.alert = alert;
		this.listContainer = listContainer;
		this.listContainerBody = listContainerBody;

		this.reset = function() {
			this.listContainer.style.visibility = "hidden";

			// delete all hidden input fields left by previous meeting creation attempts
			this.listContainer.querySelectorAll("input[type='hidden']").forEach(function(input) {
				input.remove();
			})
		}

		this.show = function() {
			var self = this;
			makeCall("GET", "getUsers", null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200: // OK
								var users = JSON.parse(req.responseText);
								if (users.length == 0) {
									self.alert.textContent = "No available users";
									return;
								}
								self.update(users);
								break;
							case 403: // forbidden
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem("username");
								break;
							case 400: // bad request
							case 401: // unauthorized
							case 500: // internal server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);
		};

		this.update = function(usersArray) {
			// empty the body
			this.listContainerBody.innerHTML = "";
			var self = this;
			usersArray.forEach(function(user) {
				row = document.createElement("div");
				row.classList.add("padded");

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

				self.listContainerBody.appendChild(row);
			});
			this.listContainer.style.visibility = "visible";
		}
	}

	function Wizard(wizardId, modalId, alert, modalAlert) {
		var now = new Date();
		var formattedDate = now.toISOString().substring(0, 16);
		this.wizard = wizardId;
		this.modal = modalId;
		this.alert = alert;
		this.modalAlert = modalAlert;

		this.wizard.querySelector("input[type='datetime-local']").setAttribute("min", formattedDate);

		this.registerEvents = function(orchestrator) {
			var self = this;

			this.modal.querySelector("input[type='button'].cancel").addEventListener("click", (e) => {
				this.modal.querySelectorAll("input[type='checkbox']").forEach(function(checkbox) {
					checkbox.checked = false;
				})
			});

			this.wizard.querySelector("input[type='button'].host").addEventListener("click", (e) => {
				let form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", "createMeeting", form,
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								let message = req.responseText;
								switch (req.status) {
									case 200: // OK
										var maxParticipants = form.elements["maxParticipants"].value;
										var meetingDuration = form.elements["meetingDuration"].value;
										var meetingDateTime = form.elements["meetingDateTime"].value;
										var meetingTitle = form.elements["meetingTitle"].value;

										var invitationForm = document.getElementById("invitationForm");

										// add hidden inputs containing data of the meeting eligible for
										// insertion, alongside with the host id, in order to keep data available
										// while passing through multiple forms

										var maxParticipantsInput = document.createElement("input");
										maxParticipantsInput.setAttribute("type", "hidden");
										maxParticipantsInput.setAttribute("name", "maxParticipants");
										maxParticipantsInput.setAttribute("value", maxParticipants);

										var durationInput = document.createElement("input");
										durationInput.setAttribute("type", "hidden");
										durationInput.setAttribute("name", "meetingDuration ");
										durationInput.setAttribute("value", meetingDuration);

										var dateTimeInput = document.createElement("input");
										dateTimeInput.setAttribute("type", "hidden");
										dateTimeInput.setAttribute("name", "meetingDateTime");
										dateTimeInput.setAttribute("value", meetingDateTime);

										var titleInput = document.createElement("input");
										titleInput.setAttribute("type", "hidden");
										titleInput.setAttribute("name", "meetingTitle");
										titleInput.setAttribute("value", meetingTitle);

										invitationForm.appendChild(maxParticipantsInput);
										invitationForm.appendChild(durationInput);
										invitationForm.appendChild(dateTimeInput);
										invitationForm.appendChild(titleInput);

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

			this.modal.querySelector("input[type='button'].invite").addEventListener("click", (e) => {
				let form = e.target.closest("form");
				if (form.checkValidity()) {
					let maxParticipants = form.elements["maxParticipants"].value;
					var numberOfParticipants = 0, numberOfDeselections;

					for (let i = 0; i < form.elements["users"].length; i++) {
						if (form.elements["users"][i].checked) {
							numberOfParticipants++;
						}
					}

					attempts++;
					if (numberOfParticipants > maxParticipants) {
						numberOfDeselections = numberOfParticipants - maxParticipants;

						if (attempts >= 3) {
							attempts = 0;
							// close the modal
							this.modal.style.display = "none";

							// refresh the view
							orchestrator.refresh();
							this.alert.textContent = "Three attempts to host a meeting which exceeds the maximum guest limit, the meeting will not be created";
						}
						else
							this.modalAlert.textContent = "Too many selected users, deselect at least " + numberOfDeselections;
					}
					else {
						// reset attempts counter
						attempts = 0;

						// insert the newly created meeting, the host and the invitations in the DB
						makeCall("POST", "inviteToMeeting", form,
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									let message = req.responseText;
									switch (req.status) {
										case 200: // OK
											self.modal.style.display = "none";
											// refresh the view
											orchestrator.refresh();
											break;
										case 400: // bad request
											document.getElementById("invitationErrors").textContent = message;
											break;
										case 401: // unauthorized
											document.getElementById("invitationErrors").textContent = message;
											break;
										case 500: // server error
											document.getElementById("invitationErrors").textContent = message;
											break;
									}
								}
							}, false);
					}
				} else {
					form.reportValidity();
				}
			});
		}

		this.reset = function() {
			wizardId.reset();
		}
	}

	function PageOrchestrator() {
		var hostingsAlert = document.getElementById("hostedMeetingsAlert");
		var attendancesAlert = document.getElementById("attendancesMeetingsAlert");
		var hostAlert = document.getElementById("meetingErrorMessage");
		var inviteAlert = document.getElementById("invitationErrors");

		this.start = function() {
			
			welcomeMessage = new WelcomeMessage(
				sessionStorage.getItem("username"),
				document.getElementById("username")
			)
			welcomeMessage.show();
			
			hostedList = new HostedMeetingList(
				hostingsAlert,
				document.getElementById("hostingsTable"),
				document.getElementById("hostingsTableBody")
			);

			attendedList = new AttendedMeetingList(
				attendancesAlert,
				document.getElementById("attendancesTable"),
				document.getElementById("attendancesTableBody")
			);

			guestList = new GuestList(
				document.getElementById("invitationErrors"),
				document.getElementById("invitationForm"),
				document.getElementById("guestList")
			);

			wizard = new Wizard(
				document.getElementById("createMeetingForm"),
				document.getElementById("invitationModal"),
				document.getElementById("meetingErrorMessage"),
				document.getElementById("invitationErrors")
			);

			// pass the orchestrator so the wizard can refresh the view after inserting a meeting
			wizard.registerEvents(this);

			document.getElementById("logoutButton").addEventListener("click", () => {
				window.sessionStorage.removeItem("username");
			});
		};

		this.refresh = function() {
			hostingsAlert.textContent = "";
			attendancesAlert.textContent = "";
			hostAlert.textContent = "";
			inviteAlert.textContent = "";

			hostedList.reset();
			attendedList.reset();
			guestList.reset();
			wizard.reset();

			hostedList.show();
			attendedList.show();
			guestList.show();
		};
	}
})();