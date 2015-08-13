package net.simpleframework.module.dict.web.page;

import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.template.AbstractTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DictUtils implements IDictContextAware {

	public static Dict getDict(final PageParameter pp) {
		return AbstractTemplatePage.getCacheBean(pp, _dictService, "dictId");
	}

	public static String getIconPath(final ComponentParameter cp, final Dict dict) {
		final String imgBase = cp.getCssResourceHomePath(DictUtils.class) + "/images/";
		final EDictMark dictMark = dict.getDictMark();
		if (dictMark == EDictMark.category) {
			return imgBase + "dict_c.png";
		} else if (dictMark == EDictMark.normal) {
			return imgBase + "dict.png";
		} else if (dictMark == EDictMark.builtIn) {
			return imgBase + "dict_lock.png";
		}
		return null;
	}
}
