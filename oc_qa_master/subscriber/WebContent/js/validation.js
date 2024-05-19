
function ValidateEmail(){
	var flag = true;
	var errmsg = "";
	var innerhtmlelement = document.getElementById('errorDivId').innerHTML;
	document.getElementById('errorDivId').innerHTML="";
	for(i=0;i<document.webForm.elements.length;i++ ){
	
	var name = document.webForm.elements[i].name;
	var token = name.split("_");
	if(token[1]=="true"){
		if(trimAll(document.webForm.elements[i].value)==""){
			errmsg= errmsg+"Please enter some value."+"<br/>";
			document.webForm.elements[i].focus();
			
		}
		else if(!(notEmpty(document.webForm.elements[i]))){
			errmsg= errmsg+"value should not be empty,Please enter some value."+"<br/>";
			document.webForm.elements[i].focus();
			continue;
		}
	
	}
	if(token[3]=="number"){
		if(!(isNumeric(document.webForm.elements[i] ))){
			errmsg= errmsg+"value should  be number only."+"<br/>";
			document.webForm.elements[i].focus();
			continue;
		}
	
	}else if(token[3]=="date"){
		if(!(isDate(document.webForm.elements[i].value))){
			errmsg= errmsg+"value should  be date of mm/dd/yyyy"+"<br/>";
			document.webForm.elements[i].focus();
			continue;
		
		}
	
	
	}else if(token[3]=="eml"){
		if(trimAll(document.webForm.elements[i].value)==""){
			errmsg= errmsg+"enter emailId"+"<br/>";
        	
         	//alert();
         	document.webForm.elements[i].focus();
         	continue;
		}
		else if(!(emailValidator(document.webForm.elements[i]))){
			errmsg= errmsg+"enter valid  emailId"+"<br/>";
			document.webForm.elements[i].focus();
			continue;
		}
	
	}else if(token[3]=="boolean"){
		if(!(boolValidator(document.webForm.elements[i]))){
			errmsg= errmsg+"enter either true or false only  emailId"+"<br/>";
			document.webForm.elements[i].focus();
			continue;
		}
	}//else if
	
	}//for

	if(errmsg.length>0){
	flag=false;
	}
	document.getElementById('errorDivId').innerHTML = errmsg;
return flag;
}//

function clearText(txtcomp){

txtcomp.value="";

}



		function trimAll(str){
		
			while(str.substring(0,1) == ' '){
			
				str = str.substring(1, str.length); 
			}
			while (str.substring(str.length-1, str.length) == ' '){
			
			str = str.substring(0,str.length-1);
			
			}
			return str;
		
		
		
		}
		
		
		
        

function isNumeric(elem){
	var numericExpression = /^[0-9]+$/;
	if(elem.value.match(numericExpression)){
		return true;
	}else{
		//innerhtmlelement=innerhtmlelement+"Please enter number type values only."+"<br/>";
		elem.focus();
		return false;
	}
}
function isAlphabet(elem){
	var alphaExp = /^[a-zA-Z]+$/;
	if(elem.value.match(alphaExp)){
		return true;
	}else{
		//document.getElementById('errorDivId').innerHTML=document.getElementById('errorDivId').innerHTML+"Please enter alphabets only."+"<br/>";
		elem.focus();
		return false;
	}
}
function emailValidator(elem){
	var emailExp =  /^\w+([-.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	if(elem.value.match(emailExp)){
		return true;
	}else{
		//document.getElementById('errorDivId').innerHTML = document.getElementById('errorDivId').innerHTML+"Please enter valid email id."+"<br/>";
		elem.focus();
		return false;
	}
}
function notEmpty(elem){
	if(elem.value.length == 0){
		//alert(helperMsg);
		elem.focus(); // set the focus to this input
		return false;
	}
	return true;
}
function boolValidator(elem){

	if((elem.value=="true")||(elem.value=="false")){
		return true;
	}else{
		//document.getElementById('errorDivId').innerHTML = document.getElementById('errorDivId').innerHTML+"Please enter either true or false."+"<br/>";
		elem.focus();
		return false;
	}

}




function isInteger(s){
	var i;
    for (i = 0; i < s.length; i++){   
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}

function stripCharsInBag(s, bag){
	var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function daysInFebruary (year){
	// February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) {
	for (var i = 1; i <= n; i++) {
		this[i] = 31
		if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
		if (i==2) {this[i] = 29}
   } 
   return this;
}
var dtCh= "/";
function isDate(dtStr){

	var daysInMonth = DaysArray(12);
	var pos1=dtStr.indexOf(dtCh);
	var pos2=dtStr.indexOf(dtCh,pos1+1);
	//alert('pos2 '+pos2);
	var strMonth=dtStr.substring(pos1+1,pos2);
	//alert('strMonth '+strMonth);
	var strDay=dtStr.substring(0,pos1);
	//alert('strDay '+strDay);
	var strYear=dtStr.substring(pos2+1);
	strYr=strYear;
	if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
	if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
	for (var i = 1; i <= 3; i++) {
		if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
	}
	month=parseInt(strMonth);
	day=parseInt(strDay);
	year=parseInt(strYr);
	if (pos1==-1 || pos2==-1){
		//alert("The date format should be : mm/dd/yyyy");
		//document.getElementById('errorDivId').innerHTML=document.getElementById('errorDivId').innerHTML+"The date format should be : mm/dd/yyyy"+"<br/>"
		return false;
	}
	if (strMonth.length<1 || month<1 || month>12){
		alert("Please enter a valid month");
		return false;
	}
	if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
		alert("Please enter a valid day");
		return false;
	}
	if (strYear.length != 4 || year==0 ){
		alert("Please enter a valid 4 digit year");
		return false;
	}
	if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
		alert("Please enter a valid date");
		return false;
	}
	return true;
}
	
	
	
