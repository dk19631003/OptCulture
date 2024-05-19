$.getScript("https://app-rsrc.getbee.io/plugin/BeePlugin.js", function() {
	var endpoint = "https://bee-auth.getbee.io/apiauth";
	var data = beekey;
//encoding emojis in jsonFile start-suraj
function encodeBeeEditorBodyJSONText(jsonFileText){
    var string =  jsonFileText;
     let finalJsonFileText='';
     let charUnicode;
for (const codePoint of string) {
     charUnicode= codePoint.codePointAt(0).toString(16);
     if(charUnicode.length == 5 || charUnicode.length == 4){
      charUnicode="&#x"+charUnicode+';';
       finalJsonFileText += charUnicode;
     }else{
      finalJsonFileText += codePoint;
    }
}
console.log('finalJsonFileText :',finalJsonFileText);
return finalJsonFileText;
}//end of encoding emojis in jsonFile

	var req = new XMLHttpRequest();
	req.open('post', endpoint, true);
	req.onreadystatechange = function() {
		if (req.readyState === 4 && req.status === 200) {
			var token = req.responseText;
			BeePlugin.create(JSON.parse(token), beeConfig, function(beePluginInstance) {
				bee = beePluginInstance;
				bee.start(mytemplate);
				showButtonsAfterLoad();
			});
		}
	};
	if (data) {
		req.setRequestHeader('Content-type','application/x-www-form-urlencoded');
		req.send(data);
	}
	var beeConfig;
	if((requestFrom == '' || requestFrom == 'undefined') && requestFrom !='e_receipt'){
		beeConfig = {
			uid : namevalue,
			container : containerId,
			autosave : false,
			trackChanges : true,
			language : 'en-US',
			saveRows : true,
			specialLinks : specialLinks79,
			mergeTags : mergeTags97,
			mergeContents : barCodes79,
			editorFonts : {
				    showDefaultFonts: false,
				   //<link rel="preconnect" href="https://fonts.googleapis.com">
					//<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
					//<link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300..800;1,300..800&display=swap" rel="stylesheet">
				        customFonts: [
							
  {
    name: "Arial ",
    fontFamily: "Arial, 'Helvetica Neue', Helvetica, sans-serif"
  }, {
    name: "Courier",
    fontFamily: "'Courier New', Courier, 'Lucida Sans Typewriter', 'Lucida Typewriter', monospace",
    url:"https://fonts.googleapis.com/css2?family=Courier+Prime:ital,wght@0,400;0,700;1,400;1,700&family=Noto+Serif+Georgian:wght@100..900&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap"
  }, {
    name: "Georgia",
    fontFamily: "Georgia, Times, 'Times New Roman', serif",
    url:"https://fonts.googleapis.com/css2?family=Noto+Serif+Georgian:wght@100..900&family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap",
   /* fontWeight :{
		100: 'Thin',
		200: 'Extra-light',
		300: 'Light',
		400: 'Regular',
		500: 'Medium',
		600: 'Semi-bold',
		700: 'Bold',
		800: 'Extra-bold',
		900: 'Black',
		
	}*/
  }, {
    name: "Helvetica",
    fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Helvetica+Neue"
  }, {
    name: "Lucida Sans",
    fontFamily: "'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Geneva, Verdana, sans-serif",
    url:"https://fonts.googleapis.com/css?family=Lucida+Sans"
  }, {
    name: "Tahoma",
    fontFamily: "Tahoma, Verdana, Segoe, sans-serif",
    url:"https://fonts.googleapis.com/css?family=Tahoma"
  }, {
    name: "Times New Roman",
    fontFamily: "TimesNewRoman, 'Times New Roman', Times, Beskerville, Georgia, serif",
    url: "https://fonts.googleapis.com/css?family=Times+New+Roman"
  }, {
    name: "Trebuchet MS",
    fontFamily: "'Trebuchet MS', 'Lucida Grande', 'Lucida Sans Unicode', 'Lucida Sans', Tahoma, sans-serif",
    url : "https://fonts.googleapis.com/css?family=Trebuchet+MS"
  }, {
    name: "Verdana",
    fontFamily: "Verdana, Geneva, sans-serif",
    url:"https://fonts.googleapis.com/css?family=Verdana"
  }, {
    name: "ヒラギノ角ゴ Pro W3",
    fontFamily: "ヒラギノ角ゴ Pro W3, Hiragino Kaku Gothic Pro,Osaka, メイリオ, Meiryo, ＭＳ Ｐゴシック, MS PGothic, sans-serif"
  }, {
    name: "メイリオ",
    fontFamily: "メイリオ, Meiryo, ＭＳ Ｐゴシック, MS PGothic, ヒラギノ角ゴ Pro W3, Hiragino Kaku Gothic Pro,Osaka, sans-serif"
  }, {
    name: "Bitter",
    fontFamily: "'Bitter', Georgia, Times, 'Times New Roman', serif",
    url: "https://fonts.googleapis.com/css?family=Bitter"
  }, {
    name: "Droid Serif",
    fontFamily: "'Droid Serif', Georgia, Times, 'Times New Roman', serif",
    url: "https://fonts.googleapis.com/css?family=Droid+Serif"
  }, {
    name: "Lato",
    fontFamily: "'Lato', Tahoma, Verdana, Segoe, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Lato"
  }, 
  /*{
    name: "Open Sans",
    fontFamily: "'Open Sans', 'Helvetica Neue', Helvetica, Arial, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Open+Sans"
  },
  */ {
    name: "Roboto",
    fontFamily: "'Roboto', Tahoma, Verdana, Segoe, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Roboto"
  }, {
    name: "Source Sans Pro",
    fontFamily: "'Source Sans Pro', Tahoma, Verdana, Segoe, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Source+Sans+Pro"
  }, {
    name: "Montserrat",
    fontFamily: "'Montserrat', 'Trebuchet MS', 'Lucida Grande', 'Lucida Sans Unicode', 'Lucida Sans', Tahoma, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Montserrat"
  }, {
    name: "Ubuntu",
    fontFamily: "'Ubuntu', Tahoma, Verdana, Segoe, sans-serif",
    url: "https://fonts.googleapis.com/css?family=Ubuntu"
},
{
							name: "Open Sans",
        					fontFamily: "'Open Sans','sans-serif', 'Times', 'serif'",
        					url: "https://fonts.googleapis.com/css2?family=Open+Sans:ital,wdth,wght@0,75..100,300..800;1,75..100,300..800&display=swap",
							fontWeight: {
            				200: 'Extra-light',
            				300: 'Light',
            				400: 'Regular',
            				500: 'Medium',
            				600: 'Semi-bold',
            				700: 'Bold',
            				800: 'Extra-bold',
          				}
          				},
          				
          			/*	{
							  name: "Narziss Text",
							  fontFamily : "'NarzissTextMedium-Drops','Open Sans','Times','sans-serif'",
							  url: "https://db.onlinewebfonts.com/c/45082a2d8465ffd80eedc1e229a3f3c5?family=NarzissTextMedium-Drops",

							} */ 
							
							]
			}, // [optional, default: see description] 4123
			sidebarPosition : 'right',
			contentDialog : {
				saveRow : {
					handler : function(resolve, reject, args) {
						if (args != null && args != '') {
							return resolve(saveRowPopupcall(args));
						} else {
							reject();
						}
					}
				}
			},
			rowsConfiguration : {
				emptyRows : true,
				defaultRows : true,
				externalContentURLs : externalUrl
			},
			onChange: function (jsonFile, response) {
			      console.log('response', response);
				jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				autosave(jsonFile, null);
			    },
			onSave : function(jsonFile, htmlFile) {
				jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				savejsonhtmlcontent(jsonFile, htmlFile);
			},
			onAutoSave : function(jsonFile) { // + thumbnail? 
				jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				autosave(jsonFile, null);
			},
			onSaveAsTemplate : function(jsonFile) { // + thumbnail? 
				jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				savejsonhtmlcontent(jsonFile, null);
			},
			onSend : function(htmlFile) {
				send('newsletters.html', htmlFile);
			},
			onError : function(errorMessage) {
				console.log('onError ', errorMessage);
			}
		};
	}else{
		beeConfig = {  
			      uid: namevalue,
			      container: 'bee-plugin-container-e_receipts',
			      autosave: false, 
			      trackChanges : true,
			      language: 'en-US',
			      saveRows: true,
			      specialLinks: specialLinks79,
			      mergeTags: mergeTags97,
			      mergeContents: barCodes78,
			      contentDialog: {
			    	  saveRow: {
			    	    handler: function (resolve, reject, args) {
			    				    const metadata = { "name": "header", 
			    				    				"tags": "product, two columns, blue"
			    				    		   			}
			    				    		 resolve(metadata);
			    					}
			    		
			    	  },
			    	},
			      rowsConfiguration: {
			            emptyRows: true,
			            defaultRows: true,         
			            externalContentURLs: [{
			                name: "Items",
			                value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=items"
			                },{
			                  name: "Payments",
			                  value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=payments"
			                  },{
			                    name: "Store Info",
			                    value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=store info"
			                    },{
			                      name: "Bill-to-Ship-to",
			                      value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=bill-to-ship-to"
			                  	  },{
			                        name: "Store Policy",
			                        value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=store policy"
			                   	    },{
			                          name: "Auto Turn-Off",
			                          value: "https://qcapp.optculture.com/subscriber/savedRows.mqrm?name=auto turn-off"
			                       	  }]         
			        },
			        onChange: function (jsonFile, response){
			        	console.log('response',response);
						jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
						autosave(jsonFile, null);
			        },
			      onSaveRow: function (rowJSON, rowHTML, pageJSON) {
			  	    saverowsjsonhtmlcontent(rowJSON, rowHTML, pageJSON);
			  	},
			      onSave: function(jsonFile, htmlFile) { 
				    jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				    savejsonhtmlcontent(jsonFile, htmlFile);
			    },
			      onAutoSave: function(jsonFile) { // + thumbnail? 
                      jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				      autosave (jsonFile,null);
			        },
			      onSaveAsTemplate: function(jsonFile) { // + thumbnail? 
					jsonFile = encodeBeeEditorBodyJSONText(jsonFile);
				    savejsonhtmlcontent(jsonFile, null);
			      },
			      onSend: function(htmlFile) {
			    	  send('newsletters.html', htmlFile);
			    	  
			      },
			      onError: function(errorMessage) { 
			        console.log('onError ', errorMessage);
			       }
			    };
	}

});
