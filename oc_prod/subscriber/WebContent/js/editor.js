

var blkBdrSize;		 


/*
tinyMCE_GZ.init({
	theme : "advanced",
	plugins : "safari,pagebreak,table,layer,save,style,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,autoresize",
	languages : 'en',
	disk_cache : true,
	debug : false
});
*/





function myFileBrowser (field_name, url, type, win) {

    // alert("Field_Name: " + field_name + "nURL: " + url + "nType: " + type + "nWin: " + win); // debug/testing

   /*  If you work with sessions in PHP and your client doesn't accept cookies you might need to carry
       the session name and session ID in the request string (can look like this: "?PHPSESSID=88p0n70s9dsknra96qhuk6etm5").
       These lines of code extract the necessary parameters and add them back to the filebrowser URL again. 

    var cmsURL = window.location.toString();    // script URL - use an absolute path!
    if (cmsURL.indexOf("?") < 0) {
        //add the type as the only query parameter
        cmsURL = cmsURL + "?type=" + type;
    }
    else {
        //add the type as an additional query parameter
        // (PHP session ID is now included if there is one at all)
        cmsURL = cmsURL + "&type=" + type;
    } */
     
     var cmsURL = gRmUrl+ '/zul/gallery/ImageLibrary.zul'; 

    tinyMCE.activeEditor.windowManager.open({
        file : cmsURL,
        title : 'My Gallery Browser',
        width : 470,  // Your dimensions may differ - toy around with them!
        height : 300,
        resizable : "yes",
        inline : "yes",  // This parameter only has an effect if you use the inlinepopups plugin!
        close_previous : "no"
    }, {
        window : win,
        input : field_name
    });
    return false;
  }



var divId = "";
var isOpen = false;

function toggleEditor() {
	//alert('toggle ..');
	
	
	if(divId != ""){
		
		if (!tinyMCE.getInstanceById(divId)){
			
			//document.getElementById("HelpTableId").style.visibility = 'hidden';
			tmce = tinyMCE.execCommand('mceAddControl', false, divId);
			
			//Load a script using a unique instance of the script loader
		
			
			isOpen = true;
		}
		else{
			tinyMCE.execCommand('mceRemoveControl', false, divId);
			//document.getElementById("HelpTableId").style.visibility = 'visible';
			isOpen = false;
			//var myInput = document.getElementsByTagName("input");
			//alert(myInput.length + " ::::::::: "  +  divId);
			//alert(divId);
			/*for (i=0;i<myInput.length;i++){
				var anElement = myInput[i];
				if(anElement.type == 'hidden' && anElement.name==divId){
					//alert("Got it :"+ anElement.type + "anElement.name="+ anElement.name);
					(anElement.parentNode).removeChild(anElement);
				}
			}*/
		}
	} 
	
}



function previewEmail() {
  
	var temp3 = $("#rootDivId_ifr").contents().find('#addDelBtnDiv').remove();
	//temp3.hide();
	$('div#mainDivId').append(temp3);

	//$('div#rootDivId').value = $("#rootDivId_ifr").contents();
	
	if($('div#rootDivId').tinymce() != undefined) {
		$('div#rootDivId').tinymce().remove();
	}
	
   //previewWin();
}

function save(htmlStuffId) {
    
	previewEmail();
	//var scriptDivObj  =  document.getElementById("scriptDiv");
	
	htmlStuffId.value = "<div id='rootDivId'>" + document.getElementById("rootDivId").innerHTML + "</div>";//<div id='scriptDiv'>" + scriptDivObj.innerHTML + "</div>";
	htmlStuffId.fire('onChange',{value:htmlStuffId.value},{toServer:true},0);
 }

function autoSave(htmlStuffAId) {
	
	var temp3 = $("#rootDivId_ifr").contents().find('#addDelBtnDiv').remove();
	//temp3.hide();
	$('div#mainDivId').append(temp3);
	
	var ed = tinymce.activeEditor;
	if (ed.isDirty())
    {
        ed.save();
    }
	htmlStuffAId.value = "<div id='rootDivId'>" + document.getElementById("rootDivId").innerHTML + "</div>";//<div id='scriptDiv'>" + scriptDivObj.innerHTML + "</div>";
	htmlStuffAId.fire('onChange',{value:htmlStuffAId.value},{toServer:true},0);
	
 }

function saveInMyTemplates(name,htmlStuffId) {
   if(isOpen){
		toggleEditor();
   }
	
   var scriptDivObj  =  document.getElementById("scriptDiv");
   htmlStuffId.value = "<div id='rootDivId'>" + document.getElementById("rootDivId").innerHTML + "</div><div id='scriptDiv'>" + scriptDivObj.innerHTML + "</div>";
   htmlStuffId.fire('onChange',{value:htmlStuffId.value},{toServer:true},0);
 }

function deleteBlock(){
	if(!isOpen){
		alert("select a block to delete");
		return false;
	}
	if(divId != ""){
		var confirmDelete=confirm("Are you sure you want to delete this block?");
		var delDiv = document.getElementById(divId);
		if(!confirmDelete){
			return false;
		}
		toggleEditor();
		delDiv.parentNode.removeChild(delDiv);
		divId = "";
	}else{
		alert("select a block to delete");
	}
}

function addNewBlock(blockId,blockdata) {
		//previewEmail();
		
		//alert('blockId :'+ blockId +' : '+ 'blockdata :'+ blockdata);
		
		//var htmlStuff = document.getElementById(blockId).innerHTML;
		var hAddDiv = blockdata;
		var tmceDiv = addEditorEvent(hAddDiv);
		var $blockDivId = $('#rootDivId_ifr').contents().find('#'+blockId);
		//alert('$blockDivId ....2'+ $blockDivId.parent());
		//alert('--1--'+ $blockDivId.attr('name') + ' : ' + $blockDivId + ' div val : '+ $blockDivId.val() + ' div len' + $blockDivId.length + ' parent :'+ $blockDivId.parent());
		
		if($blockDivId.val() == undefined) {	
			//alert('--2--'+ $blockDivId.val());
			$blockDivId = $('div#'+blockId);
		}	
		
		//alert('tmceDiv:'+tmceDiv);	
		$blockDivId.append(tmceDiv);
}

//var TmceHtmlId = 0;
var addDivId = "";
function addEditorEvent(stuff){
	var ct= new Date();
	var id = "" + ct.getMonth() + ct.getDate() + ct.getHours() + ct.getMinutes() + ct.getSeconds();
	//TmceHtmlId = TmceHtmlId + 1;
	addDivId = 'TMCEdivId' + id;
	var div = "<div name=\"TMCEeditableDiv\" style=\"padding-top:5px;position:relative\" id='TMCEdivId" + id + "' ondblclick=\"loadTinyMCE('TMCEdivId" + id + "');\" title=\"Double click here to edit this block\" onmouseover=\"javascript:parent.hlBlock('TMCEdivId" + id + "');\" onmouseout=\"javascript:parent.restoreBlock('TMCEdivId" + id + "');\"> ";
	var tmceDiv = div + stuff + "</div>";
	return tmceDiv;
 }

function undoAddBlock(){
	
	if(addDivId == ""){
		return;
	}
	
	var confirmDelete=confirm("Are you sure you want to delete the added block?");
	var delDiv = document.getElementById(addDivId);
	
	if(!confirmDelete){
		return false;
	}
	
	delDiv.parentNode.removeChild(delDiv);
	addDivId = "";
}

var win = null;

function previewWin() {
	win = window.open("","previewWin","fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=700,height=500,left=50,top=20");
	win.document.write("<a href='javascript:self.close();'>Close</a><br/><br/>");
	win.document.write(document.getElementById("rootDivId").innerHTML);
 }


function resetTMCE(){
	divId = "";
	isOpen = false;
}




function setToolbarPosition() {
  
  var browserWidth = getBrowserProps(0) - mainScrWidth;
  
  if(browserWidth > 0) {
	finalWidth = browserWidth/2 + 55;
  } else {
  	finalWidth = 70;
  }
  //alert('Screen width :'+screen.width + ' Browser width :' + browserWidth + "Toolbar position : " + finalWidth);
  changecss('.defaultSkin td.mceToolbar','left', finalWidth + 'px');
}

setToolbarPosition();

window.onresize = function(event) {
	//alert("window resized");
    window_resize();
}

var doResize = false;
var resizeTimeoutId;
var i =0;

function window_resize(e) {
 i++;
  if (navigator.appName == 'Microsoft Internet Explorer') {
 	doResize = false;
 	window.clearTimeout(resizeTimeoutId); 
    resizeTimeoutId = window.setTimeout(waitforsometime, 100); 
    doResize = true;
  }else {
 	 setToolbarPosition();
  }
  
}


function loadTMCEditor() {
	// alert('load fn called ...'+ typeof($('div#rootDivId').tinymce()));
	 
	 if (typeof($('div#rootDivId').tinymce()) == 'object') {
		 $('div#rootDivId').tinymce().remove();
	 }	 
	 
	 
	 
	 
	 
	 
	// Creates a new plugin class and a custom listbox
	 tinymce.create('tinymce.plugins.ExamplePlugin', {
	     createControl: function(n, cm) {
	         switch (n) {
	             case 'mylistbox':
	                 var mlb = cm.createListBox('mylistbox', {
	                      title : 'Insert Merge Tags',
	                      onselect : function(v) {
	                    	  //tinyMCE.activeEditor.windowManager.alert('Value selected:' + v);
	                    	  tinyMCE.activeEditor.focus();
	                    	  tinyMCE.activeEditor.selection.setContent(v);
	                      }
	                 });

	                 // Add Place holder values to the list box
	                 //   mlb.add('Email', '|^GEN_email^|');
	                 // alert('Editor phChilds='+phArr);
	                 // alert('Editor phChilds='+phArr.length);
	                 
	                 for (var i=0; i< phArr.length; i++) {
	                	 var eachEle = phArr[i];
	                	 var ind = eachEle.indexOf('::'); 
	                	 mlb.add(eachEle.substr(0, ind), eachEle.substr(ind+2));
	                 }

	                 // Return the new listbox instance
	                 return mlb;

	             case 'myCouponbox':
	                 var mlb = cm.createListBox('myCouponbox', {
	                      title : 'Insert Promotion',
	                      onselect : function(v) {
	                    	  //tinyMCE.activeEditor.windowManager.alert('Value selected:' + v);
	                    	  tinyMCE.activeEditor.focus();
	                    	  tinyMCE.activeEditor.selection.setContent(v);
	                      }
	                 });

	                 // Add Place holder values to the list box
	                 //   mlb.add('Email', '|^GEN_email^|');
	                 // alert('Editor phChilds='+phArr);
	                 // alert('Editor phChilds='+phArr.length);
	                 
	                 for (var i=0; i< ocCouponsArr.length; i++) {
	                	 var eachEle = ocCouponsArr[i];
	                	 var ind = eachEle.indexOf('::'); 
	                	 mlb.add(eachEle.substr(0, ind), eachEle.substr(ind+2));
	                 }

	                 // Return the new listbox instance
	                 return mlb;
	                 
	             case 'myImageCouponbox':
	                 var mlb = cm.createListBox('myImageCouponbox', {
	                      title : 'Insert Barcode',
	                      onselect : function(v) {
	                    	  //tinyMCE.activeEditor.windowManager.alert('Value selected:' + v);
	                    	  tinyMCE.activeEditor.focus();
	                    	  tinyMCE.activeEditor.selection.setContent(v);
	                      }
	                 });

	                 // Add Place holder values to the list box
	                 //   mlb.add('Email', '|^GEN_email^|');
	                 // alert('Editor phChilds='+phArr);
	                 // alert('Editor phChilds='+phArr.length);
	                 
	                 for (var i=0; i< ocImageCouponsArr.length; i++) {
	                	 var eachEle = ocImageCouponsArr[i];
	                	 var ind = eachEle.indexOf('::'); 
	                	 mlb.add(eachEle.substr(0, ind), eachEle.substr(ind+2));
	                 }

	                 // Return the new listbox instance
	                 return mlb;
	             /*case 'mysplitbutton':
	                 var c = cm.createSplitButton('mysplitbutton', {
	 			title : 'My split button',
	 	               	image : '/example_data/example.gif',
	 			onclick : function() {
	         	                tinyMCE.activeEditor.windowManager.alert('Button was clicked.');
	 			}
	                 });

	                 c.onRenderMenu.add(function(c, m) {
	                     m.add({title : 'Some title', 'class' : 'mceMenuItemTitle'}).setDisabled(1);

	                     m.add({title : 'Some item 1', onclick : function() {
	                         tinyMCE.activeEditor.windowManager.alert('Some  item 1 was clicked.');
	                     }});

	                     m.add({title : 'Some item 2', onclick : function() {
	                         tinyMCE.activeEditor.windowManager.alert('Some  item 2 was clicked.');
	                     }});
	                 });

	                 // Return the new splitbutton instance
	                 return c;*/
	         }

	         return null;
	     }
	 });

	 // Register plugin with a short name
	 tinymce.PluginManager.add('example', tinymce.plugins.ExamplePlugin);

	 
	 
         //alert('inside if ...');
     	//alert('37...');
          // callme();   
         // Create an instance of TinyMCE
         $('div#rootDivId').tinymce({
       		 plugins : "safari,pagebreak,table,layer,save,style,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,autoresize,spellchecker,example",
       	     elements : 'blockEditor',
       	     
             theme_advanced_toolbar_location: 'top',
             theme_advanced_toolbar_align: 'center',
             theme: 'advanced',
             theme_advanced_buttons1: "bold,italic,underline,strikethrough,forecolor,backcolor, | ,undo,redo,spellchecker, | ,formatselect,fontselect,fontsizeselect" ,
             theme_advanced_buttons2 : "justifyleft,justifycenter,justifyright,justifyfull,bullist,numlist, | ,outdent,indent,advhr,hr,search,replace, |, link,unlink,anchor,image,emotions,blockquote , | , sub,sup,cleanup,removeformat,visualaid",
             theme_advanced_buttons3 : "mylistbox,myCouponbox,myImageCouponbox", // add 'code' for html link.
             
             
             
             spellchecker_languages : "+English=dictionary",
         	 spellchecker_rpc_url : gAppUrl + "/spellchecker/jazzy-spellchecker",
         	 
         	//content_css : "css/my_tiny_content.css",
         	body_class : "my_body_class",
         	theme_advanced_statusbar_location : "none",

        	verify_html : false,
        	convert_urls : false,
        	
        	visual:false,
        	file_browser_callback : "myFileBrowser",
         });
         
       //tinymce.ScriptLoader.load('/subscriber/js/blockEditor.js');
         //tinymce.ScriptLoader.load('/subscriber/color_picker.htm');
         
         setTimeout(function() { 
        	 
       	  $('.mceEditor').css('width','100%').css('maxHeight','440px');
       	  $('.mceLayout').css('width','100%').css('maxHeight','440px');
       	  $('.mceIframeContainer').css('width','100%').css('maxHeight','440px');
       	  $('#rootDivId_ifr').css('width','100%').css('maxHeight','440px');
       	  
       	}, 2000); 

}


function waitforsometime() { 	
	if(doResize) {
 		setToolbarPosition();
	}
	//alert(i + " " + doResize);
 }


function insertPh(val) {
	if(tinyMCE == undefined || tinyMCE.activeEditor == null) {
		alert('Select the block');
		return;
	}
	if(val == 'select') {
		alert('Select a Place Holder');
		return;
	}
	tinyMCE.activeEditor.selection.setContent(val); return false;
	
} 
