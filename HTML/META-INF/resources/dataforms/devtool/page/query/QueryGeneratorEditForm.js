/**
 * @fileOverview {@link QueryGeneratorEditForm}クラスを記述したファイルです。
 */

/**
 * @class QueryGeneratorEditForm
 *
 * @extends EditForm
 */
QueryGeneratorEditForm = createSubclass("QueryGeneratorEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
QueryGeneratorEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
	var thisForm = this;
	this.find("#selectAll").click(function() {
		if (thisForm.mode == "confirm") {
			return false;
		}
		var ck = $(this).prop("checked");
		thisForm.find("[id$='sel']").each(function() {
			$(this).prop("checked", ck);
		});
		thisForm.find("[id$='selectTableClass']").each(function() {
			$(this).prop("checked", ck);
		});
		thisForm.getComponent("selectFieldList").disableDuplicate();
	});
};

/**
 * フォームに対してデータを設定します。
 * @param {Object} data 設定するデータ。
 */
QueryGeneratorEditForm.prototype.setFormData = function(data) {
	var thisForm = this;
	EditForm.prototype.setFormData.call(this, data);
	if (data.joinTableList != null || data.leftJoinTableList != null || data.rightJoinTableList != null) {
		thisForm.submit("getJoinCondition", function(r) {
			currentPage.resetErrorStatus();
			if (r.status == ServerMethod.SUCCESS) {
				logger.log("field list=" + JSON.stringify(r.result));
				thisForm.setJoinCondition(r.result);
			}		
		});
	}
};

/**
 * フィールドリストを取得します。
 */
QueryGeneratorEditForm.prototype.getFieldList = function() {
	var thisForm = this;
	this.submit("getFieldList", function(r) {
		currentPage.resetErrorStatus();
		if (r.status == ServerMethod.SUCCESS) {
			logger.log("field list=" + JSON.stringify(r.result));
			var ftbl = thisForm.getComponent("selectFieldList");
			ftbl.setTableData(r.result);
			thisForm.submit("getJoinCondition", function(r) {
				currentPage.resetErrorStatus();
				if (r.status == ServerMethod.SUCCESS) {
					logger.log("field list=" + JSON.stringify(r.result));
					thisForm.setJoinCondition(r.result);
				}		
			});
		}
	});
};

/**
 * テーブルクラス入力時のフィールドリスト取得。
 * @param {jQuery} f 更新されたフィールド。 
 */
QueryGeneratorEditForm.prototype.onCalc = function(f) {
	EditForm.prototype.onCalc.call(this);
	var thisForm = this;
	if (f != null) {
		this.getFieldList();
	}
};

/**
 * 指定されたJOINテーブルの結合時要件を表示します。
 * @param {Table} table HTMLテーブルのインスタンス。
 * @param {Array} list 結合条件リスト。
 */
QueryGeneratorEditForm.prototype.setJoinConditionToTable = function(table, list) {
	if (list != null) {
		var cf = table.getColumnField("joinCondition");
		for (var i = 0; i < list.length; i++) {
			var f = table.getRowField(i, cf);
			f.setValue(list[i].joinCondition);
		}
	}
};

/**
 * 結合条件を表示します。
 * @param {Object} data データ。
 */
QueryGeneratorEditForm.prototype.setJoinCondition = function(data) {
	this.setJoinConditionToTable(this.getComponent("joinTableList"), data.joinTableList);
	this.setJoinConditionToTable(this.getComponent("leftJoinTableList"), data.leftJoinTableList);
	this.setJoinConditionToTable(this.getComponent("rightJoinTableList"), data.rightJoinTableList);
};

