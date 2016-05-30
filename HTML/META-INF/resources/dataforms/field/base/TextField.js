/**
 * @fileOverview {@link TextField}クラスを記述したファイルです。
 */

/**
 * @class TextField
 * テキストフィールドクラス。
 * <pre>
 * テキストフィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
TextField = createSubclass("TextField", {}, "Field");


/**
 * 値を設定します。
 * @param {String} value 設定値。
 */
TextField.prototype.setValue = function(value) {
	var ut = $('<div>').html(value).text(); //unescape
	Field.prototype.setValue.call(this, ut);
};