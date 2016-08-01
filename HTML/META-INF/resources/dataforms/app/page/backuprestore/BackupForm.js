/**
 * @fileOverview {@link BackupForm}クラスを記述したファイルです。
 */

/**
 * @class BackupForm
 *
 * @extends Form
 */
BackupForm = createSubclass("BackupForm", {}, "Form");


/**
 * HTMLエレメントとの対応付けを行います。
 */
BackupForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var thisForm = this;
	this.find("#backupButton").click(function() {
		thisForm.backup();
	});
};

/**
 * バックアップ処理。
 */
BackupForm.prototype.backup = function() {
	var thisForm = this;
	this.submitForDownload("backup", function(r) {
		if (r.status == ServerMethod.INVALID) {
			currentPage.setErrorInfo(thisForm.getValidationResult(r), thisForm);
		} else {
			var systemname = MessagesUtil.getMessage("message.systemname");
			currentPage.alert(systemname, r.result);
		}
	});
};

