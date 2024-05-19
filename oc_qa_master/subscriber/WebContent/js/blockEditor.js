
   function testFnOver(id) {
	   // console.log('---just enter  Fn over--');
	    
	    if(typeof($('div#rootDivId').tinymce()) !== 'object') {
	    	return;
	    }
	    
	    var $temp1 = $('div#addDelBtnDiv').remove();
	   // console.log('tt ::'+$temp1.val());
	    if($temp1.val() == undefined) {
	    	//console.log('inside if...');
	    	$temp1 = $("#rootDivId_ifr").contents().find('#addDelBtnDiv').remove();
	    	//console.log('temp va'+ $temp1.val());
	    }
	    
	    
	    var $currDiv = $("#rootDivId_ifr").contents().find('#'+id);
	    
	    $currDiv.css({'border': '2px dashed blue', 'padding': '0px', 'position': 'relative'}); 
	    
	    $temp1.prependTo($currDiv);
	    $temp1.show(); 
	    
	  
      //  alert( $("#rootDivId_ifr").css('height') );
    }
       
    function testFnOut(id) {
    	// alert('Out........');
         var temp2 = $("#rootDivId_ifr").contents().find('#addDelBtnDiv').remove();
    	// alert(temp2);
        // alert("before parent"+temp2.parent().attr('id'));
         temp2.appendTo('#mainDivId');
         //alert("after parent"+temp2.parent().attr('id'));
         temp2.hide();
    }
    

    function btnClicked() {
    	
    	 // var temp_id = $("#rootDivId_ifr").contents().find('#myBtn').parent().parent().parent().attr('id');
    	 // alert(temp_id);
    	 // var subTemp = temp_id.substring(0,temp_id.lastIndexOf('DivId'));
    	  
    	//  var $temp = $("#rootDivId_ifr").contents().find('#myBtn').parent().parent().parent();
    	//   alert($temp);

    	  var $blockToAdd = $("#rootDivId_ifr").contents().find('#myBtn').parent().parent();
    	  //alert("blockToAdd ih="+$blockToAdd.attr('style'));

    	  
    	  var ct= new Date();
    	  var id = "" + ct.getTime(); //ct.getMonth() + ct.getDate() + ct.getHours() + ct.getMinutes() + ct.getSeconds();
    	  var divHeaderBlock = '<div name="TMCEeditableDiv" id="TMCEEditableDiv'+ id +'" onmouseover="javascript:parent.testFnOver(\'TMCEEditableDiv'+ id +'\');"   onmouseout="javascript:parent.restoreBlock(\'TMCEEditableDiv'+ id +'\');" >';
    	  var divTailBlock = '</div>';
    	  
    	  var totStr = $blockToAdd.html();
    	  
    	  var endDivInd = totStr.indexOf('</div>');
    	  
    	  if(endDivInd==-1) {
    		  endDivInd = totStr.indexOf('</DIV>');
    	  }
    	  
    	  
    	  totStr = totStr.substr(endDivInd + 6);
    	  
    	 // alert($blockToAdd.attr('innerHTML'));
    	 // alert(totStr);
    	  
    	  
    	 $blockToAdd.after(divHeaderBlock + totStr  + divTailBlock);
    }

    
    function delClicked() {  
    	
    	var $delDivId = $("#rootDivId_ifr").contents().find('#myDelBtn').parent().parent();
    	
    	var temp = $("#rootDivId_ifr").contents().find('#addDelBtnDiv').remove();
    	$('div#mainDivId').append(temp);
    	temp.hide();
    	
    	//console.log('del Id :'+ $delDivId.attr('id'));
    	$delDivId.remove();
    }
        
    function saveTmcContent() {
   	 
    	var temp3 = $("#bodyDivId_ifr").contents().find('#testjqBtn').remove();
    	temp3.hide();
    	$('div#mainDivId').append(temp3);
	   	$('div#bodyDivId').tinymce().remove();
   }
   
    function hlBlock(id) {
    	 // var block = document.getElementById(id);
    	  //blkBdrSize = block.style.border;
    	  //block.style.border = 'blue 2px dashed';
    	//console.log('id over:'+ id);
    	//if(isMouseLeaveOrEnter(event,this)) {
    	testFnOver(id);
    	//}
    }

    function restoreBlock(id){
    	 var $currDiv = $("#rootDivId_ifr").contents().find('#'+id);
 	     $currDiv.css({'border': '0 none', 'padding': '2px'}); 
 	    
 	    // console.log('id out:'+ id);
    	  //var block = document.getElementById(id);
    	  //if(blkBdrSize != 'undefined'){
    	     ///block.style.border = blkBdrSize;
    	  //}
    	  //else{
    	     //block.style.border = '0px solid';
    	 //}
    	//if(isMouseLeaveOrEnter(event,this)) {
    		//testFnOut(id);
    	//}
     }


 

   var bodyColor ="";
   var tableColor="";
   var tempVal = 0;   // 0 val is already set in ZUL .
   var cboxColrArr = new Array();

   //cboxColrArr[0] = 'test';
   function changeBgColor(newBgColorHex) {
	  // console.log('new color :'+ newBgColorHex);
	   
	  // console.log('$("#rootDivId_ifr")='+$("#rootDivId_ifr").val());
	   
	       
	    // Increment the pointer and set Value to color buffer.
	    tempVal++;
	    cboxColrArr[tempVal] = newBgColorHex;
	    
	  //--  alert('cboxColrArr length: '+ cboxColrArr.length + ' Contents :'+ cboxColrArr + ' pointer Val :'+ tempVal);
	    
	    
	    // Set For the First time only due to zk issue of variable scope
		// --   console.log('changebgcolor fn ...'+ cboxColrArr.length);
		   
		   if(cboxColrArr.length == 1 && cboxColrArr[0] == '') {
			   alert('--11--');
			   cboxColrArr[0] = zk.Widget.$(jq('$cboxId').getColor());
		   }
		   
		   
	    // disable redo
	    zk.Widget.$(jq('$redoTlBtnId')).setDisabled(true);
	    
	    if(tempVal == 0) {
			   zk.Widget.$(jq('$undoTlBtnId')).setDisabled(true);
		 } else {
			   zk.Widget.$(jq('$undoTlBtnId')).setDisabled(false);
		}
	    
	    // Set Values 	    
	   if($("#rootDivId_ifr").val() != undefined) {
		   
		   if($("#rootDivId_ifr").contents().find('#bodyTableId')!=undefined) {
			   $("#rootDivId_ifr").contents().find('#bodyTableId').css({'background': newBgColorHex});
		   }
		   else if($("#rootDivId_ifr").contents().find('#bodyDivId')!=undefined) {
			   $("#rootDivId_ifr").contents().find('#bodyDivId').css({'background': newBgColorHex});
		   }
		   
	   }
	   else {
	   
		   if(parent.document.getElementById('bodyTableId') != undefined) {
			   tableColor = parent.document.getElementById('bodyTableId').style.backgroundColor;
			   parent.document.getElementById('bodyTableId').style.background = newBgColorHex;
		   } 
		   
		   else if(parent.document.getElementById('bodyDivId') != undefined) {
				bodyColor = parent.document.getElementById('bodyDivId').style.backgroundColor;
				parent.document.getElementById('bodyDivId').style.background = newBgColorHex;
		   }
	   }
   }
   
 
   
   function undoRedoColor(optType) {
	   
	  //-- console.log('just entered'+ optType + ' tempVal :'+ tempVal + ' cboxColrArr.len ' + cboxColrArr);
	   	   
	   // if type is undo.
	   if(optType == 'undo') {
		    
		   tempVal--;
	   } else if (optType == 'redo') {
		   
		   tempVal++;
	   }   
	  // -- alert(cboxColrArr[tempVal]);
	   // disable undo redo if limit reaches.
	   if(tempVal == 0) {
		   
		   zk.Widget.$(jq('$undoTlBtnId')).setDisabled(true);
		   
	   } else {
		   
		   zk.Widget.$(jq('$undoTlBtnId')).setDisabled(false);
	   } 
	   
	   console.log('tempVal :'+ tempVal + ' len :'+ cboxColrArr.length);
	   
	   if(tempVal == cboxColrArr.length-1) {
		   
		   zk.Widget.$(jq('$redoTlBtnId')).setDisabled(true);
	   } else {
		   
	       zk.Widget.$(jq('$redoTlBtnId')).setDisabled(false);
	   }
	   
	   // set body background color.
	   if($("#rootDivId_ifr").val() != undefined) {
		   
		   if($("#rootDivId_ifr").contents().find('#bodyTableId')!=undefined) {
			   $("#rootDivId_ifr").contents().find('#bodyTableId').css({'background': cboxColrArr[tempVal]});
		   }
		   else if($("#rootDivId_ifr").contents().find('#bodyDivId')!=undefined) {
			   $("#rootDivId_ifr").contents().find('#bodyDivId').css({'background': cboxColrArr[tempVal]});
		   }
		   
	   } else {
		   		if(parent.document.getElementById('bodyTableId') != undefined) {
		   			
		   			parent.document.getElementById('bodyTableId').style.backgroundColor =  cboxColrArr[tempVal];
		   		} else if(parent.document.getElementById('bodyDivId') != undefined) {
		   			
		   			parent.document.getElementById('bodyDivId').style.backgroundColor =  cboxColrArr[tempVal];
		   		}   
       }
	   
	   //  set colorbox with presnt color. 
	  // alert('set Color to :'+cboxColrArr[tempVal]);
	   zk.Widget.$(jq('$cboxId')).setColor(cboxColrArr[tempVal]);
   }
   
  
   
 
   