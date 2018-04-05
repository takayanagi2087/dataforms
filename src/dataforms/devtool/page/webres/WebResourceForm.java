package dataforms.devtool.page.webres;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.form.LoginInfoForm;
import dataforms.app.form.SideMenuForm;
import dataforms.controller.DataForms;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.controller.WebComponent;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.JavascriptClassField;
import dataforms.devtool.field.common.PathNameField;
import dataforms.devtool.field.common.WebComponentTypeField;
import dataforms.devtool.field.common.WebSourcePathField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.FileField;
import dataforms.field.common.FlagField;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.PresenceField;
import dataforms.field.common.RowNoField;
import dataforms.field.common.SingleSelectField;
import dataforms.field.sqltype.ClobField;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.htmltable.HtmlTable;
import dataforms.htmltable.PageScrollHtmlTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.ClassNameUtil;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.validator.ValidationError;

// TODO:validatorの生成の際attachメソッドの生成は不要。
/**
 * Webリソース作成フォームクラス。
 * 
 */
public class WebResourceForm extends Form {
	/**
	 * Log.
	 */
	private static Logger logger = Logger.getLogger(WebResourceForm.class.getName());
	/**
	 * コンストラクタ。
	 */
	public WebResourceForm() {
		super(null);
		this.addField(new WebSourcePathField());
		this.addField(new ClassNameField()).setReadonly(true);
		this.addField(new WebComponentTypeField()).setReadonly(true);
		this.addField(new PathNameField("htmlPath")).setReadonly(true);
		this.addField(new PresenceField("htmlStatus")).setReadonly(true);
		this.addField(new FlagField("outputFormHtml"));
		this.addField(new PathNameField("javascriptPath")).setReadonly(true);
		this.addField(new PresenceField("javascriptStatus")).setReadonly(true);
		this.addField(new JavascriptClassField()).setReadonly(true);
		this.addField(new FlagField("forceOverwrite"));
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("webSourcePath", DeveloperPage.getWebSourcePath());
	}

	/**
	 * HTMLを作成します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse generateHtml(final Map<String, Object> p) throws Exception {
		Map<String, Object> data = this.convertToServerData(p);
		String message = "";
		List<ValidationError> errlist = new ArrayList<ValidationError>();
		String srcpath = generateHtmlFile(data);
		if (srcpath != null) {
			message = MessagesUtil.getMessage(this.getPage(), "message.htmlgenerated", srcpath);
		} else {
			errlist.add(new ValidationError("htmlPath", MessagesUtil.getMessage(this.getPage(), "error.htmlalreadyexists")));
		}
		if (errlist.size() == 0) {
			JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, message);
			this.methodFinishLog(logger, r);
			return r;
		} else {
			JsonResponse r = new JsonResponse(JsonResponse.INVALID, errlist);
			this.methodFinishLog(logger, r);
			return r;
		}
	}

	/**
	 * HtmlGeneratorクラス。
	 */
	private static class HtmlGenerator {
		/**
		 * 段付のtab数。
		 */
		private int indent = 0;

		/**
		 * コンストラクタ。
		 * @param indent 段付けのtab数。
		 */
		public HtmlGenerator(final int indent) {
			this.indent = indent;
		}

		/**
		 * 段付けのtab数を取得します。
		 * @return 段付けのtab数。
		 */
		public int getIndent() {
			return indent;
		}

		/**
		 * 段付け用のtabを取得します。
		 * @return 段付け用のtab。
		 */
		protected String getTabs() {
			String tabs = "";
			for (int i = 0; i < this.getIndent(); i++) {
				tabs += "\t";
			}
			return tabs;
		}

		/**
		 * フィールドに対応したラベルを取得します。
		 * <pre>
		 * フィールドのコメントをラベルとします。
		 * フィールドのコメントがnullの場合idをラベルとします。
		 * </pre>
		 * @param field フィールド。
		 * @return ラベル。
		 */
		protected String getFieldLabel(final Field<?> field) {
			String label = field.getComment();
			if (label == null) {
				label = field.getId();
			}
			return label;
		}


	}

	/**
	 * フォームHTMLジェネレータ。
	 *
	 */
	private static class FormHtmlGenerator extends HtmlGenerator {
		/**
		 * 対象フォーム。
		 */
		private Form form = null;
		/**
		 * コンストラクタ。
		 * @param form 対象フォーム。
		 * @param indent 段付けのtab数。
		 */
		public FormHtmlGenerator(final Form form, final int indent) {
			super(indent);
			this.form = form;

		}

		/**
		 * 作成対象フォームを取得します。
		 * @return 作成対象フォーム。
		 */
		public Form getForm() {
			return form;
		}

		/**
		 * 指定されたクラスに対応したフォームHTMLジェネレータを作成します。
		 * @param form フォーム。
		 * @param indent 段付けのtab数。
		 * @return フォームHTMLジェネレータ。
		 */
		public static FormHtmlGenerator newFormHtmlGenerator(final Form form, final int indent) {
			FormHtmlGenerator gen = null;
			if ("queryForm".equals(form.getId())) {
				gen = new QueryFormHtmlGenerator(form, indent);
			} else if ("queryResultForm".equals(form.getId())) {
				gen = new QueryResultFormHtmlGenerator(form, indent);
			} else if ("editForm".equals(form.getId())) {
				gen = new EditFormHtmlGenerator(form, indent);
			} else {
				gen = new FormHtmlGenerator(form, indent);
			}
			return gen;
		}


		/**
		 * 作成対象コンポーネントかどうか確認します。
		 * @param c 確認するコンポーネント。
		 * @return 作成対象の場合true。
		 */
		protected boolean isTargetedForGeneration(final WebComponent c) {
			return true;
		}

		/**
		 * フォームのタイトルを返す。
		 * @return フォームのタイトル。
		 */
		protected String getFormTitle() {
			return "xxx";
		}

		/**
		 * フォームのボタンのHTMLを取得します。
		 * @return フォームのボタンのHTML。
		 */
		protected String getFormButtionHtml() {
			return "";
		}

		/**
		 * 隠しフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getHiddenFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t<input type=\"hidden\" id=\"" + field.getId() + "\" />\n");
			return sb.toString();
		}


		/**
		 * テキストフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getTextFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><input type=\"text\" id=\"" + field.getId() + "\" /></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}


		/**
		 * SelectフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getSelectFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><select id=\"" + field.getId() + "\"></select></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}

		/**
		 * SelectフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getRadioFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><span><input type=\"radio\" id=\"" + field.getId() + "[0]\" name=\"" + field.getId() + "\"/></span></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}

		/**
		 * チェックボックスリストフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getCheckboxArrayFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><span><input type=\"checkbox\" id=\"" + field.getId() + "[0]\" name=\"" + field.getId() + "\"/></span></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}

		/**
		 * マルチ選択リストフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getMultiSelectFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><select id=\"" + field.getId() + "\" size=\"5\" multiple=\"multiple\"></select></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}


		/**
		 * チェックボックスフラグフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getCheckboxFlagFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><input type=\"checkbox\" id=\"" + field.getId() + "\" value=\"1\"/></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}

		/**
		 * テキストエリアフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getTextareaFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><textarea id=\"" + field.getId() + "\" rows=\"20\" cols=\"80\"></textarea></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}


		/**
		 * ファイルフィールドのHTMLを作成します。
		 * @param field フィールド。
		 * @param tabs 段付けようのtab。
		 * @return テキストフィールドのHTML。
		 */
		private String getFileFieldHtml(final Field<?> field, final String tabs) {
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t<th>" + this.getFieldLabel(field) + "</th>\n");
			sb.append(tabs + "\t\t\t\t<td><input type=\"file\" id=\"" + field.getId() + "\"/></td>\n");
			sb.append(tabs + "\t\t\t</tr>\n");
			return sb.toString();
		}



		/**
		 * 隠しフィールドを生成する。
		 * @param f フォーム。
		 * @param sb 出力先文字列バッファ。
		 */
		private void generateHiddenField(final Form f, final StringBuilder sb) {
			String tabs = getTabs();
			for (WebComponent c: f.getComponentList()) {
				if (c instanceof Field) {
					Field<?> field = (Field<?>) c;
					if (!this.isTargetedForGeneration(c)) {
						continue;
					}
					if (c instanceof DeleteFlagField || field.isHidden()) {
						sb.append(this.getHiddenFieldHtml(field, tabs));
					}
				}
			}
		}


		/**
		 * 表示フィールドを生成する。
		 * @param f フォーム。
		 * @param sb 出力先文字列バッファ。
		 */
		private void generateVisibleField(final Form f, final StringBuilder sb) {
			String tabs = getTabs();
			for (WebComponent c: f.getComponentList()) {
				if (c instanceof Field) {
					Field<?> field = (Field<?>) c;
					if (field.isHidden()) {
						continue;
					}
					if (!this.isTargetedForGeneration(c)) {
						continue;
					}
					if (c instanceof DeleteFlagField) {
						continue;
					}
					if (c instanceof SingleSelectField) {
						SingleSelectField<?> msf = (SingleSelectField<?>) c;
						if (msf.getHtmlFieldType() == SingleSelectField.HtmlFieldType.SELECT) {
							// selectを展開
							sb.append(this.getSelectFieldHtml(field, tabs));
						} else {
							sb.append(this.getRadioFieldHtml(field, tabs));
						}
					} else if (c instanceof MultiSelectField) {
						MultiSelectField<?> msf = (MultiSelectField<?>) c;
						if (msf.getHtmlFieldType() == MultiSelectField.HtmlFieldType.CHECKBOX) {
							// checkboxを展開
							sb.append(this.getCheckboxArrayFieldHtml(field, tabs));
						} else {
							// マルチ選択リストを展開
							sb.append(this.getMultiSelectFieldHtml(field, tabs));
						}
					} else if (c instanceof FlagField) {
						// checkboxを展開
						sb.append(this.getCheckboxFlagFieldHtml(field, tabs));
					} else if (c instanceof ClobField) {
						// textareaを展開
						sb.append(this.getTextareaFieldHtml(field, tabs));
					} else if (c instanceof FileField) {
						// fileを展開
						sb.append(this.getFileFieldHtml(field, tabs));
					} else {
						// 通常はテキストボックス。
						sb.append(this.getTextFieldHtml(field, tabs));
					}
				}
			}
		}


		/**
		 * テーブルタグを出力します。
		 * @param f フォーム。
		 * @param sb 出力先文字列バッファ。
		 */
		protected void generateTable(final Form f, final StringBuilder sb) {
			for (WebComponent c: f.getComponentList()) {
				if (c instanceof HtmlTable) {
					HtmlTable table = (HtmlTable) c;
					TableHtmlGenerator gen = TableHtmlGenerator.newTableHtmlGenerator(table, this.getIndent());
					sb.append(gen.generateTableHtml());
				}
			}
		}

		/**
		 * フォームのHTMLを作成します。
		 * @param outputFormHtml Form別ファイル出力フラグ。
		 * @return フォームのHTML。
		 */
		public String generateFormHtml(final String outputFormHtml) {
			String tabs = getTabs();
			Form f = this.getForm();
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "<form id=\"" + f.getId() + "\">\n");
			if ("0".equals(outputFormHtml)) {
				sb.append(tabs + "\t<div class=\"formHeader\">" + this.getFormTitle() + "</div>\n");
				this.generateHiddenField(f, sb);
				sb.append(tabs + "\t<table class=\"responsive\">\n");
				sb.append(tabs + "\t\t<tbody>\n");
				this.generateVisibleField(f, sb);
				sb.append(tabs + "\t\t</tbody>\n");
				sb.append(tabs + "\t</table>\n");
				this.generateTable(f, sb);
				sb.append(this.getFormButtionHtml());
			}
			sb.append(tabs + "</form>\n");
			return sb.toString();
		}
	}

	/**
	 * テーブルHTMLジェネレータ。
	 *
	 */
	private static class TableHtmlGenerator extends HtmlGenerator {
		/**
		 * 対象テーブル。
		 */
		private HtmlTable table = null;

		/**
		 * 生成されたカラム数。
		 */
		private int columnCount = 0;

		/**
		 * コンストラクタ。
		 * @param table テーブル。
		 * @param indent 段付けのtab数。
		 */
		public TableHtmlGenerator(final HtmlTable table, final int indent) {
			super(indent);
			this.table = table;
		}

		/**
		 * 対象テーブルを取得します。
		 * @return 対象テーブル。
		 */
		public HtmlTable getTable() {
			return table;
		}

		/**
		 * 生成されたカラム数を取得します。
		 * @return 生成されたカラム数。
		 */
		protected int getColumnCount() {
			return columnCount;
		}

		/**
		 * カラム数を設定します。
		 * @param columnCount カラム数。
		 */
		public void setColumnCount(final int columnCount) {
			this.columnCount = columnCount;
		}

		/**
		 * HtmlTableに対応したTableHtmlGeneratorを作成します。
		 * @param table HTMLテーブル。
		 * @param indent 段付けのtab数。
		 * @return HtmlTableに対応したTableHtmlGenerator。
		 */
		public static TableHtmlGenerator newTableHtmlGenerator(final HtmlTable table, final int indent) {
			TableHtmlGenerator ret = null;
			if (table instanceof EditableHtmlTable) {
				ret = new EditableTableHtmlGenerator(table, indent);
			} else if (table instanceof PageScrollHtmlTable) {
				ret = new PageScrollTableHtmlGenerator(table, indent);
			} else {
				ret = new TableHtmlGenerator(table, indent);
			}
			return ret;
		}

		/**
		 * テーブルの開始タグを作成します。
		 * @param t HtmlTableオブジェクト。
		 * @return テーブルの開始タグ。
		 */
		protected String generateStartTableTag(final HtmlTable t) {
			return "<table id=\"" + t.getId() + "\">\n";
		}

		/**
		 * テキストフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getTextFieldHtml(final String tblid, final Field<?> field) {
			return "<input type=\"text\" id=\"" + tblid + "[0]." + field.getId() + "\" />";
		}

		/**
		 * SelectフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getSelectFieldHtml(final String tblid, final Field<?> field) {
			return "<select id=\"" + tblid + "[0]." + field.getId() + "\"></select>";
		}

		/**
		 * ラジオボタンフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getRadioFieldHtml(final String tblid, final Field<?> field) {
			return "<span><input type=\"radio\" id=\"" + tblid + "[0]." + field.getId() + "[0]\" name=\"" +  tblid + "[0]." + field.getId() + "\"/></span>";
		}

		/**
		 * チェックボックスフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getCheckboxArrayFieldHtml(final String tblid, final Field<?> field) {
			return "<span><input type=\"checkbox\" id=\"" + tblid + "[0]." + field.getId() + "[0]\" name=\"" + tblid + "[0]." +field.getId() + "\"/></span>";
		}


		/**
		 * 複数選択リストフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getMultiSelectFieldHtml(final String tblid, final Field<?> field) {
			return "<select id=\"" + tblid + "[0]." +field.getId() + "\" size=\"5\" multiple=\"multiple\"></select>";
		}


		/**
		 * チェックボックスフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getCheckboxFlagFieldHtml(final String tblid, final Field<?> field) {
			return "<input type=\"checkbox\" id=\"" + tblid + "[0]." + field.getId() + "\" value=\"1\"/>";
		}

		/**
		 * テキストエリアフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getTextareaFieldHtml(final String tblid, final Field<?> field) {
			return "<textarea id=\"" + tblid + "[0]." + field.getId() + "\" rows=\"20\" cols=\"80\"></textarea>";
		}


		/**
		 * ファイルフィールドのHTMLを作成します。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドのタグ。
		 */
		private String getFileFieldHtml(final String tblid, final Field<?> field) {
			return "<input type=\"file\" id=\"" + tblid + "[0]." + field.getId() + "\" />";
		}


		/**
		 * 表示フィールドを生成する。
		 * @param tblid テーブルID。
		 * @param field フィールド。
		 * @return フィールドタグ。
		 */
		protected String generateVisibleField(final String tblid, final Field<?> field) {
			Field<?> c = field;
			StringBuilder sb = new StringBuilder();
			if (c.isSpanField()) {
				sb.append("<span id=\"" + tblid + "[0]." + c.getId() + "\"></span>");
			} else if (c instanceof SingleSelectField) {
				SingleSelectField<?> msf = (SingleSelectField<?>) c;
				if (msf.getHtmlFieldType() == SingleSelectField.HtmlFieldType.SELECT) {
					// selectを展開
					sb.append(this.getSelectFieldHtml(tblid, field));
				} else {
					sb.append(this.getRadioFieldHtml(tblid, field));
				}
			} else if (c instanceof MultiSelectField) {
				MultiSelectField<?> msf = (MultiSelectField<?>) c;
				if (msf.getHtmlFieldType() == MultiSelectField.HtmlFieldType.CHECKBOX) {
					// checkboxを展開
					sb.append(this.getCheckboxArrayFieldHtml(tblid, field));
				} else {
					// マルチ選択リストを展開
					sb.append(this.getMultiSelectFieldHtml(tblid, field));
				}
			} else if (c instanceof FlagField) {
				// checkboxを展開
				sb.append(this.getCheckboxFlagFieldHtml(tblid, field));
			} else if (c instanceof ClobField) {
				// textareaを展開
				sb.append(this.getTextareaFieldHtml(tblid, field));
			} else if (c instanceof FileField) {
				// fileを展開
				sb.append(this.getFileFieldHtml(tblid, field));
			} else {
				// 通常はテキストボックス。
				sb.append(this.getTextFieldHtml(tblid, field));
			}
			return sb.toString();
		}

		/**
		 * Hiddenフィールドを展開します。
		 * @param t HTMLテーブル。
		 * @param sb 出力先文字列バッファ。
		 */
		protected void addHiddenFields(final HtmlTable t, final StringBuilder sb) {
			String tabs = getTabs();
			for (Field<?> f: t.getFieldList()) {
				if (f.isHidden()) {
					sb.append(tabs + "\t\t\t\t\t\t<input type=\"hidden\" id=\"" + t.getId() + "[0]." + f.getId() + "\" />\n");
				}
			}
		}

		/**
		 * カラム幅リスト
		 */
		private List<Integer> columnWidthList = null;
		
		
		/**
		 * カラム幅リストを取得します。
		 * @return カラム幅リスト。
		 */
		protected List<Integer> getColumnWidthList() {
			return this.columnWidthList;
		}

		/**
		 * フィールドに対応たヘッダを出力します。
		 * @param t テーブル。
		 * @param sb 出力先文字列バッファ。
		 */
		protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
			this.columnWidthList = new ArrayList<Integer>();
			String tabs = getTabs();
			this.columnCount = 0;
			for (Field<?> f: t.getFieldList()) {
				if (f.isHidden() || f instanceof DeleteFlagField) {
					continue;
				}
				this.columnCount++;
				sb.append(tabs + "\t\t\t\t\t<th>\n");
				sb.append(tabs + "\t\t\t\t\t\t" + this.getFieldLabel(f) + "\n");
				sb.append(tabs + "\t\t\t\t\t</th>\n");
				this.columnWidthList.add(Integer.valueOf(f.calcDefaultColumnWidth()));
			}
		}

		/**
		 * テーブルヘッダを生成します。
		 * @param t テーブル。
		 * @return テーブルヘッダ。
		 */
		protected String generateTableHeader(final HtmlTable t) {
			String tabs = getTabs();
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<thead>\n");
			sb.append(tabs + "\t\t\t\t<tr>\n");
			this.addFieldHeader(t, sb);
			sb.append(tabs + "\t\t\t\t</tr>\n");
			sb.append(tabs + "\t\t\t</thead>\n");
			return sb.toString();
		}


		/**
		 * テーブルボディを生成します。
		 * @param t テーブル。
		 * @return テーブルヘッダ。
		 */
		protected String generateTableBody(final HtmlTable t) {
			String tabs = getTabs();
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t\t\t<tbody>\n");
			sb.append(tabs + "\t\t\t\t<tr>\n");
			this.addFields(t,  sb);
			sb.append(tabs + "\t\t\t\t</tr>\n");
			sb.append(tabs + "\t\t\t</tbody>\n");
			return sb.toString();
		}

		/**
		 * テーブルフッターを作成します。
		 * @param t テーブル。
		 * @return テーブルフッターの文字列。
		 */
		protected String generateTableFooter(final HtmlTable t) {
			return "";
		}


		/**
		 * テーブルに対応したフィールドを出力します。
		 * @param t テーブル。
		 * @param sb 出力先文字列バッファ。
		 */
		protected void addFields(final HtmlTable t, final StringBuilder sb) {
			String tabs = getTabs();
			boolean first = true;
			int idx = 0;
			for (Field<?> f: t.getFieldList()) {
				if (f.isHidden() || f instanceof DeleteFlagField) {
					continue;
				}
				Integer w = this.columnWidthList.get(idx++);
				if (this.getTable().getFixedColumns() >= 0) {
					sb.append(tabs + "\t\t\t\t\t<td style=\"width: " + w + "px;\">\n");
				} else {
					sb.append(tabs + "\t\t\t\t\t<td>\n");
				}
				sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f)+ "\n");
				if (first) {
					this.addHiddenFields(t, sb);
					first = false;
				}
				sb.append(tabs + "\t\t\t\t\t</td>\n");
			}
		}



		/**
		 * HtmlTableよりHTMLを作成します。
		 * @return テーブルのHTML。
		 */
		public String generateTableHtml() {
			String tabs = getTabs();
			HtmlTable t = this.getTable();
			StringBuilder sb = new StringBuilder();
			sb.append(tabs + "\t<div class=\"hScrollDiv\">\n");
			sb.append("\t\t" + tabs + this.generateStartTableTag(t));
			if (t.getCaption() != null) {
				sb.append(tabs + "\t\t\t<caption>" + t.getCaption() + "</caption>\n");
			}
			sb.append(this.generateTableHeader(t));
			sb.append(this.generateTableBody(t));
			sb.append(this.generateTableFooter(t));
			sb.append("\t\t" + tabs + "</table>\n");
			sb.append(tabs + "\t</div>\n");
			return sb.toString();
		}
	}

	/**
	 * ページスクロールテーブルHTMLジェネレータ。
	 *
	 */
	private static class PageScrollTableHtmlGenerator extends TableHtmlGenerator {
		/**
		 * コンストラクタ。
		 * @param table テーブル。
		 * @param indent 段付けのtab数。
		 */
		public PageScrollTableHtmlGenerator(final HtmlTable table, final int indent) {
			super(table, indent);
		}

		@Override
		protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
			super.addFieldHeader(t, sb);
			String tabs = this.getTabs();
			sb.append(tabs + "\t\t\t\t\t<th>\n");
			sb.append(tabs + "\t\t\t\t\t\t操作\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
			this.setColumnCount(this.getColumnCount() + 1);
		}

		@Override
		protected void addFields(final HtmlTable t, final StringBuilder sb) {
			String tabs = this.getTabs();
			boolean first = true;
			boolean genlink = true;
			int idx = 0;
			for (Field<?> f: t.getFieldList()) {
				if (f.isHidden() || f instanceof DeleteFlagField) {
					continue;
				}
				Integer w = this.getColumnWidthList().get(idx++);
				if (this.getTable().getFixedColumns() >= 0) {
					if (f instanceof RowNoField) {
						sb.append(tabs + "\t\t\t\t\t<td class=\"rowno\">\n");
					} else {
						sb.append(tabs + "\t\t\t\t\t<td style=\"width: " + w + "px;\">\n");
					}
				} else {
					sb.append(tabs + "\t\t\t\t\t<td>\n");
				}
				if (first) {
					sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f) + "\n");
					this.addHiddenFields(t, sb);
					first = false;
				} else if (genlink && !first) {
					sb.append(tabs + "\t\t\t\t\t\t" +
							"<a id=\"" + t.getId() + "[0].updateButton\" href=\"javascript:void(0);\">" +
							this.generateVisibleField(t.getId(), f) +
							"</a>" +
							 "\n");
					genlink = false;
				} else {
					sb.append(tabs + "\t\t\t\t\t\t" + this.generateVisibleField(t.getId(), f)+ "\n");
				}
				sb.append(tabs + "\t\t\t\t\t</td>\n");
			}
			if (this.getTable().getFixedColumns() >= 0) {
				sb.append(tabs + "\t\t\t\t\t<td style=\"width: 164px;\">\n");
			} else {
				sb.append(tabs + "\t\t\t\t\t<td>\n");
			}
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].viewButton\" value=\"表示\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].referButton\" value=\"参照登録\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].deleteButton\" value=\"削除\">\n");
			sb.append(tabs + "\t\t\t\t\t</td>\n");
		}
	}



	/**
	 * 編集可能テーブルHTMLジェネレータ。
	 *
	 */
	private static class EditableTableHtmlGenerator extends TableHtmlGenerator {
		/**
		 * コンストラクタ。
		 * @param table テーブル。
		 * @param indent 段付けのtab数。
		 */
		public EditableTableHtmlGenerator(final HtmlTable table, final int indent) {
			super(table, indent);
		}

		@Override
		protected String generateStartTableTag(final HtmlTable t) {
			return "<table id=\"" + t.getId() + "\" class=\"editableTable\">\n";
		}

		@Override
		protected void addFieldHeader(final HtmlTable t, final StringBuilder sb) {
			String tabs = this.getTabs();
			sb.append(tabs + "\t\t\t\t\t<th colspan=\"2\" nowrap>\n");
			sb.append(tabs + "\t\t\t\t\t\t\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
//			sb.append(tabs + "\t\t\t\t\t<th class=\"buttonColumn\" nowrap>\n");
//			sb.append(tabs + "\t\t\t\t\t\t\n");
//			sb.append(tabs + "\t\t\t\t\t</th>\n");
			sb.append(tabs + "\t\t\t\t\t<th nowrap>\n");
			sb.append(tabs + "\t\t\t\t\t\tNo.\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
			super.addFieldHeader(t, sb);
			this.setColumnCount(this.getColumnCount() + 3);
		}

		@Override
		protected void addFields(final HtmlTable t, final StringBuilder sb) {
			String tabs = this.getTabs();
			sb.append(tabs + "\t\t\t\t\t<td class=\"buttonColumn\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].addButton\" value=\"+\"/>\n");
			sb.append(tabs + "\t\t\t\t\t</td>\n");
			sb.append(tabs + "\t\t\t\t\t<td class=\"buttonColumn\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + "[0].deleteButton\" value=\"-\"/>\n");
			sb.append(tabs + "\t\t\t\t\t</td>\n");
			sb.append(tabs + "\t\t\t\t\t<td style=\"text-align: right;\" class=\"rowno\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<span id=\"" + t.getId() + "[0].no\"></span>\n");
			sb.append(tabs + "\t\t\t\t\t</td>\n");
			super.addFields(t, sb);

		}

		@Override
		protected String generateTableFooter(final HtmlTable t) {
			StringBuilder sb = new StringBuilder();
			String tabs = this.getTabs();
			sb.append(tabs + "\t\t\t<tfoot>\n");
			sb.append(tabs + "\t\t\t\t<tr>\n");
			sb.append(tabs + "\t\t\t\t\t<th class=\"buttonColumn\">\n");
			sb.append(tabs + "\t\t\t\t\t\t<input type=\"button\" id=\"" + t.getId() + ".addButton\" value=\"+\"/>\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
			sb.append(tabs + "\t\t\t\t\t<th colspan=\"" + (this.getColumnCount() - 1) + "\">\n");
			sb.append(tabs + "\t\t\t\t\t</th>\n");
			sb.append(tabs + "\t\t\t\t</tr>\n");
			sb.append(tabs + "\t\t\t</tfoot>\n");
			return sb.toString();
		}

		@Override
		public String generateTableHtml() {
			StringBuilder sb = new StringBuilder();
//			String tabs = this.getTabs();
//			sb.append(tabs + "<div style=\"overflow-x:scroll;width:100%\">\n");
			sb.append(super.generateTableHtml());
//			sb.append(tabs + "</div>\n");
			return sb.toString();
		}
	}

	/**
	 * 問い合わせフォームHTMLジェネレータ。
	 *
	 */
	private static class QueryFormHtmlGenerator extends FormHtmlGenerator {
		/**
		 * コンストラクタ。
		 *
		 * @param form フォーム。
		 * @param indent 段付けのtab数。
		 */
		public QueryFormHtmlGenerator(final Form form, final int indent) {
			super(form, indent);
		}

		@Override
		protected String getFormTitle() {
			return "検索条件";
		}

		@Override
		protected String getFormButtionHtml() {
			String tabs = this.getTabs();
			String ret = tabs + "\t<input type=\"submit\" id=\"queryButton\" value=\"検索\">\n" +
					tabs + "\t<input type=\"button\" id=\"resetButton\" value=\"リセット\">&nbsp;\n" +
					tabs + "\t<input type=\"button\" id=\"newButton\" value=\"新規登録\">\n";
			return ret;
		}
	}


	/**
	 * 問い合わせ結果フォームHTMLジェネレータ。
	 *
	 */
	private static class QueryResultFormHtmlGenerator extends FormHtmlGenerator {
		/**
		 * 指定されたクラスに対応したフォームHTMLジェネレータを作成します。
		 * @param form フォーム。
		 * @param indent 段付けのtab数。
		 */
		public QueryResultFormHtmlGenerator(final Form form, final int indent) {
			super(form, indent);

		}

		@Override
		protected boolean isTargetedForGeneration(final WebComponent c) {
			if ("hitCount".equals(c.getId()) || "pageNo".equals(c.getId()) || "linesPerPage".equals(c.getId()) || "sortOrder".equals(c.getId())) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		protected String getFormTitle() {
			return "検索結果";
		}

	}

	/**
	 * 問い合わせフォームHTMLジェネレータ。
	 *
	 */
	private static class EditFormHtmlGenerator extends FormHtmlGenerator {
		/**
		 * コンストラクタ。
		 *
		 * @param form フォーム。
		 * @param indent 段付けのtab数。
		 */
		public EditFormHtmlGenerator(final Form form, final int indent) {
			super(form, indent);
		}

		@Override
		protected String getFormTitle() {
			return "<span id=\"editFormTitle\"></span>";
		}

		@Override
		protected String getFormButtionHtml() {
			String tabs = this.getTabs();
			String ret = tabs + "\t<input type=\"button\" id=\"confirmButton\" value=\"確認\"/>\n" +
					tabs + "\t<input type=\"button\" id=\"saveButton\" value=\"登録\"/>\n" +
					tabs + "\t<input type=\"button\" id=\"resetButton\" value=\"リセット\"/>\n" +
					tabs + "\t<input type=\"button\" id=\"deleteButton\" value=\"削除\"/>\n" +
					tabs + "\t<input type=\"button\" id=\"backButton\" value=\"戻る\"/>\n";
			return ret;
		}

	}

	/**
	 * Pageクラスに対応したhtmlを作成する。
	 * @param className クラス名。
	 * @param sourcePath 出力先フォルダ。
	 * @param outputFormHtml Form別ファイル出力フラグ。
	 * @return HTMLテキスト。
	 * @throws Exception 例外。
	 */
	private String getDataformsHtml(final String className, final String sourcePath, final String outputFormHtml) throws Exception {
		String src = this.getStringResourse("template/Page.html.template");
		Class<?> pageClass = Class.forName(className);
		DataForms page = (DataForms) pageClass.newInstance();
		StringBuilder sb = new StringBuilder();
		List<WebComponent> clist = page.getComponentList();
		for (WebComponent c: clist) {
			if (c instanceof Form) {
				Form f = (Form) c;
				if (isCommonForm(f)) {
					continue;
				}
				if ("0".equals(outputFormHtml)) {
					String srcpath = sourcePath + "/" + f.getClass().getName().replaceAll("\\.", "/") + ".html";
					File srcfile = new File(srcpath);
					srcfile.delete();
				}
				FormHtmlGenerator gen = FormHtmlGenerator.newFormHtmlGenerator(f, 3);
				sb.append(gen.generateFormHtml(outputFormHtml));
			}
		}
		src = src.replaceAll("\\$\\{forms\\}", sb.toString());
		return src;
	}

	/**
	 * フォームを出力します。
	 * @param f フォーム。
	 * @param sourcePath ファイルの出力先。
	 * @param forceOverwrite 強制上書きフラグ。
	 * @return 出力ファイル。
	 * @throws Exception 例外。
	 */
	private String outputFormHtml(final Form f, final String sourcePath, final String forceOverwrite) throws Exception {
		String src = this.getStringResourse("template/Form.html.template");
		FormHtmlGenerator gen = FormHtmlGenerator.newFormHtmlGenerator(f, 2);
		StringBuilder sb = new StringBuilder();
		sb.append(gen.generateFormHtml("0"));
		String gensrc = src.replaceAll("\\$\\{form\\}", sb.toString());
		String srcpath = sourcePath + "/" + f.getClass().getName().replaceAll("\\.", "/") + ".html";
		File file = new File(srcpath);
		if ((!file.exists()) || "1".equals(forceOverwrite)) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
		} else {
			srcpath = null;
		}
		return srcpath;
	}
	/**
	 * ページまたはダイアログクラス中のフォームを別HTMLに出力します。
	 * @param className ページクラス名。
	 * @param sourcePath ファイルの出力パス。
	 * @param forceOverwrite 強制上書きフラグ。
	 * @throws Exception 例外。
	 */
	private void outputForms(final String className, final String sourcePath, final String forceOverwrite) throws Exception {
		Class<?> pageClass = Class.forName(className);
		DataForms page = (DataForms) pageClass.newInstance();
		List<WebComponent> clist = page.getComponentList();
		for (WebComponent c: clist) {
			if (c instanceof Form) {
				Form f = (Form) c;
				if (isCommonForm(f)) {
					continue;
				}
				this.outputFormHtml(f, sourcePath, forceOverwrite);
			}
		}
	}

	/**
	 * 生成対象外の共通フォームかどうかを判定します。
	 * @param f フォーム。
	 * @return 生成対象外の共通フォームの場合true。
	 */
	private boolean isCommonForm(final Form f) {
		return f instanceof LoginInfoForm || f instanceof SideMenuForm;
	}


	/**
	 * HTMLファイルを作成します。
	 * @param data データ。
	 * @return 出力されたファイル。
	 * @throws Exception 例外。
	 */
	private String generateHtmlFile(final Map<String, Object> data) throws Exception {
		String webComponentType = (String) data.get("webComponentType");
		String outputFormHtml = (String) data.get("outputFormHtml");
		String forceOverwrite = (String) data.get("forceOverwrite");
		String sourcePath = (String) data.get("webSourcePath");
		String fullClassName = (String) data.get("className");
		String gensrc = "";
		if ("page".equals(webComponentType) || "dialog".equals(webComponentType)) {
			gensrc = this.getDataformsHtml(fullClassName, sourcePath, outputFormHtml);
			if ("1".equals(outputFormHtml)) {
				this.outputForms(fullClassName, sourcePath, forceOverwrite);
			}
		} else {

		}
		String srcpath = sourcePath + "/" + fullClassName.replaceAll("\\.", "/") + ".html";
		File f = new File(srcpath);
		if ((!f.exists()) || "1".equals(forceOverwrite)) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
			return srcpath;
		} else {
			return null;
		}
	}


	/**
	 * Javascriptを作成します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外
	 */
	@WebMethod
	public JsonResponse generateJavascript(final Map<String, Object> p) throws Exception {
		this.methodStartLog(logger, p);
		Map<String, Object> data = this.convertToServerData(p);
		String message = "";
		List<ValidationError> errlist = new ArrayList<ValidationError>();
		String srcpath = generateJavascriptFile(data);
		if (srcpath != null) {
			message = MessagesUtil.getMessage(this.getPage(), "message.javascriptgenerated", srcpath);
		} else {
			errlist.add(new ValidationError("javascriptPath", MessagesUtil.getMessage(this.getPage(), "error.javascriptalreadyexists")));
		}
		if (errlist.size() == 0) {
			JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, message);
			this.methodFinishLog(logger, r);
			return r;
		} else {
			JsonResponse r = new JsonResponse(JsonResponse.INVALID, errlist);
			this.methodFinishLog(logger, r);
			return r;
		}
	}

	/**
	 * javascriptファイルを作成します。
	 * @param data データ。
	 * @return 出力されたファイル。
	 * @throws Exception 例外。
	 * 
	 */
	private String generateJavascriptFile(final Map<String, Object> data) throws Exception {
		String forceOverwrite = (String) data.get("forceOverwrite");
		String sourcePath = (String) data.get("webSourcePath");
		String fullClassName = (String) data.get("className");
		Class<?> cls = Class.forName(fullClassName);
		cls.getSuperclass().getSimpleName();
		String superClassName = cls.getSuperclass().getSimpleName(); // (String) data.get("javascriptClass");
		String className = ClassNameUtil.getSimpleClassName(fullClassName);
		String src = this.getStringResourse("template/JavascriptClass.js.template");
		String gensrc = src.replaceAll("\\$\\{className\\}", className);
		gensrc = gensrc.replaceAll("\\$\\{superClassName\\}", superClassName);
		String srcpath = sourcePath + "/" + fullClassName.replaceAll("\\.", "/") + ".js";
		File f = new File(srcpath);
		if ((!f.exists()) || "1".equals(forceOverwrite)) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
			return srcpath;
		} else {
			return null;
		}
	}

}
