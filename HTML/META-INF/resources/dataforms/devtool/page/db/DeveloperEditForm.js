/**
 * @fileOverview {@link DeveloperEditForm}クラスを記述したファイルです。
 */

/**
 * @class DeveloperEditForm
 *
 * @extends EditForm
 */
DeveloperEditForm = createSubclass("DeveloperEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
DeveloperEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
	if (this.userInfoDataExists) {
		this.find("#flagDiv").show();
	} else {
		this.find("#flagDiv").hide();
	}
	var thisForm = this;
	this.find("#userImportFlag").click(function() {
		if ($(this).prop("checked")) {
			thisForm.find("#userInfoTable").hide();
		} else {
			thisForm.find("#userInfoTable").show();
		}
	});
};

DeveloperEditForm.prototype.validate = function() {
	if (this.find("#userImportFlag").prop("checked")) {
		return true;
	} else {
		return Form.prototype.validate.call(this);
	}
};
