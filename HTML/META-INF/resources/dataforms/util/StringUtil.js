/**
 * @fileOverview  {@link StringUtil}クラスを記述したファイルです。
 */


/**
 * @class StringUtil
 * 文字列ユーティリティ。
 * <pre>
 * 文字列操作ユーティリティクラス。
 * </pre>
 */
var StringUtil = function() {
};


/**
 * 空白文字列判定を行います。
 * @param {String} s 判定する文字列。
 * @returns {Boolean} 空白文字の場合true。
 */
StringUtil.isBlank = function(s) {
	if (s == null) {
		return true;
	}
	if (s.length == 0) {
		return true;
	}
	return false;
};

/**
 * 半角カナリスト。
 */
						// 12345678901234567890123456789012345678901234567890123456789012
StringUtil.halfKanaList = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝｧｨｩｪｫｬｭｮｯ､｡ｰ｢｣ﾞﾟ";

/**
 * 全角カナリスト。
 */						// １２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０
StringUtil.fullKanaList = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンァィゥェォャュョッ、。ー「」"
						+ "　　　　　ガギグゲゴザジズゼゾダヂヅデド　　　　　バビブベボ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　"
						+ "　　　　　　　　　　　　　　　　　　　　　　　　　パピプペポ　　　　　　　　　　　　　　　　　　　　　　　　　　　　　";

/**
 * 半角カナ→全角カナ変換を行います。
 *
 * @param {String} orgtext 変換対象文字列。
 * @returns {String} 変換結果。
 */
StringUtil.halfToFullKana = function(orgtext) {
	var str = "";
	for (var i=0; i < orgtext.length; i++){
		var c = orgtext.charAt(i);
		var cnext = orgtext.charAt(i + 1);
		var n = StringUtil.halfKanaList.indexOf(c, 0);
		var nnext = StringUtil.halfKanaList.indexOf(cnext, 0);
		if (n >= 0){
			if (nnext == 60){
				c = StringUtil.fullKanaList.charAt(n + 60);
				i++;
			}else if (nnext == 61){
				c = StringUtil.fullKanaList.charAt(n + 120);
				i++;
			}else{
				c = StringUtil.fullKanaList.charAt(n);
			}
		}
		if ((n != 60) && (n != 61)){
			str += c;
		}
	}
	return str;
};

/**
 * 半角→全角変換を行います。
 *
 * @param {String} str 変換対象文字列。
 * @returns {String} 変換結果。
 */
StringUtil.halfToFull = function(str) {
	var v = str.replace(/[A-Za-z0-9-!"#$%&'()=<>,.?_\[\]{}@^~\\]/g, function(s) {
		return String.fromCharCode(s.charCodeAt(0) + 65248);
	});
	return StringUtil.halfToFullKana(v);
};


/**
 * 全角カナ→半角カナ変換を行います。
 *
 * @param {String} orgtext 変換対象文字列。
 * @returns {String} 変換結果。
 */
StringUtil.fullToHalfKana = function(orgtext) {
	var str = "";
	for (var i=0; i < orgtext.length; i++){
		var c = orgtext.charAt(i);
		var n = StringUtil.fullKanaList.indexOf(c, 0);
		if (n < 0) {
			str += c;
		} else if (n < 60) {
			str += StringUtil.halfKanaList.charAt(n);
		} else if (n < 120) {
			str += StringUtil.halfKanaList.charAt(n - 60);
			str += "ﾞ";
		} else {
			str += StringUtil.halfKanaList.charAt(n - 120);
			str += "ﾟ";
		}
	}
	return str;
};


/**
 * 全角→半角変換。
 *
 * @param {String} str 変換対象文字列。
 * @returns {String} 変換結果。
 */
StringUtil.fullToHalf = function(str) {
	var v = str.replace( /[Ａ-Ｚａ-ｚ０-９－！”＃＄％＆’（）＝＜＞，．？＿［］｛｝＠＾～￥]/g, function(s) {
		return String.fromCharCode(s.charCodeAt(0) - 65248);
	});
	return StringUtil.fullToHalfKana(v);
};


