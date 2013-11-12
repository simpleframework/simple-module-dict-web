package net.simpleframework.module.dict.web.component.dictSelect;

import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DictTreeSelectBean extends DictListSelectBean {
	private boolean cookies = true;

	private boolean dynamicTree;

	public DictTreeSelectBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(pageDocument, xmlElement);
		setHandleClass(DictTreeSelectHandler.class);
	}

	public boolean isDynamicTree() {
		return dynamicTree;
	}

	public DictTreeSelectBean setDynamicTree(final boolean dynamicTree) {
		this.dynamicTree = dynamicTree;
		return this;
	}

	public boolean isCookies() {
		return cookies;
	}

	public DictTreeSelectBean setCookies(final boolean cookies) {
		this.cookies = cookies;
		return this;
	}
}
