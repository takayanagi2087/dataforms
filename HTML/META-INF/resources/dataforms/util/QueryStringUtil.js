/**
 * @fileOverview  {@link QueryStringUtil}クラスを記述したファイルです。
 */


/**
 * @class QueryStringUtil
 * 問合せ文字列ユーティリティ。
 * <pre>
 * 問合せ文字列操作ユーティリティクラス。
 * </pre>
 */
var QueryStringUtil = function() {
};


/**
 * 問合せ文字列を解析し、Objectに変換します。
 * @param {String} s 問合せ文字列。
 * @returns {Object} 変換されたObject。
 */
QueryStringUtil.parse = function(qs) {
	var vars = new Object;
	var temp = qs.split('&');
	for(var i = 0; i <temp.length; i++) {
		var params = temp[i].split('=');
		if (params[1] != null) {
			var v = params[1].replace(/\+/g, "%20");
			vars[params[0]] = decodeURIComponent(v);
		} else {
			var v = params[1];
			vars[params[0]] = decodeURIComponent(v);
		}
	}
	return vars;
};