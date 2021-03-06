package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.EVerticalAlign;
import net.simpleframework.mvc.common.element.ImageElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.menu.MenuItems;
import net.simpleframework.mvc.component.ui.pager.AbstractTablePagerSchema;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumns;
import net.simpleframework.mvc.template.t1.ext.LCTemplateTablePagerHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictItemTbl extends LCTemplateTablePagerHandler implements IDictContextAware {

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final ID orgId = DictUtils.getDomainId(cp);
		if (orgId != null) {
			cp.addFormParameter("orgId", orgId);
		}
		final Dict dict = DictUtils.getDict(cp);
		if (dict != null) {
			cp.addFormParameter("dictId", dict.getId());
		}
		return _dictItemService.queryItems(dict, orgId);
	}

	private static final MenuItems CONTEXT_MENUS = MenuItems.of()
			.append(MenuItem.itemEdit().setOnclick_act("DictMgrPage_itemWin", "itemId"))
			.append(MenuItem.itemDelete().setOnclick_act("DictMgrPage_delete", "id"))
			.append(MenuItem.sep())
			.append(MenuItem.itemLog().setOnclick_act("DictMgrPage_logWin", "beanId"))
			.append(MenuItem.sep())
			// 移动菜单
			.append(MenuItem.TBL_MOVE_UP("DictMgrPage_move"))
			.append(MenuItem.TBL_MOVE_UP2("DictMgrPage_move"))
			.append(MenuItem.TBL_MOVE_DOWN("DictMgrPage_move"))
			.append(MenuItem.TBL_MOVE_DOWN2("DictMgrPage_move"));

	@Override
	public MenuItems getContextMenu(final ComponentParameter cp, final MenuBean menuBean,
			final MenuItem menuItem) {
		return menuItem == null ? CONTEXT_MENUS : null;
	}

	@Override
	public AbstractTablePagerSchema createTablePagerSchema() {

		return new DefaultDbTablePagerSchema() {
			@Override
			public TablePagerColumns getTablePagerColumns(final ComponentParameter cp) {
				final Dict dict = DictUtils.getDict(cp);
				if (dict == null) {
					final TablePagerColumns columns = new TablePagerColumns(
							super.getTablePagerColumns(cp));
					columns.add(4,
							new TablePagerColumn("dictId", $m("DictItemTbl.1"), 100).setFilterSort(false));
					return columns;
				}
				return super.getTablePagerColumns(cp);
			}

			@Override
			public Map<String, Object> getRowData(final ComponentParameter cp,
					final Object dataObject) {
				final DictItem item = (DictItem) dataObject;

				final KVMap kv = new KVMap();
				final StringBuilder sb = new StringBuilder();
				sb.append(item.getText());
				final String desc = item.getDescription();
				if (StringUtils.hasText(desc)) {
					sb.append(BlockElement.tipText(desc));
				}

				AbstractElement<?> img = null;
				if (!cp.isLmanager() && item.getDomainId() == null) {
					img = new ImageElement(
							cp.getCssResourceHomePath(DictItemTbl.class) + "/images/dict_lock.png")
									.setVerticalAlign(EVerticalAlign.middle);
				}
				if (img != null) {
					kv.add(TablePagerColumn.ICON, img);
				}

				kv.add("text", sb.toString()).add("codeNo", item.getCodeNo());
				final PermissionDept dept = cp.getPermission().getDept(item.getDomainId());
				if (dept.exists()) {
					kv.add("domainId", dept);
				}

				final DictItem parent = _dictItemService.getBean(item.getParentId());
				if (parent != null) {
					kv.add("parentId", parent.getText());
				}
				if (DictUtils.getDict(cp) == null) {
					final Dict dict2 = _dictService.getBean(item.getDictId());
					if (dict2 != null) {
						kv.add("dictId", dict2.getText());
					}
				}

				kv.put(TablePagerColumn.OPE, toOpeHTML(cp, item));
				return kv;
			}
		};
	}

	protected String toOpeHTML(final ComponentParameter cp, final DictItem item) {
		final StringBuilder sb = new StringBuilder();
		sb.append(ButtonElement.editBtn()
				.setOnclick("$Actions['DictMgrPage_itemWin']('itemId=" + item.getId() + "');"));
		sb.append(AbstractTablePagerSchema.IMG_DOWNMENU);
		return sb.toString();
	}
}