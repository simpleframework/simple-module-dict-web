package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictItemService;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ETextAlign;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.SpanElement;
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
public class DictItemList extends LCTemplateTablePagerHandler implements IDictContextAware {

	private static Dict getDict(final PageRequestResponse rRequest) {
		Dict dict = (Dict) rRequest.getRequestAttr("select_dict");
		if (dict != null) {
			return dict;
		}
		dict = context.getDictService().getBean(rRequest.getParameter("dictId"));
		if (dict != null) {
			rRequest.setRequestAttr("select_dict", dict);
		}
		return dict;
	}

	@Override
	public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
		final Dict dict = getDict(cp);
		final IDictItemService service = context.getDictItemService();
		if (dict == null) {
			return service.queryAll();
		} else {
			cp.addFormParameter("dictId", dict.getId());
			return service.queryItems(dict);
		}
	}

	private static final MenuItems CONTEXT_MENUS = MenuItems
			.of()
			.append(MenuItem.itemEdit().setOnclick_act("DictMgrPage_itemWin", "itemId"))
			.append(MenuItem.itemDelete().setOnclick_act("DictMgrPage_Delete", "id"))
			.append(MenuItem.sep())
			.append(MenuItem.itemLog().setOnclick_act("DictMgrPage_logWin", "beanId"))
			.append(MenuItem.sep())
			.append(
					MenuItem
							.of($m("Menu.move"))
							.addChild(
									MenuItem.of($m("Menu.up"), MenuItem.ICON_UP,
											"$pager_action(item).move(true, 'DictMgrPage_Move');"))
							.addChild(
									MenuItem.of($m("Menu.up2"), MenuItem.ICON_UP2,
											"$pager_action(item).move2(true, 'DictMgrPage_Move');"))
							.addChild(
									MenuItem.of($m("Menu.down"), MenuItem.ICON_DOWN,
											"$pager_action(item).move(false, 'DictMgrPage_Move');"))
							.addChild(
									MenuItem.of($m("Menu.down2"), MenuItem.ICON_DOWN2,
											"$pager_action(item).move2(false, 'DictMgrPage_Move');")));

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
				final Dict dict = getDict(cp);
				if (dict == null) {
					final TablePagerColumns columns = new TablePagerColumns(
							super.getTablePagerColumns(cp));
					columns.add(4, new TablePagerColumn("dictId", $m("DictItemList.1"), 140)
							.setTextAlign(ETextAlign.left));
					return columns;
				}
				return super.getTablePagerColumns(cp);
			}

			@Override
			public Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
				final DictItem item = (DictItem) dataObject;
				final ID id = item.getId();
				final KVMap kv = new KVMap();
				final StringBuilder sb = new StringBuilder();
				sb.append(item.getText());
				final String desc = item.getDescription();
				if (StringUtils.hasText(desc)) {
					sb.append(BlockElement.tip(desc));
				}

				kv.put("text", sb.toString());
				kv.put("codeNo", item.getCodeNo());
				kv.put("itemMark", item.getItemMark());
				final DictItem parent = context.getDictItemService().getBean(item.getParentId());
				if (parent != null) {
					kv.put("parentId", parent.getText());
				}
				if (getDict(cp) == null) {
					final Dict dict2 = context.getDictService().getBean(item.getDictId());
					if (dict2 != null) {
						kv.put("dictId", dict2.getText());
					}
				}

				sb.setLength(0);
				sb.append(ButtonElement.editBtn().setOnclick(
						"$Actions['DictMgrPage_itemWin']('itemId=" + id + "');"));
				sb.append(SpanElement.SPACE);
				sb.append(ButtonElement.logBtn().setOnclick(
						"$Actions['DictMgrPage_logWin']('beanId=" + id + "');"));
				sb.append(SpanElement.SPACE).append(IMG_DOWNMENU);
				kv.put(TablePagerColumn.OPE, sb.toString());
				return kv;
			}
		};
	}

	@Override
	protected ElementList getNavigationTitle(final ComponentParameter cp) {
		return doNavigationTitle(cp, getDict(cp), new NavigationTitleCallback<Dict>() {
			@Override
			protected String rootText() {
				return $m("DictItemList.0");
			}

			@Override
			protected String categoryIdKey() {
				return "dictId";
			}

			@Override
			protected boolean isLink(final Dict t) {
				return t.getDictMark() != EDictMark.category;
			}

			@Override
			protected Dict get(final Object id) {
				return context.getDictService().getBean(id);
			}

			@Override
			protected String getText(final Dict t) {
				return !isLink(t) ? super.getText(t) : t.getText()
						+ SpanElement.shortText("(" + t.getName() + ")");
			}
		});
	}
}