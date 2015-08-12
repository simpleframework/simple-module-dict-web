package net.simpleframework.module.dict.web.page;

import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.mvc.component.ComponentParameter;

public abstract class DictUtils {

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
