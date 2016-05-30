
/**
 * @fileOverview {@link TimeField}クラスを記述したファイルです。
 */

/**
 * @class TimeField
 * Time型フィールドクラス。
 * <pre>
 * </pre>
 * @extends DateTimeField
 */
TimeField = createSubclass("TimeField", {}, "DateTimeField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
TimeField.prototype.attach = function() {
	DateTimeField.prototype.attach.call(this);
	var comp = this.get();
	if (!comp.prop("readonly")) {
		var displayFormat = MessagesUtil.getMessage("format.timefield");
		var editFormat = MessagesUtil.getMessage("editformat.timefield");
		this.setFormat(displayFormat, editFormat);
	}
	this.backupStyle();
};
