/**
 * @fileOverview {@link IntegerSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class IntegerSingleSelectField
 *
 * @extends SingleSelectField
 */
IntegerSingleSelectField = createSubclass("IntegerSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
IntegerSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

