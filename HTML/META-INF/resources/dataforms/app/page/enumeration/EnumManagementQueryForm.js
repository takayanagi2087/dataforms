/**
 * @fileOverview {@link EnumManagementQueryForm}クラスを記述したファイルです。
 */

/**
 * @class EnumManagementQueryForm
 *
 * @extends QueryForm
 */
EnumManagementQueryForm = createSubclass("EnumManagementQueryForm", {}, "QueryForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumManagementQueryForm.prototype.attach = function() {
	QueryForm.prototype.attach.call(this);
	
	logger.log("userInfo=" + JSON.stringify(currentPage.userInfo));
	if (currentPage.userInfo.userLevel == "developer") {
		var thisForm = this;
		this.find("#exportButton").click(function() {
			thisForm.exportData()
		});
	} else {
		this.find("#exportButton").remove();
	}
};

/**
 * データのエクスポートを行います。
 */
EnumManagementQueryForm.prototype.exportData = function() {
	var thisForm = this;
	currentPage.confirm(null, MessagesUtil.getMessage("message.dexportAsInitialDataConfirm"), function() {
		thisForm.submit("export", function(data) {
			currentPage.alert(null, data.result);
		});
	});
};