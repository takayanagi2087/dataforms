/**
 * @fileOverview {@link EnumGroupEditForm}クラスを記述したファイルです。
 */

/**
 * @class EnumGroupEditForm
 *
 * @extends EditForm
 */
EnumGroupEditForm = createSubclass("EnumGroupEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumGroupEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
};
/*
EnumGroupEditForm.prototype.setFormData = function(data) {
	EditForm.prototype.setFormData.call(this, data)
	var gc = this.getComponent("enumGroupCode");
	logger.log("saveMode=" + this.saveMode);
	if (this.saveMode == "update") {
		gc.lock(true);
	} else {
		gc.lock(false);
	}
};*/