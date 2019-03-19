/**
 * @fileOverview {@link EnumMasterEditForm}クラスを記述したファイルです。
 */

/**
 * @class EnumMasterEditForm
 *
 * @extends EnumManagementEditForm
 */
EnumMasterEditForm = createSubclass("EnumMasterEditForm", {}, "EnumManagementEditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumMasterEditForm.prototype.attach = function() {
	EnumManagementEditForm.prototype.attach.call(this);
	var thisForm = this;
	var optionNameList = this.getComponent("optionNameList");
	optionNameList.onAddTr = function(rowid) {
		thisForm.setMultiLanguageMode();
	};
};

/**
 * 多言語モードの設定を行います。
 */
EnumMasterEditForm.prototype.setMultiLanguageMode = function() {
	logger.log("multiLanguage=" + this.multiLanguage);
	if (!this.multiLanguage) {
		this.find(".langCode").hide();
	} else {
		this.find(".langCode").show();
	}
};

/**
 * フォームのデータを設定します。
 * @param {Object} data データ。
 */
EnumMasterEditForm.prototype.setFormData = function(data) {
	EnumManagementEditForm.prototype.setFormData.call(this, data);
	this.setMultiLanguageMode();
};

