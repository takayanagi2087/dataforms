/**
 * @fileOverview {@link DateTimeField}クラスを記述したファイルです。
 */

/**
 * @class DateTimeField
 *
 * 日付/時刻フィールドクラス。
 * <pre>
 * 日付/時刻フィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
DateTimeField = createSubclass("DateTimeField", {}, "Field");


/**
 * 日付の表示フォーマットを指定します。
 * @param {String} displayFormat 表示時のフォーマット。
 * @param {String} editFormat 編集時のフォーマット。
 */
DateTimeField.prototype.setFormat = function(displayFormat, editFormat) {
	this.get().focus(function() {
		var v = $(this).val();
		var fmt = new SimpleDateFormat(displayFormat);
		var efmt = new SimpleDateFormat(editFormat);
		var ev = fmt.parse(v);
		if (ev != null) {
			$(this).val(efmt.format(ev));
		}
	});
	this.get().blur(function() {
		var v = $(this).val();
		var fmt = new SimpleDateFormat(displayFormat);
		var efmt = new SimpleDateFormat(editFormat);
		var ev = efmt.parse(v);
		if (ev != null) {
			$(this).val(fmt.format(ev));
		}
	});
};

