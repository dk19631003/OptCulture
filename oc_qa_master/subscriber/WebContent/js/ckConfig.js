CKEDITOR.editorConfig = function( config )
{
config.toolbar = 'MyToolbar';

/*config.toolbar_MyToolbar =
	[
	   ['Source','-','NewPage','Preview','-','Templates'],
	    ['Cut','Copy','Paste','PasteText','-', 'SpellChecker'],
	    ['Find','Replace','-','SelectAll','RemoveFormat'],
	    ['Styles','Format','Font','FontSize'],
	    ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
	    '/',
	    ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote','CreateDiv'],
	    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
	    ['Link','Unlink','Anchor'],
	    ['Image','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
	    ['TextColor','BGColor'],
	    ['Undo','Redo','-','Maximize', 'ShowBlocks'],
	    ['tokens','occoupons','ocimagecoupons']
	];
	*/

config.toolbarGroups = [
                        { name: 'document',    groups: [ 'mode', 'document', 'doctools' ] },
                        { name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
                        { name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
                        { name: 'links' },
                        { name: 'insert' },
                        { name: 'forms' },
                        { name: 'tools' },
                        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                        { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] },
                        { name: 'styles' },
                        { name: 'colors' },
                        { name: 'others' },
                        
                    ];



config.filebrowserBrowseUrl = gRmUrl+ '/zul/gallery/ImageLibrary.zul';
config.filebrowserWindowWidth = '576';
config.filebrowserWindowHeight = '466';
config.extraPlugins = 'tokens,occoupons,ocimagecoupons';
config.allowedContent = true;
config.removeButtons = '';
//config.removePlugins = 'magicline';
//config.ignoreEmptyParagraph = false;
//Added Enter BR
config.enterMode = CKEDITOR.ENTER_BR;
//config.startupShowBorders = false;


};