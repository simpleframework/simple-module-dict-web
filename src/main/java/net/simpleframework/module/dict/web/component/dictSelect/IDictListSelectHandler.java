package net.simpleframework.module.dict.web.component.dictSelect;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.dictionary.IDictionaryHandle;
import net.simpleframework.mvc.component.ui.listbox.ListItems;
import net.simpleframework.mvc.component.ui.listbox.ListboxBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDictListSelectHandler extends IDictionaryHandle {

	/**
	 * 获取字典的条目列表
	 * 
	 * @param cParameter
	 * @param listbox
	 * @return
	 */
	ListItems getDictItems(ComponentParameter cp, ListboxBean listbox);
}
