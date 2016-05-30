/**
 * @fileOverview  {@link NumberUtil}クラスを記述したファイルです。
 */

/**
 * @class NumberUtil
 *
 * 数値整形ユーティリティ。
 * <pre>
 * </pre>
 */
var NumberUtil = function() {
};

/**
 * 3桁ごとに','を付けます。
 * @param {String} v 数値文字列。
 * @returns {String} 整形後の文字列。
 */
NumberUtil.addComma = function(v) {
    // カンマとスペースを除去（入力ミスを考慮）
    var value = this.delComma(v);
    // カンマ区切り
    if (isNaN(Number(value))) {
    	// 数値以外の場合はそのまま
        value = v;
    } else {
    	while (value != (value = value.replace(/^([+-]?\d+)(\d{3})/, "$1,$2")));
    }
    return value;
};

/**
 * ','を削除します。
 * @param {String} v 数値文字列。
 * @return {String} 整形後の文字列。
 */
NumberUtil.delComma = function(v) {
    // 正規表現で扱うために文字列に変換
    var value = "" + v;
    // スペースとカンマを削除
    return value.replace(/^\s+|\s+$|,/g, "");
};

/**
 * 数値文字列を数値に変換します.
 * <pre>
 * 数値に','が含まれていても変換します。
 * </pre>
 * @param v 数値文字列.
 * @returns 数値.
 */
NumberUtil.parse = function(v) {
	return Number(NumberUtil.delComma(v));
};
