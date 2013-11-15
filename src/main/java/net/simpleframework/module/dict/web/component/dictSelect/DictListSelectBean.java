package net.simpleframework.module.dict.web.component.dictSelect;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictListSelectBean extends DictionaryBean {
	private String dictName;

	public DictListSelectBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(pageDocument, xmlElement);
		setTitle($m("DictSelectBean.0"));
		setHandleClass(DictListSelectHandler.class);
	}

	public String getDictName() {
		return dictName;
	}

	public DictListSelectBean setDictName(final String dictName) {
		this.dictName = dictName;
		return this;
	}
}
