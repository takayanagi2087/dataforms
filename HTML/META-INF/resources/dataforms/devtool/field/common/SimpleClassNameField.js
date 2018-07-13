/**
 * @fileOverview {@link SimpleClassNameField}クラスを記述したファイルです。
 */

/**
 * @class SimpleClassNameField
 *
 * @extends VarcharField
 */
SimpleClassNameField = createSubclass("SimpleClassNameField", {}, "VarcharField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
SimpleClassNameField.prototype.attach = function() {
	VarcharField.prototype.attach.call(this);
};

