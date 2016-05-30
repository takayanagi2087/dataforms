/**
 * @fileOverview {@link WebSourcePathField}クラスを記述したファイルです。
 */

/**
 * @class WebSourcePathField
 *
 * @extends VarcharField
 */
WebSourcePathField = createSubclass("WebSourcePathField", {}, "VarcharField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
WebSourcePathField.prototype.attach = function() {
	VarcharField.prototype.attach.call(this);
	this.get().attr("placeholder", MessagesUtil.getMessage("message.setwebsourcepath"))
};

