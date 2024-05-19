
var downIdleBtn;
var downIdleBtnClass;
	
	function onIdleMouseDown(tbn) {
		downIdleBtn=tbn;
		downIdleBtnClass = tbn.getSclass();
		
		if(downIdleBtnClass.indexOf(' down') == -1) {
			tbn.setSclass(downIdleBtnClass+' down') ;
		}
		else {
			downIdleBtnClass = downIdleBtnClass.substring(0, downIdleBtnClass.lastIndexOf(" down"));
		}
     } 
	
	
	function onIdleMouseUp(tbn) {

		if(downIdleBtn==null || tbn.getId() != downIdleBtn.getId()) {
			return;
		}
		
		downIdleBtn=null;
		tbn.setSclass(downIdleBtnClass);
	 } 