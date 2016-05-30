/**
 * @fileOverview {@link WebResourceQueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class WebResourceQueryResultForm
 * Webリソース検索フォームクラス。
 * <pre>
 * 指定されたページ中のWebコンポーネントを検索するためのフォームです。
 * </pre>
 * @extends QueryResultForm
 */
WebResourceQueryResultForm = createSubclass("WebResourceQueryResultForm", {}, "QueryResultForm");

/**
 * HTMLエレメントフォームとの対応付けを行います。
 *
 */
WebResourceQueryResultForm.prototype.attach = function() {
	QueryForm.prototype.attach.call(this);
	var thisForm = this;
};


/**
 * 問い合わせ結果を表示します。
 * @param {Object} result 問い合わせ結果。
 */
WebResourceQueryResultForm.prototype.setFormData = function(result) {
	QueryResultForm.prototype.setFormData.call(this, result);
	var thisForm = this;
	var queryResult = result.queryResult;
	var table = this.getComponent("queryResult");
	if (queryResult != null) {
		for (var i = 0; i < queryResult.length; i++) {
			var id = "queryResult[" + i + "].className";
			this.find("#" + this.selectorEscape(id)).click(function() {
				var classname = table.getSameRowField($(this), "className").html();
				var webComponentType = table.getSameRowField($(this), "webComponentType").val();
				var htmlStatus = table.getSameRowField($(this), "htmlStatus").val();
				var javascriptStatus = table.getSameRowField($(this), "javascriptStatus").val();
				var javascriptClass = table.getSameRowField($(this), "javascriptClass").val();
				var data = {};
				data.className = classname;
				data.webComponentType = webComponentType;
				data.htmlStatus = htmlStatus;
				data.javascriptStatus = javascriptStatus;
				data.javascriptClass = javascriptClass;
				var dlg = thisForm.parent.getComponent("webResourceDialog");
				var f = dlg.getComponent("webResourceForm");
				data.webSourcePath = f.find("#webSourcePath").val();
				f.setFormData(data);
				dlg.showModal({width: 850});

			});
		}
	}
};

