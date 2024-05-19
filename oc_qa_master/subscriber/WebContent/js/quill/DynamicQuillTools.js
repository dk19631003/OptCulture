/** @class Abstract class representing a tool for a Quill Editor toolbar. */

class QuillToolbarItem {
    constructor(options) {
        const me = this
        me.options = options

        me.qlFormatsEl = document.createElement("span")
        me.qlFormatsEl.className = "ql-formats"
    }
    /**
     * Attaches this tool to the given Quill Editor instance.
     *
     * @param {Quill} quill - The Quill Editor instance that this tool should get added to.
     */
    attach(quill) {
        const me = this
        me.quill = quill
        me.toolbar = quill.getModule('toolbar')
        me.toolbarEl = me.toolbar.container
        me.toolbarEl.appendChild(me.qlFormatsEl)
    }
    /**
     * Detaches this tool from the given Quill Editor instance.
     *
     * @param {Quill} quill - The Quill Editor instance that this tool should get added to.
     */
    detach(quill) {
        const me = this
        me.toolbarEl.removeChild(me.qlFormatsEl)
    }
    /**
     * Calculate the width of text.
     *
     * @param {string} text - The text of which the length should be calculated.
     * @param {string} [font="500 14px 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"] - The font css that shuold be applied to the text before calculating the width.
     */
    _getTextWidth(text, font="500 14px 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif") {
        const canvas = this._getTextWidth.canvas || (this._getTextWidth.canvas = document.createElement("canvas"))
        const context = canvas.getContext("2d")
        context.font = font
        const metrics = context.measureText(text)
        return metrics.width
    }
    /**
     * Add a global css rule to the document.
     *
     * @param {string} cssRule - CSS rules
     */
    _addCssRule(cssRule) {
        const style = document.createElement("style");
        document.head.appendChild(style);
        style.sheet.insertRule(cssRule, 0)
    }
    /**
     * Generate a random ID.
     *
     * @returns {string} random 10 digit ID
     */
    _generateId() {
        return Math.random().toString().substr(2, 10)
    }
}

/** @class Class representing a dropdown tool for a Quill Editor toolbar. */
class QuillToolbarDropDown extends QuillToolbarItem {
    /**
     * Creates an instance of QuillToolbarDropDown.
     *
     * @constructor
     * @param {object} [options] - The options/settings for this QuillToolbarDropDown.
     * @param {string} [options.id=`dropdown-${random10digitNumber}`] - The id of the quill tool.
     * @param {string} [options.label=""] - The default label that is being displayed before making a selection.
     * @param {boolean} [options.rememberSelection=true] - Automatically change the label to the current selection.
     * @param {object} [options.items={}] - The default items this dropdown will have. Needs to be a key-value-object (key=visible label; value=actual value).
     */
    constructor(options) {
        super(options)
        const me = this

        me.id = me.options.id || `dropdown-${me._generateId()}`

        const qlPicker = document.createElement("span")
        qlPicker.className = `ql-${me.id} ql-picker`
        me.qlFormatsEl.appendChild(qlPicker)

        const qlPickerLabel = document.createElement("span")
        qlPickerLabel.className = "ql-picker-label"
        qlPicker.appendChild(qlPickerLabel)
        qlPickerLabel.addEventListener('click', function(e) {
            qlPicker.classList.toggle('ql-expanded')

        })
        window.addEventListener('click', function(e){
            if (!qlPicker.contains(e.target)){
                qlPicker.classList.remove('ql-expanded')
            }
        })

        const qlPickerOptions = document.createElement("span")
        qlPickerOptions.className = "ql-picker-options"
        qlPicker.appendChild(qlPickerOptions)

        me.dropDownEl = qlPicker
        me.dropDownPickerEl = me.dropDownEl.querySelector('.ql-picker-options')
        me.dropDownPickerLabelEl = me.dropDownEl.querySelector('.ql-picker-label')
        me.dropDownPickerLabelEl.innerHTML = `<svg viewBox="0 0 18 18"> <polygon class="ql-stroke" points="7 11 9 13 11 11 7 11"></polygon> <polygon class="ql-stroke" points="7 7 9 5 11 7 7 7"></polygon> </svg>`

        me.setLabel(me.options.label || "")
        me.setItems(me.options.items || {})

        me._addCssRule(`
            .ql-snow .ql-picker.ql-${me.id} .ql-picker-label::before, .ql-${me.id} .ql-picker.ql-size .ql-picker-item::before {
                content: attr(data-label);
            }
        `)
    }
    /**
     * Set the items for this dropdown tool.
     *
     * @param {object} items - Needs to be a key-value-object (key=visible label; value=actual value).
     */
    setItems(items) {
        const me = this
        for (const [label,value] of Object.entries(items)) {
            const newItemEl = document.createElement("span")
            newItemEl.className = "ql-picker-item"
            newItemEl.innerHTML = label
            newItemEl.setAttribute('data-value', value)
            newItemEl.onclick = function(e) {
                me.dropDownEl.classList.remove('ql-expanded')
                if (me.options.rememberSelection)
                    me.setLabel(label)
                if (me.onSelect)
                    me.onSelect(label, value, me.quill)
            }
            me.dropDownPickerEl.appendChild(newItemEl)
        }
    }
    /**
     * Set the label for this dropdown tool and automatically adjust the width to fit the label.
     *
     * @param {String} newLabel - The new label that should be set.
     */
    setLabel(newLabel) {
        const me = this
        const requiredWidth = `${me._getTextWidth(newLabel)+30}px`
        me.dropDownPickerLabelEl.style.width = requiredWidth
        me.dropDownPickerLabelEl.setAttribute('data-label', newLabel)
    }
    /**
     * A callback that gets called automatically when the dropdown selection changes. This callback is expected to be overwritten.
     *
     * @param {string} label - The label of the newly selected item.
     * @param {string} value - The value of the newly selected item.
     * @param {Quill} quill - The quill instance the dropdown tool is attached to.
     */
    onSelect(label, value, quill) {
    	quill.js
    }
}


var myDropDown = new QuillToolbarDropDown({
    label: "Merge Tags",
    rememberSelection: false
});

const toolbarOptions = {
		container: [
			['bold', 'italic', 'underline', 'strike'],
			['link'],        // toggled buttons
			[{ 'list': 'ordered'}, { 'list': 'bullet' }],
			[{ 'script': 'super' }],      // superscript/subscript
			[{ 'indent': '-1'}, { 'indent': '+1' }],          // outdent/indent
			[{ 'header': [1, 2, 3, 4, 5, 6, false] }],
			[{ 'color': [] }, { 'background': [] }],          // dropdown with defaults from theme
			[{ 'font': [] }],
			[{ 'align': [] }],
			['clean'],
        ],
		counter: {
            container: '#counter',
            unit: 'characters'
        }
	}