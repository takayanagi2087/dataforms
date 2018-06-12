/**
 * @fileOverview {@link AllTypeEditForm}クラスを記述したファイルです。
 */

/**
 * @class AllTypeEditForm
 *
 * @extends EditForm
 */
AllTypeEditForm = createSubclass("AllTypeEditForm", {}, "EditForm");


/**
 * HTMLエレメントとの対応付けを行います。
 */
AllTypeEditForm.prototype.attach = function() {
	EditForm.prototype.attach.call(this);
	var thisForm = this;
	this.find("#printButton").click(function() {
		thisForm.print();
	});
	this.find("#printPdfButton").click(function() {
		thisForm.printPdf();
	});
	this.find("#getValueTestButton").click(function() {
		thisForm.getValueTest();
	});
	this.find("#userDialogButton").click(function() {
		var dlg = thisForm.parent.getComponent("userQueryDialog");
		dlg.data = null;
		dlg.showModal({
			close:function(event, ui) {
				if (dlg.data != null) {
					var json = JSON.stringify(dlg.data);
					logger.log("select data=" + json);
					alert(json);
				}
			}
		});
	});
//	$("#test1").html("<script>alert('aaa');</script>");
//	$("#test2").text("<script>alert('bbb');</script>");
//	$("#test3").val("<script>alert('ccc');</script>");
//	alert(MessagesUtil.getMessage("error.duplicate", "aaa"));
};

/**
 * 印刷処理を行います。
 */
AllTypeEditForm.prototype.print = function() {
	this.submitForDownload("print");
};

/**
 * 印刷処理を行います。
 */
AllTypeEditForm.prototype.printPdf = function() {
	this.submitForDownload("printPdf");
};

AllTypeEditForm.prototype.getFieldDump = function(id) {
	var v = this.getComponent(id).getValue();
	var text = id + "=" + v + ":" + v.constructor + "\n";
	return text;
}


/**
 * 各種getValueのテストを行います。
 */
AllTypeEditForm.prototype.getValueTest = function() {
/*	logger.log("getValueTest()");
	var text = "";
	text += this.getFieldDump("varcharField");
	text += this.getFieldDump("numericField");
	text += this.getFieldDump("dateField");
	text += this.getFieldDump("timeField");
	text += this.getFieldDump("timestampField");
	text += this.getFieldDump("dropdownField");
	text += this.getFieldDump("multiSelectListField");
	text += this.getFieldDump("radioField");
	text += this.getFieldDump("checkboxField");
	text += this.getFieldDump("uploadBlobData");
	text += this.getFieldDump("uploadFileData");

//	alert(text);
	logger.log(text);*/

	this.find("#attachFileTable thead tr").find("th").each(function() {
		logger.dir(this);
	});

};
