/**
 * @fileOverview {@link NumberField}クラスを記述したファイルです。
 */

/**
 * @class NumberField
 * 数値フィールドクラス。
 * <pre>
 * 各種数値フィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
NumberField = createSubclass("NumberField", {}, "Field");


/**
 * 3桁ごとにカンマを追加するします。
 * @param {String} v 数値文字列。
 * @returns {String} カンマを追加した文字列。
 */
NumberField.prototype.addComma = function(v) {
	return NumberUtil.addComma(v);
};


/**
 * カンマを削除します。
 * @param {String} v カンマを含む文字列。
 * @returns {String} カンマを削除した文字列。
 */
NumberField.prototype.delComma = function(v) {
	return NumberUtil.delComma(v);
};

/**
 * 数値フィールドの初期化を行います。
 *
 */
NumberField.prototype.init = function() {
	Field.prototype.init.call(this);
};

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * focus, blurイベント処理を登録し、カンマの付け外しを行います。
 * </pre>
 */
NumberField.prototype.attach = function() {
	Field.prototype.attach.call(this);
	var thisField = this;
	var el = this.get();
	// 右寄せに設定.
	el.css("text-align", "right");
	// focusイベント処理を登録
	el.focus(function() {
		if (thisField.commaFormat) {
			var v = el.val();
			v = thisField.delComma(v);
			el.val(v);
		}
	});
	// blurイベント処理を登録.
	el.blur(function() {
		var v = el.val();
		var v = StringUtil.fullToHalf(v);
		v = thisField.adjustScale(v);
		el.val(v);
		if (thisField.commaFormat) {
			var v = el.val();
			v = thisField.addComma(v);
			el.val(v);
		}
	});
	this.backupStyle();
};

/**
 * 小数点以下の桁数を調節します。
 * @param {String} v 値。
 * @return {String} 小数点以下の桁数を調整した文字列。
 */
NumberField.prototype.adjustScale = function(v) {
	// 数値に変換できなければ、そのままの文字列.
	if (v == null || v == "" || isNaN(Number(v))) {
		return v;
	}
	var value = v.toString();
	if (this.scale > 0) {
		if (value.indexOf(".") < 0) {
			value += ".";
		}
		for (var i = 0; i < this.scale; i++) {
			value += "0";
		}
		var pp = value.indexOf(".");
		if (pp >= 0) {
			value = value.substring(0, pp + this.scale + 1);
		}
	} else {
		var pp = value.indexOf(".");
		if (pp >= 0) {
			value = value.substring(0, pp);
		}
	}
	return value;
};


/**
 * 値を設定します。
 * @param {String} value 設定値。
 */
NumberField.prototype.setValue = function(value) {
	var v = value;
	v = this.adjustScale(v);
	if (this.commaFormat) {
		v = this.addComma(v);
	}
	Field.prototype.setValue.call(this, v);
};

/**
 * 値を取得します。
 * @return {String} 値。
 */
NumberField.prototype.getValue = function() {
	var ret = Field.prototype.getValue.call(this);
	return this.delComma(ret);
};


/**
 * マップ中の対応フィールドを比較します。
 * @param a {Object} 比較対象のマップ1。
 * @param b {Object} 比較対象のマップ2。
 * @returns {Number} 比較結果。
 */
NumberField.prototype.comp = function(a, b) {
	var ret = 0;
	if (parseFloat(a[this.id].toString()) < parseFloat(b[this.id].toString())) {
		ret = -1;
	} else if (parseFloat(a[this.id].toString()) > parseFloat(b[this.id].toString())) {
		ret = 1;
	}
	return ret;
};

