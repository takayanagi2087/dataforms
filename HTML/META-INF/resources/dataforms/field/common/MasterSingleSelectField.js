/**
 * @fileOverview {@link MasterSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class MasterSingleSelectField
 *
 * @extends SingleSelectField
 */
MasterSingleSelectField = createSubclass("MasterSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
MasterSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

