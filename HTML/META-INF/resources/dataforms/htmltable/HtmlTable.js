/**
 * @fileOverview {@link HtmlTable}クラスを記述したファイルです。
 */
/**
 * @class HtmlTable
 * HTMLテーブルクラス。
 * <pre>
 * このクラスに対応するHTMLの記述は以下のようになります。
 * 以下の例はid="queryResult"の場合です。
 * <code>
 *	&lt;table id=&quot;queryResult&quot;&gt;
 *		&lt;thead&gt;
 *			&lt;tr&gt;
 *				&lt;th&gt;No.&lt;/th&gt;
 *				&lt;th&gt;&#12525;&#12464;&#12452;&#12531;ID&lt;/th&gt;
 *				&lt;th&gt;&#27663;&#21517;&lt;/th&gt;
 *				&lt;th&gt;&#12518;&#12540;&#12470;&#12524;&#12505;&#12523;&lt;/th&gt;
 *				&lt;th class=&quot;deleteColumn&quot;&gt;&#21066;&#38500;&lt;/th&gt;
 *			&lt;/tr&gt;
 *		&lt;/thead&gt;
 *		&lt;tbody&gt;
 *			&lt;tr&gt;
 *				&lt;td style=&quot;text-align:right;&quot;&gt;
 *					&lt;span id=&quot;queryResult[0].rowNo&quot; &gt;&lt;/span&gt;
 *					&lt;input type=&quot;hidden&quot; id=&quot;queryResult[0].userId&quot;&gt;
 *				&lt;/td&gt;
 *				&lt;td&gt;
 *					&lt;a id=&quot;queryResult[0].updateButton&quot; href=&quot;javascript:void(0);&quot;&gt;&lt;span id=&quot;queryResult[0].loginId&quot;&gt;&lt;/span&gt;&lt;/a&gt;
 *				&lt;/td&gt;
 *				&lt;td&gt;&lt;span id=&quot;queryResult[0].userName&quot;&gt;&lt;/span&gt;&lt;/td&gt;
 *				&lt;td&gt;&lt;span id=&quot;queryResult[0].userLevelName&quot;&gt;&lt;/span&gt;&lt;/td&gt;
 *				&lt;td class=&quot;deleteColumn&quot;&gt;
 *					&lt;input type=&quot;button&quot; id=&quot;queryResult[0].deleteButton&quot; value=&quot;&#21066;&#38500;&quot;&gt;
 *				&lt;/td&gt;
 *			&lt;/tr&gt;
 *		&lt;/tbody&gt;
 *	&lt;/table&gt;
 * </code>
 * </pre>
 * @extends WebComponent
 * @prop fields {Array} fieldListのクラス情報を元に作成した、Fieldクラスのインスタンスです。
 *
 */
HtmlTable = createSubclass("HtmlTable", {fields:[]}, "WebComponent");


/**
 * 所有コンポーネントのインスタンスを取得します。
 * <pre>
 * テーブル中のフィールドのインスタンスは行情報を持たないものをthis.fieldsに保持しています。
 * 指定されたidが"tableid[0].fieldid"の形式で行情報が指定されていた場合、this.fields中の対応フィールドのコピーを作成し、
 * そのインスタンスのidを"tableid[0].fieldid"に設定したものを返します。
 * 指定されたidが"fieldid"の形式で行情報が指定されてない場合、this.fields中の対応フィールドを返します。
 * </pre>
 * @param {String} id 所有オブジェクトのID。
 * @returns {WebComponent} 所有オブジェクトのインスタンス。
 */
HtmlTable.prototype.getComponent = function (id) {
	var tblid = this.getHtmlTableId(id);
	if (tblid != null) {
		var colid = this.getHtmlTableColumnId(id);
		var tbl = this;
		for (var i = 0; i <  tbl.fields.length; i++) {
			if (tbl.fields[i].id == colid) {
				var tblfield = $.extend(true, {}, tbl.fields[i]);
				tblfield.id = id;
				return tblfield;
			}
		}
	} else {
		return this.componentMap[id];
	}
};


/**
 * テーブル中のフィールドに対応したラベルを取得します。
 * @param {Field} field フィールド。
 * @returns {jQuery} ラベルのエレメント.
 */
HtmlTable.prototype.getLabelElement = function(field) {
	var label = null;
	var tblid = this.id;
//	var tag = this.parent.find('#' + this.selectorEscape(field.labelId));
	var fid = field.id;
	if (this.isHtmlTableElementId(fid)) {
		fid = this.getHtmlTableColumnId(fid);
	}
	var tag = this.parent.find("label[for='" + fid + "']");

	if (tag.length > 0) {
		// ラベルのIDが指定されていた場合の処理.
		label = tag;
	} else {
		var theadTr = this.parent.find("#" + tblid).find("thead tr:last");
		var tbodyTr = this.parent.find("#" + tblid).find("tbody tr:first");
		if (tbodyTr.length == 0) {
			tbodyTr = $("<tr>" + this.trLine + "</tr>");
		}
		var tdlist = tbodyTr.children();
		var idx = -1;
		for (var i = 0; i < tdlist.length; i++) {
			var comp = $(tdlist[i]).find("[id$='" + field.id + "']");
			if (comp.length > 0) {
				idx = i;
				break;
			}
		}
		if (idx >= 0) {
			var thlist = theadTr.find("th")
			var hidx = idx;
			for (var i = 0; i < idx && i < thlist.length; i++) {
				var colspan = thlist.eq(i).attr("colspan");
				if (colspan != null) {
					var cs = parseInt(colspan);
					hidx -= (cs - 1);
				}
			}
			label = $(theadTr.children()[hidx]);
		}
	}
	return label;
};

/**
 * フィールドに対応したラベル文字列を取得します。
 * @param {Field} field フィールド。
 * @returns {String} ラベル文字列。
 */
HtmlTable.prototype.getLabel = function(field) {
	var el = this.getLabelElement(field);
	if (el != null) {
		return el.html();
	} else {
		return null;
	}
};


/**
 * フィールドの初期化を行います。
 *
 * @param {Array} fieldList フィールドリスト。
 *
 */
HtmlTable.prototype.initField = function(fieldList) {
	for (var i = 0; i < fieldList.length; i++) {
		var f = fieldList[i];
		var field = this.newInstance(f);
		this.fields[i] = field;
	}
};


/**
 * HTMLテーブルを初期化します。
 *
 */
HtmlTable.prototype.init = function() {
	WebComponent.prototype.init.call(this);
	this.initField(this.fieldList);
};

/**
 * カラムソートイベントを設定します。
 */
HtmlTable.prototype.setColumnSortEvent = function() {
	var thisTable = this;
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		field.label = this.getLabel(field);
		if (field.sortable) {
			var el = this.getLabelElement(field);
			if (el != null) {
				el.click(function() {
					thisTable.sortTable($(this));
				});
			}
		}
	}
};

/**
 * テーブルの各カラム幅を設定します。
 * @param {String} tag "thead"または"tfoot"。
 * @param {Array} warray カラム幅の配列。
 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
 */
HtmlTable.prototype.setColumnWidth = function(tag, warray, warrayAll) {
	this.find(tag + " tr").each(function () {
		var i = 0;
		$(this).children().each(function() {
			var colspan = $(this).attr("colspan");
			if (colspan === undefined) {
				$(this).width(warray[i++]);
			} else {
				var w = 0;
				var cnt = parseInt(colspan);
				for (var c = 0; c < cnt; c++) {
					if (c < (cnt - 1)) {
						w += warrayAll[i++];
					} else {
						w += warray[i++];
					}
				}
				$(this).width(w);
			}
		});
	});
};

/**
 * ヘッダのカラム幅を設定します。
 * @param {Array} warray カラム幅の配列。
 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
 */
HtmlTable.prototype.setHeaderColumnWidth = function(warray, warrayAll) {
	this.setColumnWidth("thead", warray, warrayAll);
};

/**
 * フッタのカラム幅を設定します。
 * @param {Array} warray カラム幅の配列。
 * @param {Array} warrayAll カラム幅の配列(padding, borderを含めた幅)。
 */
HtmlTable.prototype.setFooterColumnWidth = function(warray, warrayAll) {
	this.setColumnWidth("tfoot",warray, warrayAll);
};

/**
 * thead,tfoot用の固定カラム設定。
 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
 * @param {Number} cols 固定カラム数。
 * @param {Array} warray カラム幅の配列。
 */
HtmlTable.prototype.lockColumn = function(tr, cols, warray) {
	var idx = 0;
	var pos = 0;
	tr.children().each(function() {
		if (idx < cols) {
			$(this).addClass("fixedColumn");
			/*$(this).css("top", "0px");*/
			$(this).css("left", pos + "px");
			var colspan = $(this).prop("colspan");
			logger.log("colspan=" + colspan);
			if (colspan == null) {
				colspan = 1;
			}
			for (var i = 0; i < colspan; i++) {
				pos += warray[idx++];
			}
			$(this).css("z-index", "3");
		}
	});
};


/**
 * thead用の固定カラム設定。
 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
 * @param {Number} cols 固定カラム数。
 * @param {Array} warray カラム幅の配列。
 */
HtmlTable.prototype.setTheadFixedColumn = function(tr, cols, warray) {
	this.lockColumn(tr, cols, warray);
};


/**
 * tbody用の固定カラム設定。
 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
 * @param {Number} cols 固定カラム数。
 * @param {Array} warray カラム幅の配列。
 */
HtmlTable.prototype.setTbodyFixedColumn = function(tr, cols, warray) {
	var idx = 0;
	var pos = 0;
	tr.children().each(function() {
		if (idx < cols) {
			$(this).addClass("fixedColumn");
			$(this).css("left", pos + "px");
			pos += warray[idx++];
			$(this).css("z-index", "1");
		}
	});
};

/**
 * tfoot用の固定カラム設定。
 * @param {jQuery} tr 設定するtrのjQueryオブジェクト。
 * @param {Number} cols 固定カラム数。
 * @param {Array} warray カラム幅の配列。
 */
HtmlTable.prototype.setTfootFixedColumn = function(tr, cols, warray) {
	this.lockColumn(tr, cols, warray);
};

/**
 * 固定カラムを設定する。
 * @param {Number} col 固定カラム数。
 * @param {Array} warray カラム幅の配列。
 */
HtmlTable.prototype.setFixedColumn = function(cols, warray) {
	var thisTable = this;
	var idx = 0;
	var pos = 0;
	this.find("thead tr").each(function() {
		thisTable.setTheadFixedColumn($(this), cols, warray);
	});
	this.find("tbody tr").each(function() {
		thisTable.setTbodyFixedColumn($(this), cols, warray);
	});
	this.find("tfoot tr").each(function() {
		thisTable.setTfootFixedColumn($(this), cols, warray);
	});
};

/**
 * 指定されたカラムの幅を取得します。
 * @param {jQuery} col カラムのjQueryオブジェクト。
 */
HtmlTable.prototype.getColumnWidth = function(col) {
	return col.outerWidth(true);
};

/**
 * 固定カラム用のスタイル設定を行います。
 */
HtmlTable.prototype.setFixedColumnStyle = function() {
	var sd = this.get().closest("div.hScrollDiv");
	this.get().closest("div.hScrollDiv").css("overflow-x", "hidden");
	this.get().addClass("columnFixedTable");
	var wbody = 0;
	var warray = [];
	var warrayAll = [];
	var thisTable = this;
	this.find("tbody tr:first").find("td").each(function() {
		warray.push($(this).width());
		var fw = thisTable.getColumnWidth($(this));
		warrayAll.push(fw);
		wbody += fw;
	});
	this.find("thead").width(wbody);
	this.find("tbody").width(wbody);
	this.find("tfoot").width(wbody);
	this.setHeaderColumnWidth(warray, warrayAll);
	this.setFooterColumnWidth(warray, warrayAll);
	if (this.fixedColumns > 0) {
		this.setFixedColumn(this.fixedColumns, warrayAll);
	}
};

/**
 * IE用のカラム位置設定。
 */
HtmlTable.prototype.setColumnLeftForIe = function(tr) {
	var idx = 0;
	var pos = 0;
	var sx = this.get().scrollLeft();
	var thisForm = this;
	tr.children(".fixedColumn").each(function() {
		$(this).css("left", sx + "px");
	});
};

/**
 * IE用のカラム移動。
 */
HtmlTable.prototype.moveColumnForIe = function() {
	this.get().find("th.fixedColumn").css("position", "relative");
	this.get().find("td.fixedColumn").css("position", "relative");
	var thisTable = this;
	this.find("thead tr").each(function() {
		thisTable.setColumnLeftForIe($(this));
	});
	this.find("tbody tr").each(function() {
		thisTable.setColumnLeftForIe($(this));
	});
	this.find("tfoot tr").each(function() {
		thisTable.setColumnLeftForIe($(this));
	});
	
};

/**
 * HTMLエレメントとの対応付けを行います。
 */
HtmlTable.prototype.attach = function() {
	// WebComponent.prototype.attach.call(this);
	logger.log("fixedColumns=" + this.fixedColumns);
	var thisTable = this;
	if (this.fixedColumns >= 0) {
		this.setFixedColumnStyle();
		var ua = navigator.userAgent;
		var isEdge = (ua.indexOf("Edge") >= 0);
		var isIe = ua.match(/(msie|MSIE)/) || ua.match(/(T|t)rident/);
		if (isIe || isEdge) {
			// Edgeはtheadのstickyの動作がおかしいので、theadの固定をスクリプトで行う。
			this.get().find("thead").css("position", "relative");
			thisTable.timeoutId = null;
			this.get().scroll(function() {
				if (thisTable.timeoutId != null) {
					clearTimeout(thisTable.timeoutId);
					thisTable.timeoutId = null;
				}
				thisTable.timeoutId = setTimeout(function () {
					var top = thisTable.get().scrollTop();
					thisTable.get().find("thead").css("top", top);
					if (isIe) {
						thisTable.moveColumnForIe();
					}
				}, 100 ) ;
			});
		}
	}
	this.setSortMark();
	this.setColumnSortEvent();
	var tbl = this.get();
	this.trLine = tbl.find("tbody>tr:first").html();
	this.clear();
};


/**
 * カラムにソートマークを設定する。
 */
HtmlTable.prototype.setSortMark = function() {
	logger.log("setSortMark");
	var thisTable = this;
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		if (field.sortable) {
			var el = this.getLabelElement(field);
			if (el == null) {
				continue;
			}
			logger.log("el.tag=" + el.prop("tagName"));
			var labelspan = MessagesUtil.getMessage("htmltable.sortablelabel");
			var mark = MessagesUtil.getMessage("htmltable.sortable");
			if (field.sortOrder == "ASC") {
				mark = MessagesUtil.getMessage("htmltable.sortedasc");
				el.data("order", "ASC");
			} else if (field.sortOrder == "DESC") {
				mark = MessagesUtil.getMessage("htmltable.sorteddesc");
				el.data("order", "DESC");
			} else {
				mark = MessagesUtil.getMessage("htmltable.sortable");
				el.data("order", "NONE");
			}
			if (el.find(".sortableMark").length == 0) {
				el.contents().wrap(labelspan).before(mark);
				el.data("id", field.id);
			} else {
				el.find(".sortableMark").remove();
				el.find(".sortableLabel").prepend(mark);
			}

		}
	}
};

/**
 * ソート状態を変更する。
 * @param co {jQuery} ラベルのエレメント.l
 */
HtmlTable.prototype.changeSortMark = function(col) {
	var colid = col.data("id");
	var order = col.data("order");
	logger.log("column click=" + col.data("id") + "," + col.data("order"));
	var mark = MessagesUtil.getMessage("htmltable.sortable");
	if (order == "ASC") {
		mark = MessagesUtil.getMessage("htmltable.sorteddesc");
		col.data("order", "DESC");
	} else if (order == "DESC") {
		mark = MessagesUtil.getMessage("htmltable.sortable");
		col.data("order", "NONE");
	} else {
		mark = MessagesUtil.getMessage("htmltable.sortedasc");
		col.data("order", "ASC");
	}
	col.find(".sortableMark").remove();
	col.find(".sortableLabel").prepend(mark);
};

/**
 * ソート対象フィールドのリストを取得します。
 * @returns {Array} ソート対象フィールドのリスト。
 */
HtmlTable.prototype.getSortFieldList = function() {
	var flist = [];
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		if (field.sortable) {
			var col = this.getLabelElement(field);
			if (col != null) {
				field.currentSortOrder = col.data("order");
				logger.log(field.id + ":" + field.currentSortOrder);
				if (field.currentSortOrder == "ASC" || field.currentSortOrder == "DESC") {
					flist.push(field);
				}
			}
		}
	}
	return flist;
};

/**
 * カラムのソート設定に応じて、リストをソートします。
 *
 * @param list ソートするリスト。
 * @returns ソート結果。
 */
HtmlTable.prototype.sort = function(list) {
	var sflg = false;
	for (var i = 0; i < this.fields.length; i++) {
		var field = this.fields[i];
		if (field.sortable) {
			var col = this.getLabelElement(field);
			if (col != null) {
				field.currentSortOrder = col.data("order");
				logger.log(field.id + ":" + field.currentSortOrder);
				if (field.currentSortOrder == "ASC" || field.currentSortOrder == "DESC") {
					sflg = true;
				}
			}
		}
	}
//	logger.log("sflg=" + sflg);
	var slist = list;
	if (sflg) {
		var thisTable = this;
		slist = list.sort(function(a, b) {
			var cmp = 0;
			for (var i = 0; i < thisTable.fields.length; i++) {
				var field = thisTable.fields[i];
				if (field.sortable) {
					if (field.currentSortOrder == "ASC") {
						cmp = field.comp(a, b);
					} else if (field.currentSortOrder == "DESC") {
						cmp = field.comp(b, a);
					}
				}
				if (cmp != 0) {
					return cmp;
				}
			}
			return cmp;
		});
	}
	return slist;
};

/**
 * カラムの設定に従った、ソートされたリストを取得します。
 * @returns {Array} ソートされたリスト。
 */
HtmlTable.prototype.getSortedList = function() {
	var list = this.tableData.concat();
	return this.sort(list);
};

/**
 * ソートを行います。
 * @param co {jQuery} ラベルのエレメント.
 * @return {Array} ソート結果リスト。
 *
 */
HtmlTable.prototype.sortTable = function(col) {
	var thisTable = this;
	this.changeSortMark(col);
	var slist = this.getSortedList();
	this.setTableData(slist);
	return slist;
};

/**
 * テーブルをクリアします。
 */
HtmlTable.prototype.clear = function() {
	// フィールドの解放を行う
	var n = this.find("tbody>tr").length;
	for (var lidx = 0; lidx < n; lidx++) {
		for (var i = 0; i < this.fields.length; i++) {
			var f = this.getRowField(lidx, this.fields[i]);
			f.onDestroy();
		}
	}
	var tbl = this.parent.find("#" + this.selectorEscape(this.id));
	tbl.find("tbody").empty();
};

/**
 * テーブルのカラムフィールドを所得します。
 * <pre>
 * テーブルの各カラムに割り当てられたフィールドクラスのインスタンスを所得します。
 * このフィールドをgetRowFieldに渡すことにより、行を限定したフィールドを取得することができます。
 * </pre>
 * @param {String} id フィールドID。
 * @returns テーブルのカラムフィールド。
 */
HtmlTable.prototype.getColumnField = function(id) {
	for (var i = 0; i < this.fields.length; i++) {
		if (this.fields[i].id == id) {
			return this.fields[i];
		}
	}
	return null;
};

/**
 * 指定行のフィールドを取得します。
 * @param {Integer} idx 指定行。
 * @param {Field} field フィールド。
 * @returns {Field} フィールド。
 * @deprecated getRowFieldを使用してください。
 */
HtmlTable.prototype.getLineField = function(idx, field) {
	return this.getRowField(idx, field);
};

/**
 * 指定行のフィールドを取得します。
 * @param {Integer} idx 指定行。
 * @param {Object} fobj フィールドIDまたはカラムフィールド。
 * @returns {Field} フィールド。
 */
HtmlTable.prototype.getRowField = function(idx, fobj) {
	var field = fobj;
	if (!(fobj instanceof Field)) {
		field = this.getColumnField(fobj);
	}
	var f = $.extend(true, {}, field);
	f.id = this.id + "[" + idx + "]." + field.id;
	f.initValidator(f.validatorList);
	return f;
};


/**
 * 行を追加します。
 *
 */
HtmlTable.prototype.addTr = function(l) {
	var tb = this.find("tbody");
	var lidx = this.find("tbody>tr").length;
	var line = this.trLine.replace(/\[0\]/g, "[" + lidx + "]");
	if (l == null) {
		tb.append("<tr>" + line + "</tr>");
	} else {
		$(this.find("tbody>tr").get(l)).before("<tr>" + line + "</tr>");
	}
	for (var i = 0; i < this.fields.length; i++) {
		var f = this.getRowField(lidx, this.fields[i]);
		f.attach();
	}
	return lidx;
};

/**
 * 指定行のデータを設定します。
 * @param {Integer} idx 行。
 * @param {Object} line フォームデータ。
 * @deprecated setRowDataを使用してください。
 */
HtmlTable.prototype.setLineData = function(idx, line) {
	this.setRowData(idx, line)
};

/**
 * 指定行のデータを設定します。
 * @param {Integer} idx 行。
 * @param {Object} line フォームデータ。
 */
HtmlTable.prototype.setRowData = function(idx, line) {
	for (var i = 0; i < this.fields.length; i++) {
		var orgf = this.fields[i];
		var f = this.getRowField(idx, orgf);
		f.setValue(line[orgf.id]);
	}
};


/**
 * 指定行のデータを更新する.
 * <pre>
 * 行編集後の更新用.
 * </pre>
 * @param line 行.
 * @param rowData 行のみのデータ.
 */
HtmlTable.prototype.updateRowData = function(line, rowData) {
	for (var i = 0; i < this.fields.length; i++) {
		var orgf = this.fields[i];
		var f = this.getRowField(line, orgf);
		f.setValue(rowData[orgf.id]);
	}
};

/**
 * テーブルに対するテータ設定を行います。
 * @param {Object} formData フォームのデータ。
 */
HtmlTable.prototype.setFormData = function(formData) {
	var list = formData[this.id];
	this.setSortMark();
	this.setTableData(list);
	this.tableData = list;
};

/**
 * 各行の背景色を設定します。
 */
HtmlTable.prototype.resetBackgroundColor = function() {
	var fsel = 'input[type="text"],input[type="password"],textarea,select';
	//
	this.find('tbody tr:even').removeClass("oddTr");
	this.find('tbody tr:even').find(fsel).removeClass("oddTr");
	this.find('tbody tr:even td').removeClass("oddTr");
	//
	this.find('tbody tr:even').addClass("evenTr");
	this.find('tbody tr:even').find(fsel).addClass("evenTr");
	this.find('tbody tr:even td').addClass("evenTr");
	//
	this.find('tbody tr:odd').removeClass("evenTr");
	this.find('tbody tr:odd').find(fsel).removeClass("evenTr");
	this.find('tbody tr:odd td').removeClass("evenTr");
	//
	this.find('tbody tr:odd').addClass("oddTr");
	this.find('tbody tr:odd').find(fsel).addClass("oddTr");
	this.find('tbody tr:odd td').addClass("oddTr");
};

/**
 * 行追加時に呼び出されるメソッドです。
 *
 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
 */
HtmlTable.prototype.onAddTr = function(rowid) {
};

/**
 * テーブルに対するテータ設定を行います。
 * @param {Array} list テーブルデータ。
 */
HtmlTable.prototype.setTableData = function(list) {
	this.tableData = list;
	if (list != null) {
		this.find("tbody").empty();
		// 表の行を追加.
		for (var i = 0; i < list.length; i++) {
			this.addTr();
		}
		// 表のデータを追加.
		for (var i = 0; i < list.length; i++) {
			this.setRowData(i, list[i]);
			this.onAddTr(this.id + "[" + i + "]");
		}
		this.resetBackgroundColor();
	}
};

/**
 * テーブルのバリデーションを行います。
 * @returns {Array} バリデーション結果。
 */
HtmlTable.prototype.validate = function() {
	var result = [];
	for (var i = 0;; i++) {
		var flg = false;
		for (var f = 0; f < this.fields.length; f++) {
			var fld = this.getRowField(i, this.fields[f]);
			if (fld.get().length > 0) {
				flg = true;
				var e = fld.validate();
				if (e != null) {
					result.push(e);
				}
			}
		}
		if (!flg) {
			break;
		}
	}
	return result;
};

/**
 * テーブルの指定行をロック/アンロックします。
 * @param {Number} line 指定行インデックス。
 * @param {Boolean} lk ロック指定。
 * @returns {Boolean} 指定行が存在する場合true。
 * @deprecated lockRowを使用してください。
 */
HtmlTable.prototype.lockLine = function(line, lk) {
	return this.lockRow(line, lk);
};

/**
 * テーブルの指定行をロック/アンロックします。
 * @param {Number} line 指定行インデックス。
 * @param {Boolean} lk ロック指定。
 * @returns {Boolean} 指定行が存在する場合true。
 */
HtmlTable.prototype.lockRow = function(line, lk) {
	var flg = false;
	for (var f = 0; f < this.fields.length; f++) {
		var fld = this.getRowField(line, this.fields[f]);
		if (fld.get().length > 0) {
			flg = true;
			fld.lock(lk);
		}
	}
	return flg;
};

/**
 * テーブル内のフィールドをロック/アンロックします。
 * @param {Boolean} lk ロックする場合true.
 */
HtmlTable.prototype.lockFields = function(lk) {
	for (var i = 0;; i++) {
		var flg = this.lockRow(i, lk);
		if (!flg) {
			break;
		}
	}
};


/**
 * 同じ行の指定フィールドを取得します。
 * @param {jQuery} f 指定フィールドに対応したjQueryオブジェクト。
 * @param {String} tid 取得するフィールドID.
 * @return {jQuery} 見つけた要素のjQueryオブジェクト。
 * <pre>
 * fで指定されたjQueryオブジェクトと同じ行にある、tidをもつ要素を取得します。
 * </pre>
 * @deprecated getSameRowFieldを使用してください。
 */
HtmlTable.prototype.getSameLineField = function(f, tid) {
	return this.getSameRowField(f, tid);
};

/**
 * 指定フィールドと同じ行のフィールドを取得します。
 * @param {jQuery} f 指定フィールドに対応したjQueryオブジェクト。
 * @param {String} tid 取得するフィールドID.
 * @return {jQuery} 見つけた要素のjQueryオブジェクト。
 * <pre>
 * fで指定されたjQueryオブジェクトと同じ行にある、tidをもつ要素を取得します。
 * </pre>
 */
HtmlTable.prototype.getSameRowField = function(f, tid) {
	var id = f.attr("id");
	var rid = id.replace(/\]\..+$/, "]." + tid);
	return this.find("#" + this.selectorEscape(rid));
};

/**
 * 指定された要素の行インデックスを取得します。
 * @param {jQuery} el 要素。
 * @returns {Integer} 行インデックス。
 */
HtmlTable.prototype.getRowIndex = function(el) {
	var id = el.attr("id");
	var sp = id.split(/[\[\]]/);
	return parseInt(sp[1]);
};


/**
 * 必須マークを設定します。
 */
HtmlTable.prototype.setRequiredMark = function() {
};

/**
 * 指定行の指定フィールドをもつtd要素を取得します。
 * @param {Number} row 行。
 * @param {String} id フィールドID。
 * @returns {jQuery} td要素。
 */
HtmlTable.prototype.getTd = function(row, id) {
	var fid = this.id + "[" + row + "]." + id;
	return this.find("#" + this.selectorEscape(fid)).parents("td:first");
};

/**
 * 指定されたフィールドの同じ値のものをまとめます。
 * @param {String} id フィールドID。
 */
HtmlTable.prototype.setRowSpan = function(id) {
	if (this.tableData != null) {
		var v0 = null;
		var rowspan = 1;
		var startrow = 0;
		for (var i = 0; i < this.tableData.length; i++) {
			var v = this.tableData[i][id];
			logger.log("v=" + v);
			if (v != v0) {
				this.getTd(startrow, id).prop("rowspan", rowspan);
				v0 = v;
				rowspan = 1;
				startrow = i;
			} else {
				rowspan++;
				this.getTd(i, id).remove();
			}
		}
		this.getTd(startrow, id).prop("rowspan", rowspan);
	}
};

/**
 * テーブルの行数を取得します。
 * @returns {Number} テーブルの行数。
 */
HtmlTable.prototype.getRowCount = function() {
	var n = this.find("tbody>tr").length;
	return n;
};


