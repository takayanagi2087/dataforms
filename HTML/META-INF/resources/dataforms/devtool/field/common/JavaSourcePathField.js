/**
 * @fileOverview {@link JavaSourcePathField}クラスを記述したファイルです。
 */

/**
 * @class JavaSourcePathField
 *
 * @extends VarcharField
 */
JavaSourcePathField = createSubclass("JavaSourcePathField", {}, "VarcharField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
JavaSourcePathField.prototype.attach = function() {
	VarcharField.prototype.attach.call(this);
	this.get().attr("placeholder", MessagesUtil.getMessage("message.setjavasourcepath"))
};

