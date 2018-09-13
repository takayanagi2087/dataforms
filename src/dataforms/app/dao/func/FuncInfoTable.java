package dataforms.app.dao.func;

import java.util.Map;

import dataforms.app.field.func.FuncIdField;
import dataforms.app.field.func.FuncPathField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 機能情報テーブルクラス。
 *
 */
public class FuncInfoTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public FuncInfoTable() {
		this.setComment("機能情報テーブル");
		this.addPkField(new FuncIdField());
		this.addField(new SortOrderField());
		this.addField(new FuncPathField());
//		this.addField(new FuncCommentField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
		this.setAutoIncrementId(true);
		this.setSequenceStartValue(Long.valueOf(1000));
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		FuncInfoTableRelation rel = new FuncInfoTableRelation(this);
		return rel.getJoinCondition(joinTable, alias);
	}

	
	/**
	 * 機能IDフィールドを取得します。
	 * @return 機能IDフィールド。
	 */
	public FuncIdField getFuncIdField() {
		return (FuncIdField) this.getField(Entity.ID_FUNC_ID);
	}
	
	
	/**
	 * ソート順フィールドを取得します。
	 * @return ソート順フィールド。
	 */
	public SortOrderField getSortOrderField() {
		return (SortOrderField) this.getField(Entity.ID_SORT_ORDER);
	}
	

	/**
	 * パスフィールドを取得します。
	 * @return パスフィールド。
	 */
	public FuncPathField getFuncPathField() {
		return (FuncPathField) this.getField(Entity.ID_FUNC_PATH);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** ユーザIDのフィールドID。 */
		public static final String ID_FUNC_ID = "funcId";
		/** ソート順のフィールドID。 */
		public static final String ID_SORT_ORDER = "sortOrder";
		/** パスのフィールドID。 */
		public static final String ID_FUNC_PATH = "funcPath";
		
		
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		
		/**
		 * 機能IDを取得します。
		 * @return 機能ID。
		 */
		public Long getFuncId() {
			return (Long) this.getMap().get(ID_FUNC_ID);
		}
		
		/**
		 * 機能IDを設定します。
		 * @param funcId ユーザID。
		 */
		public void setFuncId(final Long funcId) {
			this.getMap().put(ID_FUNC_ID, funcId);
		}
		
		/**
		 * ソート順を取得します。
		 * @return ソート順。
		 */
		public Short getSortOrder() {
			return (Short) this.getMap().get(ID_SORT_ORDER);
		}

		/**
		 * ソート順を設定します。
		 * @param sortOrder ソート順。
		 */
		public void setSortOrder(final Short sortOrder) {
			this.getMap().put(ID_SORT_ORDER, sortOrder);
		}
		
		/**
		 * パスを取得します。
		 * @return パス。
		 */
		public String getFuncPath() {
			return (String) this.getMap().get(ID_FUNC_PATH);
		}

		/**
		 * パスを設定します。
		 * @param funcPath パス。
		 */
		public void setFuncPath(final String funcPath) {
			this.getMap().put(ID_FUNC_PATH, funcPath);
		}
	}

	
	
}
