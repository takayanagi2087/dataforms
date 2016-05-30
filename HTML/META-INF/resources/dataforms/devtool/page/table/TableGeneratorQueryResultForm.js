/**
 * @fileOverview {@link TableGeneratorQueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class TableGeneratorQueryResultForm
 *
 * @extends QueryResultForm
 */
TableGeneratorQueryResultForm = createSubclass("TableGeneratorQueryResultForm", {}, "QueryResultForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
TableGeneratorQueryResultForm.prototype.attach = function() {
	QueryResultForm.prototype.attach.call(this);
}

/**
 * 問い合わせ結果を表示します。
 * @param {Object} result 問い合わせ結果。
 */
TableGeneratorQueryResultForm.prototype.setFormData = function(result) {
	QueryResultForm.prototype.setFormData.call(this, result);
	var thisForm = this;
	var queryResult = result.queryResult;
	if (queryResult != null) {
		for (var i = 0; i < queryResult.length; i++) {
			logger.log("statusVal=" + queryResult[i].statusVal + ",differenceVal=" + queryResult[0].differenceVal);
			if (queryResult[i].differenceVal == "1") {
				this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
			}
			if (queryResult[i].statusVal == "0") {
				this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
			}
		}
	}
};
