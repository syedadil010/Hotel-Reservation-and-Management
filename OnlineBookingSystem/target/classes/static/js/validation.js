var validateCustomerRegistration = function() {
	pw = $('#password').val();
	confirm = $('#confirm').val();
	
	if(pw == confirm) {
		return true;
	} else {
		return false;
	}
};

var validateConfirmPassword = function() {
	pw = $('#password').val();
	confirm = $('#confirm').val();
	console.log(confirm + "  " + pw);
	if(pw == confirm) {
		$('#confirm-error').addClass('hidden');
	} else {
		$('#confirm-error').removeClass('hidden');
	}
};

var validateEmployeeServiceCheckboxes = function() {
	var checked = $('.checkbox-group.required :checkbox:checked').length;
	if(checked > 0) {
		$('#service-error').addClass('hidden');
		return true;
	} else {
		$('#service-error').removeClass('hidden');
		return false;
	}
};