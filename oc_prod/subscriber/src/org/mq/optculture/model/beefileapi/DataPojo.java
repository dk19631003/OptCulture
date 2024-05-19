package org.mq.optculture.model.beefileapi;

import java.util.List;

public class DataPojo {

	private DirPojo meta;
	private List<FilePojo> items;
	public DirPojo getMeta() {
		return meta;
	}
	public void setMeta(DirPojo meta) {
		this.meta = meta;
	}
	public List<FilePojo> getItems() {
		return items;
	}
	public void setItems(List<FilePojo> items) {
		this.items = items;
	}
	
}
