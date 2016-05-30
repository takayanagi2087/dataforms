/**
 * @fileOverview  {@link SimpleDateFormat}クラスを記述したファイルです。
 */

/**
 * @class SimpleDateFormat
 *
 * 日付フォーマットクラス。
 * <pre>
 * java.text.SimpleDateFormatのサブセットで、以下の書式をサポートします。
 * yyyy 年(4桁).
 * yy 年(2桁).
 * MM 月
 * dd 日
 * HH 時
 * mm 分
 * ss 秒
 * SSS ミリ秒
 * <pre>
 *
 * @param {String} f フォーマット。
 */
var SimpleDateFormat = function(f) {
	this.formatString = f;
};


/**
 * 日付をフォーマットした文字列を取得します。
 * @param {Date} date 日付。
 * @returns {String} フォーマットした文字列。
 */
SimpleDateFormat.prototype.format = function(date) {
	var ret = new String(this.formatString);
	ret = ret.replace(/yyyy/g, date.getFullYear());
	ret = ret.replace(/yy/g, date.getFullYear() % 100);
	ret = ret.replace(/MM/g, ('0' + (date.getMonth() + 1)).slice(-2));
	ret = ret.replace(/dd/g, ('0' + date.getDate()).slice(-2));
	ret = ret.replace(/HH/g, ('0' + date.getHours()).slice(-2));
	ret = ret.replace(/mm/g, ('0' + date.getMinutes()).slice(-2));
	ret = ret.replace(/ss/g, ('0' + date.getSeconds()).slice(-2));
	if (ret.match(/S/g)) {
		var milliSeconds = ('00' + date.getMilliseconds()).slice(-3);
		var length = ret.match(/S/g).length;
		for (var i = 0; i < length; i++) {
			ret = ret.replace(/S/, milliSeconds.substring(i, i + 1));
		}
	}
	return ret;

};

/**
 *
 * 指定したパターンに対応した文字列を取得します。
 * @param {String} dstr 日付フォーマットの文字列。
 * @param {String} pat パターン。
 * @returns {String} 切り出した文字列。
 */
SimpleDateFormat.prototype.getValue = function(dstr, pat) {
	var ret = null;
	var idx = this.formatString.indexOf(pat);
	if (idx >= 0) {
		ret = dstr.substring(idx, idx + pat.length);
	}
	return ret;
};

/**
 * 年を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} 年の文字列。
 */
SimpleDateFormat.prototype.getYear = function(dstr) {
	var year = this.getValue(dstr, "yyyy");
	if (year == null) {
		var yy = this.getValue(dstr, "yy");
		if (yy != null) {
			year = "20" + yy;
		} else {
			year = "1970"; // 既定の年を返す.
		}
	}
	return year;
};

/**
 * 月を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} 月の文字列。
 */
SimpleDateFormat.prototype.getMonth = function(dstr) {
	var ret = this.getValue(dstr, "MM");
	if (ret == null) {
		ret = "01";
	}
	return ret;
};

/**
 * 日を取得するします。
 * @param {String} dstr 日付文字列。
 * @returns {String} 日の文字列。
 */
SimpleDateFormat.prototype.getDate = function(dstr) {
	var ret = this.getValue(dstr, "dd");
	if (ret == null) {
		ret = "01";
	}
	return ret;
};

/**
 * 時を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} 時の文字列。
 */
SimpleDateFormat.prototype.getHour = function(dstr) {
	var ret = this.getValue(dstr, "HH");
	if (ret == null) {
		ret = "00";
	}
	return ret;
};

/**
 * 分を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} 分の文字列。
 */
SimpleDateFormat.prototype.getMin = function(dstr) {
	var ret = this.getValue(dstr, "mm");
	if (ret == null) {
		ret = "00";
	}
	return ret;
};

/**
 * 秒を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} 秒の文字列。
 */
SimpleDateFormat.prototype.getSec = function(dstr) {
	var ret = this.getValue(dstr, "ss");
	if (ret == null) {
		ret = "00";
	}
	return ret;
};


/**
 * ミリ秒を取得します。
 * @param {String} dstr 日付文字列。
 * @returns {String} ミリ秒の文字列。
 */
SimpleDateFormat.prototype.getMSec = function(dstr) {
	var ret = this.getValue(dstr, "SSS");
	if (ret == null) {
		ret = "000";
	}
	return ret;
};

/**
 * 文字列をフォーマットにしたがって解析します。
 * @param {String} dstr 日付文字列。
 * @returns {Date} 日付。
 */
SimpleDateFormat.prototype.parse = function(dstr) {
	var ret = null;
	var validPattern = "^" + this.formatString.replace(/[yMdHmsS]/g, "[0-9]") + "$";
	var regexp = new RegExp(validPattern);
	if (dstr.match(regexp)) {
		var year = parseInt(this.getYear(dstr), 10);
		var month = parseInt(this.getMonth(dstr), 10);
		var date = parseInt(this.getDate(dstr), 10);
		var hour = parseInt(this.getHour(dstr), 10);
		var min = parseInt(this.getMin(dstr), 10);
		var sec = parseInt(this.getSec(dstr), 10);
		var msec = parseInt(this.getMSec(dstr), 10);
		ret = new Date(year, month - 1, date, hour, min, sec, msec);
		if (year == ret.getFullYear() && month == (ret.getMonth() + 1) && date == ret.getDate() &&
			hour == ret.getHours() && min == ret.getMinutes() && sec == ret.getSeconds() && msec == ret.getMilliseconds()) {
			; // この場合は正常.
		} else {
			ret = null;
		}

	}
	return ret;
};


