/**
 * @fileOverview {@link BigintSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class BigintSingleSelectField
 *
 * @extends SingleSelectField
 */
BigintSingleSelectField = createSubclass("BigintSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
BigintSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

