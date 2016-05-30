/**
 * @fileOverview {@link WebResourceForm}クラスを記述したファイルです。
 */

/**
 * @class WebResourceForm
 * Webリソース作成フォームクラス。
 * @extends QueryForm
 */
WebResourceForm = createSubclass("WebResourceForm", {}, "Form");

/**
 * HTMLエレメントフォームとの対応付けを行います。
 *
 */
WebResourceForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var thisForm = this;
	this.find("#generateButton").click(function() {
		thisForm.generate();
	});
	this.find('#closeButton').click(function() {
		thisForm.parent.close();
	});
	this.find("#generateHtml").click(function() {
		thisForm.generateHtml();
	});
	this.find("#generateJavascript").click(function() {
		thisForm.generateJavascript();
	});
};



/**
 * フォームに対してデータを設定します。
 * @param {Object} data 表示データ。
 */
WebResourceForm.prototype.setFormData = function(data) {
	logger.log("setFormData:" + JSON.stringify(data));

	this.find("#generateHtml").prop("checked", false);
	this.find("#generateJavascript").prop("checked", false);
	if (data.className != null) {
		data.htmlPath = "/" + data.className.replace(/\./g, "/") + ".html";
		data.javascriptPath = "/" + data.className.replace(/\./g, "/") + ".js";
	}
	Form.prototype.setFormData.call(this, data);
	if (data.htmlStatus == "0" || data.htmlStatus == "1") {
		this.find("#htmlTr").show();
		this.find("#generateHtmlButton").show();
		var type = this.find("#webComponentType").val();
		this.find("#noFormContent").prop("checked", false);
		this.find("#outputFormHtml").show();
		this.find("label[for='outputFormHtml']").show();
	} else {
		this.find("#htmlTr").hide();
	}
	if (data.javascriptStatus == "0" || data.javascriptStatus == "1") {
		this.find("#generateJavascriptButton").show();
	} else {
		this.find("#generateJavascriptButton").hide();
	}


};

/**
 * HTML作成を行います。
 */
WebResourceForm.prototype.generateHtml = function() {
	var thisForm = this;
	this.submit("generateHtml", function(ret) {
		thisForm.parent.resetErrorStatus();
		if (ret.status == ServerMethod.SUCCESS) {
			alert(ret.result);
		} else if (ret.status == ServerMethod.INVALID) {
			thisForm.parent.setErrorInfo(thisForm.getValidationResult(ret), thisForm);
		}
	});
};



/**
 * javascriptの作成を行います。
 */
WebResourceForm.prototype.generateJavascript = function() {
	var thisForm = this;
	this.submit("generateJavascript", function(ret) {
		thisForm.parent.resetErrorStatus();
		if (ret.status == ServerMethod.SUCCESS) {
			alert(ret.result);
		} else if (ret.status == ServerMethod.INVALID) {
			thisForm.parent.setErrorInfo(thisForm.getValidationResult(ret), thisForm);
		}
	});
};

