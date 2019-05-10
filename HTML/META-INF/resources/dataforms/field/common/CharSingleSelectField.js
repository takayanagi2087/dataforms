/**
 * @fileOverview {@link CharSingleSelectField}クラスを記述したファイルです。
 */

/**
 * @class CharSingleSelectField
 *
 * @extends SingleSelectField
 */
CharSingleSelectField = createSubclass("CharSingleSelectField", {}, "SingleSelectField");


/**
 * HTMLエレメントとの対応付けを行います。
 */
CharSingleSelectField.prototype.attach = function() {
	SingleSelectField.prototype.attach.call(this);
};

