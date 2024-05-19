$(document).ready(function() {

	$('#loginForm').ajaxForm({
		dataType : 'json',
		beforeSubmit : function(formData, $form, options) {
			for ( var i = 0; i < formData.length; i++) {
				if (!formData[i].value) {
					alert('Please enter a value for both ID and Password');
					return false;
				}
			}
		},
		success : function(json, statusText, xhr, $form) {
			if (json.success == true) {
				var url = json.returnUrl || './';
				document.location.href = url;
			} else {
				alert(json.message);
			}
		},
		error : function(xhr) {
			alert(xhr.statusText);
		}
	});
	
	$(':input[name=id]').focus();

});