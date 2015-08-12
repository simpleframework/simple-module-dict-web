package net.simpleframework.module.dict.web.component.dictSelect;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.listbox.ListItem;
import net.simpleframework.mvc.component.ui.listbox.ListItems;
import net.simpleframework.mvc.component.ui.listbox.ListboxBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictListSelectHandler extends AbstractDictSelectHandler implements
		IDictListSelectHandler {

	protected ListItem createItem(final ListboxBean listbox, final DictItem dictItem) {
		final ListItem item = new ListItem(listbox, dictItem.getText());
		item.setId(dictItem.getCodeNo());
		return item;
	}

	protected ID getOrgId(final PageParameter pp) {
		return pp.getLdept().getDomainId();
	}

	@Override
	public ListItems getDictItems(final ComponentParameter cp, final ListboxBean listbox) {
		final Dict dict = getDict(cp);
		if (dict == null) {
			return null;
		}

		final ListItems items = ListItems.of();
		final IDataQuery<DictItem> dq = _dictItemService.queryItems(dict, getOrgId(cp));
		DictItem dictItem;
		while ((dictItem = dq.next()) != null) {
			final ListItem item = createItem(listbox, dictItem);
			if (item != null) {
				items.add(item);
			}
		}
		return items;
	}
}
