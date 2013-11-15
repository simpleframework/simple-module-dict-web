package net.simpleframework.module.dict.web.component.dictSelect;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryRegistry;
import net.simpleframework.mvc.component.ui.listbox.AbstractListboxHandler;
import net.simpleframework.mvc.component.ui.listbox.ListItems;
import net.simpleframework.mvc.component.ui.listbox.ListboxBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentBean(DictListSelectBean.class)
@ComponentName(DictListSelectRegistry.DICTLISTSELECT)
@ComponentRender(DictSelectRender.class)
public class DictListSelectRegistry extends DictionaryRegistry {
	public static final String DICTLISTSELECT = "dictListSelect";

	@Override
	public DictionaryBean createComponentBean(final PageParameter pp, final Object attriData) {
		final DictListSelectBean dictSelect = (DictListSelectBean) super.createComponentBean(pp,
				attriData);
		final ComponentParameter nCP = ComponentParameter.get(pp, dictSelect);

		final String dictSelectName = nCP.getComponentName();
		final ListboxBean listbox = (ListboxBean) pp.addComponentBean(dictSelectName + "_list",
				ListboxBean.class).setHandleClass(DictList.class);
		dictSelect.addListboxRef(nCP, listbox.getName());
		listbox.setAttr("$dictSelect", dictSelect);

		return dictSelect;
	}

	public static class DictList extends AbstractListboxHandler {

		@Override
		public ListItems getListItems(final ComponentParameter cp) {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
			return ((IDictListSelectHandler) nCP.getComponentHandler()).getDictItems(nCP,
					(ListboxBean) cp.componentBean);
		}
	}
}
