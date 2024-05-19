var gAppName = 'subscriber';
//var gAppUrl = "http://localhost:8080";
var gAppUrl = "";
var gRmUrl = gAppUrl + "/"+gAppName;
var gTomcatUrl = gAppUrl + "/" + gAppName;
var gUser = undefined;
var mainScrWidth = 1000; 

function homePage(){
	if(gAppUrl != undefined){
		window.location = gAppUrl;
	}
}
