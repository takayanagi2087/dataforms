/**
 * @fileOverview {@link DisplayedRequiredValidator}クラスを記述したファイルです。
 */

/**
 * @class DisplayedRequiredValidator
 *
 * @extends RequiredValidator
 */
DisplayedRequiredValidator = createSubclass("DisplayedRequiredValidator", {}, "RequiredValidator");


/**
 * HTMLエレメントとの対応付けを行います。
 */
DisplayedRequiredValidator.prototype.attach = function() {
	RequiredValidator.prototype.attach.call(this);
};


/**
 * バリデーションを行ないます。
 * @param {String} v 値。
 * @returns {Boolean} バリデーション結果。
 */
DisplayedRequiredValidator.prototype.validate = function(v) {
	var f = this.getParentForm();
	var vflg = f.find("#" + this.selectorEscape(this.fieldId)).is(":visible");
	logger.log("vflg=" + vflg);
	if (vflg) {
		return (this.isBlank(v) == false);
	} else {
		return true;
	}
};
