/**
 * @fileOverview {@link VarcharSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class VarcharSingleSelectField
 *
 * @extends SingleSelectField
 */
VarcharSingleSelectField = createSubclass("VarcharSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
VarcharSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

