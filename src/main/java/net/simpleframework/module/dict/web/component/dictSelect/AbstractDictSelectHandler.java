package net.simpleframework.module.dict.web.component.dictSelect;

import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.dictionary.AbstractDictionaryHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractDictSelectHandler extends AbstractDictionaryHandler
		implements IDictContextAware {

	protected Dict getDict(final ComponentParameter cp) {
		String dictName = cp.getParameter("dictName");
		if (!StringUtils.hasText(dictName)) {
			dictName = (String) cp.getBeanProperty("dictName");
		}
		return _dictService.getDictByName(dictName);
	}

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		if ("title".equals(beanProperty)) {
			final Dict dict = getDict(cp);
			if (dict != null) {
				return dict.getText();
			}
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		final KVMap parameters = (KVMap) super.getFormParameters(cp);
		final Dict dict = getDict(cp);
		if (dict != null) {
			parameters.add("dictName", dict.getName());
		}
		return parameters;
	}
}
