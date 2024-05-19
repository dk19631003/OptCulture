function getInternetExplorerVersion()
// Returns the version of Internet Explorer or a -1
// (indicating the use of another browser).
{
  var rv = -1; // Return value assumes failure.
  if (navigator.appName == 'Microsoft Internet Explorer')
  {
    var ua = navigator.userAgent;
    var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
    if (re.exec(ua) != null)
      rv = parseFloat( RegExp.$1 );
  }
  return rv;
}
function checkVersion()
{
  var msg = "You're not using Internet Explorer.";
  var ver = getInternetExplorerVersion();

  if ( ver > -1 )
  {
    if ( ver == 6.0 ) 
      document.write("<link REL='stylesheet' HREF='" +gTomcatUrl+ "/css/zk-ie6.css' TYPE='text/css'>");
  }
}
checkVersion();
/* -- End of checking borwer type and version -- */

function parentSrc(src){
	parent.window.location = gAppUrl + '/' + src;
}



function changecss(theClass,element,value) {
	 var cssRules;

	 var added = false;
	 for (var S = 0; S < document.styleSheets.length; S++){

    if (document.styleSheets[S]['rules']) {
	  cssRules = 'rules';
	 } else if (document.styleSheets[S]['cssRules']) {
	  cssRules = 'cssRules';
	 } else {
	  //no rules found... browser unknown
	 }

	  for (var R = 0; R < document.styleSheets[S][cssRules].length; R++) {
	   if (document.styleSheets[S][cssRules][R].selectorText == theClass) {
	    if(document.styleSheets[S][cssRules][R].style[element]){
	    document.styleSheets[S][cssRules][R].style[element] = value;
	    added=true;
		break;
	    }
	   }
	  }
	  var i=0;
	  if(!added){
	  if(document.styleSheets[S].insertRule){
			  document.styleSheets[S].insertRule(theClass+' { '+element+': '+value+'; }',document.styleSheets[S][cssRules].length);
			} else if (document.styleSheets[S].addRule) {
				document.styleSheets[S].addRule(theClass,element+': '+value+';');
			}
	  }
	 }
	}



	function getBrowserProps(option) {
	  var myWidth = 0, myHeight = 0;
	  if( typeof( window.innerWidth ) == 'number' ) {
	    //Non-IE
	    myWidth = window.innerWidth;
	    myHeight = window.innerHeight;
	  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
	    //IE 6+ in 'standards compliant mode'
	    myWidth = document.documentElement.clientWidth;
	    myHeight = document.documentElement.clientHeight;
	  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
	    //IE 4 compatible
	    myWidth = document.body.clientWidth;
	    myHeight = document.body.clientHeight;
	  }
	  if(option == 0) {
	  	return myWidth;
	  }
	  if(option == 1) {
	  	return myHeight;
	  }
	  return -1;
	}



var win = null;

 function previewByUrl(src){
	win = window.open(src,"previewWin","fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=700,height=500,left=50,top=20");
	win.focus();
//	setTimeout(function(){refresh();}, 150);
 }
/*function refresh()
{
	win.location.reload();
}*/

 function previewHTML(htmlTbId){
	win = window.open("","previewWin","fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=700,height=500,left=50,top=20");
	win.document.write("<html><head><title>Preview</title></head><body><a href='javascript:self.close();'>Close</a><br/><br/>");
	win.document.write(htmlTbId.getValue());
	win.document.write("</body></html>");
	win.focus();
 }
 
 function preview(content){
	win = window.open("","previewWin","fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=700,height=500,left=50,top=20");
	win.document.write("<html><head><title>Preview</title></head><body><a href='javascript:self.close();'>Close</a><br/><br/>");
	win.document.write(content);
	win.document.write("</body></html>");
	win.focus();
 }
 
function previewWin(url){
	win = window.open(url,"Preview","fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=no,resizable=no,directories=no,location=no,left=10,top=10");
	win.focus();
}

//var iframe = parent.document.getElementById("rmIFrameId");
function setIframeSize(size){
	/*
	if(iframe != undefined){
		iframe.height = size;
	}
	*/
}

function scrollUp(){
	parent.window.scrollTo(0,0);
}

/* ------- Trim ------- */
function trim(str) {
	return ltrim(rtrim(str));
}
 
function ltrim(str) {
	return str.replace(new RegExp("^[\\s]+", "g"), "");
}
 
function rtrim(str) {
	return str.replace(new RegExp("[\\s]+$", "g"), "");
}
/* ------- End of Trim ------- */

var pEditorOpen = false;
var pEditorDivId = 'editorDivId';

function checkPlainEditor(){
	if(pEditorOpen){
		closeEditor(pEditorDivId);
		pEditorOpen = false;
	}
}

function setEditorId(id){
	pEditorDivId = id;
}

function changeRepTabMenu(id,selId,totTabs){
	var tab;
	
	for(i=1;i<=totTabs;i++){
		tab = document.getElementById(id+i);
		tab.className = 'tabmenu';
	}
	
	tab = document.getElementById(id+selId);
	tab.className = 'tabmenu tabmenusel';
}

function setUserName(userName) {
	alert(gUser + "  " + userName);
	gUser = userName;
	alert(gUser);
}

/*  inserts specified value into the textarea at the cursor position */
function insertAtCursor(myField, myValue) {
	//IE support
		myField.focus();
	if (document.selection) {
		sel = document.selection.createRange();
		sel.text = myValue;
	}
	//MOZILLA/NETSCAPE support
	else if (myField.selectionStart || myField.selectionStart == '0') {
		var startPos = myField.selectionStart;
		var endPos = myField.selectionEnd;
		myField.value = myField.value.substring(0, startPos)
		+ myValue
		+ myField.value.substring(endPos, myField.value.length);
	} else {
		myField.value += myValue;
	}
}



/* Validates emails string */
function validateEmail(email) {
	var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	return reg.test(email);
}