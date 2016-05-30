package dataforms.app.dao.user;

import java.util.List;
import java.util.Map;

import dataforms.dao.Query;

/**
 * ユーザ属性問い合わせクラス。
 *
 */
public class UserAttributeQuery extends Query {
	/**
	 * コンストラクタ。
	 * @param data attTableにユーザ属性リストを記録したマップ。
	 */
	public UserAttributeQuery(final Map<String, Object> data) {
		UserAttributeTable mtbl = new UserAttributeTable();
		mtbl.setAlias("m");
		this.setFieldList(mtbl.getFieldList());
		this.setMainTable(mtbl);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> clist = (List<Map<String, Object>>) data.get("attTable");

		// where句の条件式を設定する.
		StringBuilder sb = new StringBuilder();
		if (clist != null) {
			for (int i = 0; i < clist.size(); i++) {
				String c = "(" + this.getMatchFieldSql(mtbl.getField("userAttributeType")) + "=:att_table[" + i + "].user_attribute_type and " +
						this.getMatchFieldSql(mtbl.getField("userAttributeValue")) + "=:att_table[" + i + "].user_attribute_value)\n";
				if (sb.length() > 0) {
					sb.append(" or ");
				}
				sb.append(c);
			}
		}
		this.setCondition(sb.toString());
	}
}

