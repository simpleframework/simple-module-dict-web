package net.simpleframework.module.dict.web.component.dictSelect;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictListSelectBean extends DictionaryBean {
	private static final long serialVersionUID = -5342415775392175185L;

	private String dictName;

	public DictListSelectBean() {
		setTitle($m("DictSelectBean.0"));
		setHandlerClass(DictListSelectHandler.class);
	}

	public String getDictName() {
		return dictName;
	}

	public DictListSelectBean setDictName(final String dictName) {
		this.dictName = dictName;
		return this;
	}
}
