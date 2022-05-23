(function() {
	var modal = document.getElementById("invitationModal");
	
	document.getElementById("hostButton").addEventListener('click', (e) => {
		modal.style.display = "block";
	})

	document.getElementsByClassName("close")[0].addEventListener('click', (e) => {
		modal.style.display = "none";
	});

	window.addEventListener('click', (e) => {
		if (e.target == modal) {
			modal.style.display = "none";
		}
	});
})();