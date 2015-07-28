package net.simpleframework.module.dict.web.page.mgr2;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictMgrTPage extends AbstractMgrTPage implements IDictContextAware {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='DictMgrTPage clearfix'>");
		sb.append("</div>");
		return sb.toString();
	}
}
