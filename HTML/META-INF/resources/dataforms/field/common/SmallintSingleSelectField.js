/**
 * @fileOverview {@link SmallintSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class SmallintSingleSelectField
 *
 * @extends SingleSelectField
 */
SmallintSingleSelectField = createSubclass("SmallintSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
SmallintSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

