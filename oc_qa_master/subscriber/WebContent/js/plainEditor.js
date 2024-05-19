tinyMCE.init({
	theme : "advanced",
	plugins : "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
	elements : 'plainEditor',
	
	theme_advanced_buttons1 : "newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,styleprops,pagebreak",
	theme_advanced_buttons2 : "tablecontrols,|,bullist,numlist,|,outdent,indent,|,sub,sup,|,link,unlink,anchor,image,|,hr,advhr",
	theme_advanced_buttons3 : "spellchecker,search,replace,|,undo,redo,|,cleanup,removeformat,visualaid,|,code,preview,|,forecolor,backcolor,|,emotions,|,insertdate,inserttime",
	
	spellchecker_languages : "+English=dictionary",
	spellchecker_rpc_url : gAppUrl + "/spellchecker/jazzy-spellchecker",
	
	theme_advanced_statusbar_location : "bottom",
	theme_advanced_toolbar_align : "center",
	theme_advanced_toolbar_location : "top",
	forced_root_block : false,
    force_br_newlines : true,
    force_p_newlines : false

});

var lpEditorDivId;
//tinyMCE.execCommand('mceAddControl', false, pEditorDivId);
function loadEditor(editorDivId){
	closeEditor(lpEditorDivId);
	lpEditorDivId = editorDivId
	tinyMCE.execCommand('mceAddControl', false, editorDivId);
}
loadEditor(pEditorDivId);


pEditorOpen = true;
function closeEditor(id){
	if (!tinyMCE.getInstanceById(id)){
		tinyMCE.execCommand('mceRemoveControl', false, id);
	}
}
function save(htmlStuffId) {
		htmlStuffId.value = '';
		var eId = tinyMCE.activeEditor.editorId; 
		var editorIframe = document.getElementById(eId + "_ifr");
		//alert("editorIframe : "+editorIframe+" - pEditorDivId : "+eId);
		if(editorIframe == null) {
			htmlStuffId.value = document.getElementById(eId).innerHTML
	  		final = htmlStuffId.value;
		}else{
	  		htmlStuffId.value = editorIframe.contentWindow.document.body.innerHTML;
		    //appendTextNodes(editorIframe.contentWindow.document.body);
	  	}
	  	htmlStuffId.fire('onChange',{value:htmlStuffId.value},{toServer:true},0);
	  	/*
	   	//if(trim(final)==''){
	   	//	alert("Email should not be empty");
   		//	return false;
	   	//	}	
	   	//	final = ''
	   if (document.createEvent) { 
		 var evt = document.createEvent('HTMLEvents');
		 evt.initEvent( 'blur', false, false);
		 htmlStuffId.dispatchEvent(evt);
		 var evt2 = document.createEvent('HTMLEvents');
		 evt2.initEvent( 'change', false, false);
		 htmlStuffId.dispatchEvent(evt2);
	   } else if (document.createEventObject) {
		 htmlStuffId.fireEvent('onblur');
		 htmlStuffId.fireEvent('onchange');
	   }
	   */
}

var final = '';
function appendTextNodes(element){
    var text = '';

    // Loop through the childNodes of the passed in element
    for (var i = 0, len = element.childNodes.length; i < len; i++) {
        var node = element.childNodes[i];		// Get a reference to the current child
        if (node.nodeType == 3) {			 	// Append the node's value if it's a text node
                text += node.nodeValue;	
        }
        if (node.childNodes.length > 0) {		// Recurse through the node's children, if there are any
                appendTextNodes(node);
        }
    }
    final = final + text;
}
