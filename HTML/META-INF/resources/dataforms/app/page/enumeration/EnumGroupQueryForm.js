/**
 * @fileOverview {@link EnumGroupQueryForm}クラスを記述したファイルです。
 */

/**
 * @class EnumGroupQueryForm
 *
 * @extends QueryForm
 */
EnumGroupQueryForm = createSubclass("EnumGroupQueryForm", {}, "QueryForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
EnumGroupQueryForm.prototype.attach = function() {
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
EnumGroupQueryForm.prototype.exportData = function() {
	var thisForm = this;
	currentPage.confirm(null, MessagesUtil.getMessage("message.dexportAsInitialDataConfirm"), function() {
		thisForm.submit("export", function(data) {
			currentPage.alert(null, data.result);
		});
	});
};
