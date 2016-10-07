/**
 * @fileOverview {@link Page}クラスを記述したファイルです。
 */

/**
 * @class Page
 *
 * ページクラス。
 * <pre>
 * 1つのHTMLファイル(ページ)に対応するクラスです。
 * ページは複数のFormと複数のDialogを持つことができます。
 * </pre>
 * @extends DataForms
 */
Page = createSubclass("Page", {id: "mainDiv"}, "DataForms");


/**
 * 現在のページインスタンスです。
 */
currentPage = null;

/**
 * consoleのコピーです。
 * <pre>
 * javascriptのログ出力はconsoleでなくloggerを使用してください。
 * loggerを使用すると、web.xmlのclient-log-levelの設定で、ログレベルの変更が可能です。
 * </pre>
 */
logger = null;

/**
 * loggerの設定をおこないます。
 * <pre>
 * consoleをサポートしないブラウザの場合、consoleオブジェクトを作成します。
 * consoleオブジェクトをloggerオブジェクトに代入します。
 * 基本的にjavascriptのログ出力はloggerを使用してください。
 * </pre>
 */
Page.prototype.configureLogger = function () {
	if (typeof window.console === "undefined") {
		window.console = {};
	}
	if (typeof window.console.log !== "function") {
		window.console.log = function() {};
	}
	if (typeof window.console.info !== "function") {
		window.console.info = function() {};
	}
	if (typeof window.console.warn !== "function") {
		window.console.warn = function() {};
	}
	if (typeof window.console.error !== "function") {
		window.console.error = function() {};
	}
	if (typeof window.console.dir !== "function") {
		window.console.dir = function() {};
	}
	window.logger = window.console;
};

/**
 * logレベルを設定します。
 * <pre>
 * クライアントログレベルに応じて、loggerオブジェクトのメソッドを無効化します。
 * </pre>
 */
Page.prototype.configureLogLevel = function () {
	if (this.clientLogLevel == "info" || this.clientLogLevel == "warn" || this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.log = function() {};
	}
	if (this.clientLogLevel == "warn" || this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.info = function() {};
	}
	if (this.clientLogLevel == "error" || this.clientLogLevel == "none") {
		logger.warn = function() {};
	}
	if (this.clientLogLevel == "none") {
		logger.error = function() {};
	}
	logger.info("clientLogLevel=" + this.clientLogLevel);
	logger.log("debug log.");
	logger.info("info log.");
	logger.warn("warn log.");
	logger.error("error log.");
};


/**
 * 同期サーバメソッドを取得する.
 * <pre>
 * Page中のWebMethodはメソッド名のみ(WebComponentのidは無い形式)のためオーバーライドしています。
 * </pre>
 * @param {String} method メソッド名.
 * @returns {SyncServerMethod} 同期サーバメソッド.
 */
Page.prototype.getSyncServerMethod = function(method) {
	return new SyncServerMethod(method);
};

/**
 * mainDivをFrame.htmlに指定されたフレームで囲む。
 * @param frame {jQuery} フレームのbodyの内容のjQueryオブジェクト。
 * @param frameMainDiv {jQuery}　フレームのmainDivのjQueryオブジェクト。
 * @param mainDiv {jQuery} ページのmainDivのjQueryオブジェクト。
 */
Page.prototype.wrapFrame = function(frame, frameMainDiv, mainDiv) {
	var p = mainDiv.parent();
	var div = mainDiv;
	frameMainDiv.prevAll().each(function() {
		div.before($(this));
		div = $(this);
	});
	div = mainDiv;
	frameMainDiv.nextAll().each(function() {
		div.after($(this));
		div = $(this);
	});
	var fparent = frameMainDiv.parent();
	logger.log("id=" + fparent.attr("id"));
	if (fparent.attr("id") != "rootDiv") {
		logger.log("parent.size()=" + fparent.size());
		logger.log("id=" + fparent.attr("id"));
		fparent.empty();
		p.children().wrapAll(fparent);
		this.wrapFrame(frame, fparent, mainDiv.parent());
	}

};

/**
 * 各ブロックのレイアウトを行います。
 * <pre>
 * Frame.htmlを取得し、その内容にしたがってページをレイアウトします。
 * </pre>
 */
Page.prototype.layout = function() {
	var getParts = this.getSyncServerMethod("getParts");
	var ret = getParts.execute("parts=" + this.framePath + "/Frame.html");
	if (ret.status == ServerMethod.SUCCESS) {
		var frame = $("<div id=\"rootDiv\">" + ret.result + "</div>");
		this.wrapFrame(frame, frame.find("#mainDiv"), $("#mainDiv"));
		logger.log("frame=" + frame.html());
	}
	var getHead = this.getSyncServerMethod("getHead");
	var hret = getHead.execute("parts=" + this.framePath + "/Frame.html");
	if (hret.status == ServerMethod.SUCCESS) {
		var head = $("<div>" + hret.result + "</div>");
		// ライブラリと競合するタグを削除
		head.find("meta[charset='UTF-8']").remove();
		head.find("meta[charset='utf-8']").remove();
		head.find("link[rel='stylesheet'][href='Frame.css']").remove();
		head.find("link[rel='stylesheet'][href='FramePC.css']").remove();
		head.find("link[rel='stylesheet'][href='FrameTB.css']").remove();
		head.find("link[rel='stylesheet'][href='FrameSP.css']").remove();
		head.find("title").remove();
		$("head").append(head.html());
	}

	var systemName = MessagesUtil.getMessage("message.systemname");
	if (systemName != null) {
		$('#systemName').html(systemName);
	}
	if (this.pageTitle == null) {
		$('#pageName').html($('title').html());
	} else {
		$('title').html(this.pageTitle);
		$('#pageName').html(this.pageTitle);
	}
	initFrame();
};

/**
 * ダイアログを初期化します。
 * @param {Array} dialogList ダイアログリスト.
 */
Page.prototype.initDialog = function(dialogList) {
	// ダイアログの初期化.
	for (var key in dialogList) {
		var dlgclass = dialogList[key];
		var dlg = this.newInstance(dlgclass);
		dlg.htmlPath = dlgclass.path + ".html";
		dlg.init();
	}
};

/**
 * トップページに遷移します。
 */
Page.prototype.toTopPage = function() {
	window.location.href = this.contextPath + this.topPage;
};

/**
 * ブラウザの戻るボタン押下時の処理を行います。
 * <pre>
 * web.xmlのbrowser-back-buttonがenabledの場合は呼ばれません。
 * </pre>
 * @param event イベント情報。
 */
Page.prototype.onBrowserBackButton = function(event) {
	history.pushState("disableBack", "disableBack", location.href);
	return;
};

/**
 * ブラウザの戻るボタンの設定を行います。
 */
Page.prototype.configureBrowserBackButton = function() {
	var thisPage = this;
	logger.log("browserBackButton=" + this.browserBackButton);
	if (this.browserBackButton == "disabled") {
		history.pushState("disableBack", "disableBack", location.href);
		$(window).on("popstate", function(event){
			if (!event.originalEvent.state){
				thisPage.onBrowserBackButton(event);
			}
		});
	}
};

/**
 * ページの初期化処理を行います。
 */
Page.prototype.init = function() {
	DataForms.prototype.init.call(this);
	this.configureLogger();
	logger.info("language=" + this.getLanguage());
	$.datepicker.setDefaults($.datepicker.regional[this.getLanguage()]);
	//メッセージユーティリティの初期化.
	MessagesUtil.init();

	// ページの初期化.
	var method = this.getSyncServerMethod("getPageInfo");
	var result = method.execute();
	for (var key in result.result) {
		this[key] = result.result[key];
	}

	this.setCookie("cookiecheck", "true");
	var cookiecheck = this.getCookie("cookiecheck");
	logger.log("cookiecheck=" + cookiecheck);
	if (cookiecheck != "true") {
		alert(MessagesUtil.getMessage("error.cookienotsupport"));
		window.history.back();
	}
	this.setCookie("cookiecheck", "");

	this.configureLogLevel();
	this.configureBrowserBackButton();

	if (!this.noFrame) {
		this.layout();
	}
	// 各フォームの初期化
	this.initForm(this.formMap);
	// ダイアログの初期化
	this.initDialog(this.dialogMap);
	// バージョン情報などを表示。
	$("#dataformsVersion").html(this.dataformsVersion);
//	$("#dataformsVendor").html(this.dataformsVendor);
};

/**
 * エレメントとの対応付けを行います。
 */
Page.prototype.attach = function() {
	DataForms.prototype.attach.call(this);
	$("#showMenuButton").click(function() {
		var menu = $("#menuDiv");
		if (menu.size() == 0) {
			// leftbarDivの対応は互換性維持のため
			$("#leftbarDiv").toggle("blind");
		} else {
			menu.toggle("blind");
		}
	});
	var thisPage = this;
	$(window).resize(function() {
		thisPage.onResize();
	});

};

/**
 * リサイズ時の処理を行います。
 */
Page.prototype.onResize = function() {
	logger.log("onResize");
	$("#menuDiv").css("display", "");
	// leftbarDivの対応は互換性維持のため
	$("#leftbarDiv").css("display", "");
};


/**
 * ログインダイアログを表示します。
 */
Page.prototype.showLoginDialog = function() {
	var dlg = this.getComponent("loginDialog");
	dlg.showModal();
};

/**
 * ページをロックします。
 * <pre>
 * Ajax呼び出し前に呼び出され、画面をロックします。
 * </pre>
 */
Page.prototype.lock = function() {
    $("#lockLayer").css({
        backgroundColor: 'black',
        position: 'absolute',
        display: 'block',
        left: 0,
        top: 0,
        opacity: 0.5,
        width: $(document).width(),
        height: $(document).height()
      });
	  $(window).on("resize.lockLayer", function() {
	    $('#lockLayer').css({
	      width: $(document).width(),
	      height: $(document).height()
	    });
	  });
};

/**
 * ページをアンロックします。
 * <pre>
 * Ajax呼び出しの応答受信後に呼び出され、画面をアンロックします。
 * </pre>
 */
Page.prototype.unlock = function() {
	 $("#lockLayer").css({display: 'none'});
	  $(window).off("resize.lockLayer");
};

/**
 * alertの代替えメソッドです。
 * 
 * @param {String} title ダイアログタイトル。
 * @param {String} msg ダイアログメッセージ。
 * @param {Functuion} func OKボタンが押された際の処理。
 */
Page.prototype.alert = function(title, msg, func) {
	var dlg = this.getComponent("alertDialog");
	dlg.title = title;
	dlg.message = msg;
	dlg.okFunc = func;
	dlg.showModal({
		minHeight: 100
	});
};

/**
 * confirmの代替えメソッドです。
 * @param {String} title タイトル。
 * @param {String} msg メッセージ。
 * @param {Function} okFunc OKボタンのダイアログ。
 * @param {Function} cancelFunc キャンセルボタンのダイアログ。
 */
Page.prototype.confirm = function(title, msg, okFunc, cancelFunc) {
	var dlg = this.getComponent("confirmDialog");
	dlg.title = title;
	dlg.message = msg;
	dlg.okFunc = okFunc;
	dlg.cancelFunc = cancelFunc;
	dlg.showModal({
		minHeight: 100
	});
};


