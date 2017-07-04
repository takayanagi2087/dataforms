package dataforms.field.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.dao.file.FileObject;
import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.field.base.Field.SortOrder;
import dataforms.util.StringUtil;

/**
 * フィールドリストクラス。
 *
 */
public class FieldList extends ArrayList<Field<?>> {
	/**
	 *
	 */
	private static final long serialVersionUID = 7679090688363245776L;

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(FieldList.class.getName());

	/**
	 * コンストラクタ。
	 *
	 */
	public FieldList() {

	}

	/**
	 * フィールドリスト。
	 * @param fieldList フィールドリスト。
	 */
	public FieldList(final Field<?>... fieldList) {
		for (Field<?> f: fieldList) {
			this.add(f);
		}
	}

	/**
	 * 指定されたIDに対応したフィールドを取得します。
	 * @param id フィールドID。
	 * @return フィールド。
	 */
	public Field<?> get(final String id) {
		Field<?> ret = null;
		for (Field<?> f : this) {
			if (id.equals(f.getId())) {
				ret = f;
				break;
			}
		}
		if (ret == null) {
			log.debug("field not found id=" + id);
		}
		return ret;
	}

	/**
	 * リストの指定フィールドIDの前に、フィールドを挿入します。
	 * @param field 挿入するフィールド。
	 * @param id 指定ティールドID。
	 * @return 挿入されたフィールド。
	 */
	public Field<?> insertBefore(final Field<?> field, final String id) {
		for (int i = 0; i < this.size(); i++) {
			Field<?> f = this.get(i);
			if (f.getId().equals(id)) {
				this.add(i, field);
				break;
			}
		}
		return field;
	}

	/**
	 * リストの指定手フィールドIDの後に、フィールドを挿入します。
	 * @param field 挿入するフィールド。
	 * @param id 指定フィールドID。
	 * @return 挿入されたフィールド。
	 */
	public Field<?> insertAfter(final Field<?> field, final String id) {
		for (int i = 0; i < this.size(); i++) {
			Field<?> f = this.get(i);
			if (f.getId().equals(id)) {
				this.add(i + 1, field);
				break;
			}
		}
		return field;
	}


	/**
	 * フィールドを追加します。
	 * @param field 追加するフィールド。
	 * @return 追加されたフィールド。
	 */
	public Field<?> addField(final Field<?> field) {
		this.add(field);
		return field;
	}

	/**
	 * クラス情報を取得します。
	 * @return クラス情報。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getFieldListClassInfo() throws Exception {
		List<Map<String, Object>> fieldList = new ArrayList<Map<String, Object>>();
		for (Field<?> f : this) {
			fieldList.add(f.getClassInfo());
		}
		return fieldList;
	}

	/**
	 * 指定したIDのフィールドを削除します。
	 * @param id フィールドID。
	 */
	public void remove(final String id) {
		Field<?> f = this.get(id);
		if (f != null) {
			this.remove(f);
		}
	}

	/**
	 * Postされたクライアント形式データをサーバ形式データに変換します。
	 * <pre>
	 * フィールドリスト中のフィールドの変換ルールに従ってクライアント形式→サーバ形式変換を行います。
	 * </pre>
	 * @param param クライアント形式データ。
	 * @return サーバ形式データ。
	 */
	public Map<String, Object> convertClientToServer(final Map<String, Object> param) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this) {
			f.setClientValue(param.get(f.getId()));
			ret.put(f.getId(), f.getValue());
		}
		return ret;
	}

	/**
	 * サーバ形式データをクライアント形式に変換します。
	 * <pre>
	 * フィールドリスト中のフィールドの変換ルールに従ってサーバ形式→クライアント形式変換を行います。
	 * </pre>
	 * @param data サーバ形式データ。
	 * @return クライアント形式データ。
	 */
	public Map<String, Object> convertServerToClient(final Map<String, Object> data) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this) {
			f.setValueObject(data.get(f.getId()));
			ret.put(f.getId(), f.getClientValue());
		}
		return ret;
	}


	/**
	 * サーバ形式データをDB形式に変換します。
	 * <pre>
	 * フィールドリスト中のフィールドの変換ルールに従ってサーバ形式→DB形式変換を行います。
	 * </pre>
	 * @param data サーバ形式データ。
	 * @return DB形式データ。
	 */
	public Map<String, Object> convertServerToDb(final Map<String, Object> data) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this) {
			f.setValueObject(data.get(f.getId()));
			ret.put(f.getId(), f.getDBValue());
		}
		return ret;
	}

	/**
	 * DB形式データをサーバ形式に変換します。
	 * <pre>
	 * フィールドリスト中のフィールドの変換ルールに従ってDB形式→サーバ形式変換を行います。
	 * </pre>
	 * @param data DB形式データ。
	 * @return サーバ形式データ。
	 */
	public Map<String, Object> convertDbToServer(final Map<String, Object> data) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this) {
			f.setDBValue(data.get(f.getId()));
			if (f instanceof SqlBlob) {
				// ダウンロードパラメータを設定する。
				FileObject v = (FileObject) f.getValue();;
				if (v != null) {
					SqlBlob blobf = (SqlBlob) f;
					if (v.getFileName() != null) {
						v.setDownloadParameter(blobf.getBlobDownloadParameter(data));
					}
				}
			}
			ret.put(f.getId(), f.getValue());
		}
		return ret;
	}

	/**
	 * 指定されたリストをマージします。
	 * @param list 指定リスト。
	 * @param replace 同一idのフィールドが存在した場合、trueの場合置き換え、falseの場合追加させません。
	 */
	public void marge(final FieldList list, final boolean replace) {
		for (Field<?> f: list) {
			Field<?> oldf = this.get(f.getId());
			if (oldf != null) {
				if (replace) {
					this.remove(oldf);
					this.add(f);
				}
			} else {
				this.add(f);
			}
		}
	}

	/**
	 * 指定されたリストをマージします。
	 * <pre>
	 * 既に同一のフィールドIDが存在した場合、追加されません。
	 * </pre>
	 * @param list 指定リスト。
	 */
	public void marge(final FieldList list) {
		this.marge(list, false);
	}

	/**
	 * ソート用のフィールドリストを取得します。
	 * @param sortOrder ソートフィールと文字列。
	 * @return ソート用のフィールドリスト。
	 */
	public FieldList getOrderByFieldList(final String sortOrder) {
		FieldList list = new FieldList();
		if (!StringUtil.isBlank(sortOrder)) {
			String[] sp = sortOrder.split("\\,");
			for (String f: sp) {
				log.debug("f=" + f);
				String[] fsp = f.split("\\:");
				log.debug("fsp[0]=" + fsp[0]);
				Field<?> field = this.get(fsp[0]);
				if (field != null) {
					if ("ASC".equals(fsp[1])) {
						field.setSortOrder(SortOrder.ASC);
					} else if ("DESC".equals(fsp[1])) {
						field.setSortOrder(SortOrder.DESC);
					}
					list.add(field);
				}
			}
		}
		return list;
	}
}
