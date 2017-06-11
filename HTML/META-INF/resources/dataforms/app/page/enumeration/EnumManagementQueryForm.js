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
	
	var thisForm = this;
	this.find("#exportButton").click(function() {
		thisForm.exportData()
	});
};


EnumManagementQueryForm.prototype.exportData = function() {
	var thisForm = this;
	this.submit("export", function(data) {
		currentPage.alert(null, data.result);
	});
};