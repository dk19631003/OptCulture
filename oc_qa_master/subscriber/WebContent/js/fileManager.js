$.getScript("https://app-rsrc.getbee.io/plugin/BeePlugin.js", function() {
	var endpoint = "https://bee-auth.getbee.io/apiauth";
	var data = beekey;
	var req = new XMLHttpRequest();
	req.open('post', endpoint, true);
	req.onreadystatechange = function() {
		if (req.readyState === 4 && req.status === 200) {
			var token = req.responseText;
			BeePlugin.create(JSON.parse(token), beeConfig, function(beePluginInstance) {
				bee = beePluginInstance;
				bee.start();
			});
		}
	};
	if (data) {
		req.setRequestHeader('Content-type','application/x-www-form-urlencoded');
		req.send(data);
	}
	
	var beeConfig;
	var requestFrom = '';
	if((requestFrom == '' || requestFrom == 'undefined')){
		beeConfig = {
			uid : namevalue,
			container : containerId,
			language: 'en-US',

			      onError: function(errorMessage) { 
			        console.log('onError ', errorMessage);
			       }
			    };
	}
});
