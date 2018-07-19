/**
 * @fileOverview {@link MasterMultiSelectField}クラスを記述したファイルです。
 */

/**
 * @class MasterMultiSelectField
 *
 * @extends MultiSelectField
 */
MasterMultiSelectField = createSubclass("MasterMultiSelectField", {}, "MultiSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
MasterMultiSelectField.prototype.attach = function() {
	MultiSelectField.prototype.attach.call(this);
};

