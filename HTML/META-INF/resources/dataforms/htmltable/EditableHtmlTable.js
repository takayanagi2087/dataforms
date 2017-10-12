/**
 * @fileOverview {@link EditableHtmlTable}クラスを記述したファイルです。
 */

/**
 * @class EditableHtmlTable
 * 編集可能なHTMLテーブルクラス。
 * <pre>
 * 行の追加、削除、ドラックによる順序変更をサポートします。
 * </pre>
 * @extends HtmlTable
 */
EditableHtmlTable = createSubclass("EditableHtmlTable", {}, "HtmlTable");


/**
 * ソート切替モードかどうがの判定を行います。
 * 
 * @reutrns {Boolean} ソート切替モードの場合true。
 */
EditableHtmlTable.prototype.isSortableSwitching = function() {
	if (!this.sortableSwitching) {
		if ("ontouchstart" in window) {
			return true;
		} else {
			return false;
		}
	} else {
		return true;
	}
};


/**
 * 行入替機能を有効にします。
 *
 */
EditableHtmlTable.prototype.enableSortable = function() {
	var thisTable = this;
	if (this.isSortable != true) {
		this.get().find("tbody").sortable({
			start:function(event, ui) {
			},
			update:function(event, ui) {
				var cnt = thisTable.find("tbody>tr").size();
				var lasttr = thisTable.find("tbody>tr:last");
				var sel = "[id^='" + thisTable.selectorEscape(thisTable.id + "[" + (cnt - 1) + "]") + "']";
				thisTable.resetIdIndex();
			},
			axis: "y"
		});
		this.isSortable = true;
	}
};

/**
 * 行入替機能を無効にします。
 *
 */
EditableHtmlTable.prototype.disableSortable = function() {
	if (this.isSortable == true) {
		this.get().find("tbody").sortable("destroy");
		this.isSortable = false;
	}
};


/**
 * ロック処理時のソート設定の制御。
 */
EditableHtmlTable.prototype.lockSortable = function(lk) {
	if (this.sortable) {
		if (!this.isSortableSwitching()) {
			// sorableの切り替えをしない。
			if (lk) {
				this.disableSortable();
			} else {
				this.enableSortable();
			}
		} else {
			var ckid = this.id + ".sortable";
			var ck = this.parent.find("#" + this.selectorEscape(ckid));
			var sort = ck.prop("checked");
			if (sort) {
				// sortのチェックがOnの時のみ切り替える。
				if (lk) {
					this.disableSortable();
					
				} else {
					this.enableSortable();
				}
			}
			if (lk) {
				ck.prop("disabled", true);
			} else {
				ck.prop("disabled", false);
			}
		}
	}
};


/**
 * 行入替可能なテーブルに設定します。
 */
EditableHtmlTable.prototype.makeSortable = function() {
	var thisTable = this;
	var ckid = this.id + ".sortable";
	this.enableSortable();
	if (this.isSortableSwitching()) {
		// タッチパネルの場合は行入替のOn/Off切り替え機能が必要。
		var label = MessagesUtil.getMessage("message.table.sortablelabel");
		var cb = '<input type="checkbox" id="' + ckid + '"><label for="' + ckid + '">' + label + '</label>';
//		this.get().before(cb);
		this.find("thead th:first").append(cb);
		this.parent.find("#" + this.selectorEscape(ckid)).click(function() {
			if ($(this).prop("checked")) {
				thisTable.find("[id$='\\.addButton']").prop("disabled", true);
				thisTable.find("[id$='\\.deleteButton']").prop("disabled", true);
				thisTable.enableSortable();
			} else {
				thisTable.find("[id$='\\.addButton']").prop("disabled", false);
				thisTable.find("[id$='\\.deleteButton']").prop("disabled", false);
				thisTable.disableSortable();
			}
			return true;
		});
		this.disableSortable();
	}
};

/**
 * 各エレメントとの対応付け.
 */
EditableHtmlTable.prototype.attach = function() {
	var thisTable = this;
	HtmlTable.prototype.attach.call(this);
	if (!this.readonly) {
		this.find("tfoot").find("[id$='\\.addButton']").click(function() {
			thisTable.addRow(null);
			return false;
		});
		if (thisTable.sortable) {
			thisTable.makeSortable();
		}
	}
};



/**
 * ソートはサポートしない。
 * @param col {jQuery} ソート対象カラム。
 * @returns 常にnull。
 */
EditableHtmlTable.prototype.sortTable = function(col) {
	return null;
};



/**
 * 「+」「-」ボタンの表示/非表示.
 * @param lk 非表示にする場合true.
 */
EditableHtmlTable.prototype.lockEditButton = function(lk) {
	var ckid = this.id + ".sortable";
	var addbtn = this.find("[id$='\\.addButton']");
	var delbtn = this.find("[id$='\\.deleteButton']");
	if (lk) {
		addbtn.hide();
		delbtn.hide();
		this.find("#" + this.selectorEscape(ckid)).hide();
		this.find("#" + this.selectorEscape(ckid)).next("label").hide();
	} else {
		addbtn.show();
		delbtn.show();
		this.find("#" + this.selectorEscape(ckid)).show();
		this.find("#" + this.selectorEscape(ckid)).next("label").show();
	}
	this.lockSortable(lk);
};

/**
 * テーブル中のフィールドのロック/アンロックを行う.
 * @param lk ロックする場合true.
 */
EditableHtmlTable.prototype.lockFields = function(lk) {
	if (this.readonly) {
		HtmlTable.prototype.lockFields.call(this, true);
		this.lockEditButton(true);
	} else {
		HtmlTable.prototype.lockFields.call(this, lk);
		this.lockEditButton(lk);
	}
}


/**
 * 各行のid中のインデックスを整列する.
 */
EditableHtmlTable.prototype.resetIdIndex = function() {
	var thisTable = this;
	var trlist = thisTable.find("tbody>tr,tfoot>tr");
	for (var i = 0; i < trlist.size(); i++) {
		var c = $(trlist.get(i)).find("[id^='" + thisTable.id + "\\[']");
		c.each(function() {
			var id = $(this).attr("id");
			var newid = id.replace(new RegExp(thisTable.id + "\\[.+?\\]"), thisTable.id + "[" + i + "]")
			$(this).attr("id", newid);
			var name = $(this).attr("name");
			if (name != null) {
				var newname = name.replace(new RegExp(thisTable.id + "\\[.+?\\]"), thisTable.id + "[" + i + "]")
				$(this).attr("name", newname);
			}
		});
	}
	this.resetRowNo();
	// IDを作り直したのでdatepickerを設定し直す.
	var n = this.find("tbody>tr").length;
	for (var lidx = 0; lidx < n; lidx++) {
		for (var i = 0; i < this.fields.length; i++) {
			var f = this.getRowField(lidx, this.fields[i]);
			f.onIdChange();
		}
	}
}

/**
 * 行追加時に呼び出されるメソッドです。
 * <pre>
 * 行中のボタンなどにイベント処理を登録します。
 * </pre>
 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
 */
EditableHtmlTable.prototype.onAddTr = function(rowid) {
	var thisTable = this;
	thisTable.find("#" + this.selectorEscape(rowid + ".addButton")).click(function() {
		thisTable.addRow(this);
		return false;
	});
	thisTable.find("#" + this.selectorEscape(rowid + ".deleteButton")).click(function() {
		thisTable.deleteRow(this);
		return false;
	});
};


/**
 * 行を追加します。
 * @param {Object} rowinfo 追加ボタンまたは追加する行。
 * <pre>
 * 	rowinfoには挿入する行のElementまたはjQueryオブジェクトまたは行のインデックス値を指定します。
 * 	rowinfoにnullを設定すると最終行に追加されます。
 * </pre>
 */
EditableHtmlTable.prototype.addRow = function(rowinfo) {
	var thisTable = this;
	var rowIndex = null;
	if (rowinfo != null) {
		if (rowinfo instanceof Element) {
			rowIndex = thisTable.getRowIndex($(rowinfo));
		} else if (typeof rowinfo.attr == "function") {
			rowIndex = thisTable.getRowIndex(rowinfo);
		} else {
			rowIndex = rowinfo;
		}
	}
	if (this.getRowCount() == 0) {
		rowIndex = null;
	}
	var lidx = thisTable.addTr(rowIndex);
	this.onAddTr(thisTable.id + "[" + lidx + "]");
	this.resetIdIndex();
};

/**
 * 行を削除します。
 * @param {Element} btn 削除ボタン。
 */
EditableHtmlTable.prototype.deleteRow = function(btn) {
	var thisTable = this;
	var rowIndex = thisTable.getRowIndex($(btn));
	for (var i = 0; i < this.fields.length; i++) {
		var f = this.getRowField(rowIndex, this.fields[i]);
		f.onDestroy();
	}
	thisTable.find("tbody>tr:eq(" + rowIndex + ")").remove();
	this.resetIdIndex();
	var form = this.getParentForm();
	form.onCalc(this.get());
};

/**
 * 行番号等を設定し直します。
 * <pre>
 * 行番号(no),ソート順(sortOrder)の振り直しを行います。
 * </pre>
 * @deprecated resetRowNoを使用してください。
 */
EditableHtmlTable.prototype.resetLineNo = function() {
	var trlist = this.find("[id$=\\.no]");
	for (var i = 0; i < trlist.size(); i++) {
		var lineNoId = this.id + "[" + i + "].no";
		this.find("#" + this.selectorEscape(lineNoId)).text(i + 1);
		var sortOrderId = this.id + "[" + i + "].sortOrder";
		this.find("#" + this.selectorEscape(sortOrderId)).val(i);

	}
	this.resetBackgroundColor();
};


/**
 * 行番号等を設定し直します。
 * <pre>
 * 行番号(no),ソート順(sortOrder)の振り直しを行います。
 * </pre>
 */
EditableHtmlTable.prototype.resetRowNo = function() {
	var trlist = this.find("[id$=\\.no]");
	for (var i = 0; i < trlist.size(); i++) {
		var lineNoId = this.id + "[" + i + "].no";
		this.find("#" + this.selectorEscape(lineNoId)).text(i + 1);
		var sortOrderId = this.id + "[" + i + "].sortOrder";
		this.find("#" + this.selectorEscape(sortOrderId)).val(i);

	}
	this.resetBackgroundColor();
};


/**
 * テーブルに対するテータ設定を行います。
 * @param {Array} list テーブルデータ。
 */
EditableHtmlTable.prototype.setTableData = function(list) {
	var thisTable = this;
	HtmlTable.prototype.setTableData.call(this, list);
	var lastAddButton = this.find("tfoot").find("[id$='\\.addButton']");
	if (lastAddButton.size() == 0) {
		var lidx = this.addTr();
		this.find("#" + this.selectorEscape(this.id + "[" + lidx + "].addButton")).click(function() {
			thisTable.addRow(this);
			return false;
		});
		this.resetRowNo();
		if (list != null) {
			var tdlist = this.find("tbody>tr:eq(" + list.length + ")>td");
			for (var i = 0; i < tdlist.size(); i++) {
				var td = $(tdlist.get(i));
				if (td.find("[id$='addButton']").size() == 0) {
					td.empty();
				}
			}
		}
	} else {
		this.resetRowNo();
	}
};


/**
 * 必須マークを設定します。
 */
EditableHtmlTable.prototype.setRequiredMark = function() {
	for (var i = 0; i < this.fields.length; i++) {
		var el = $(this.trLine).find("#" + this.selectorEscape(this.id + "[0]." + this.fields[i].id));
		if (this.fields[i].isRequired(el)) {
			var lel = this.getLabelElement(this.fields[i]);
			lel.addClass("requiredFieldLabel");
		}

	}
};

/**
 * 各行の背景色を設定します。
 *
 */
EditableHtmlTable.prototype.resetBackgroundColor = function() {
	HtmlTable.prototype.resetBackgroundColor.call(this);
	this.find('tr:last').removeClass("evenTr");
	this.find('tr:last').removeClass("oddTr");
};
