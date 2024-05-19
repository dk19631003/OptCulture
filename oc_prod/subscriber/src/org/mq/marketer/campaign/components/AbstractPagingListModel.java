package org.mq.marketer.campaign.components;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelList;

public abstract class AbstractPagingListModel<T> extends ListModelList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6613208067174831719L;
	
	private int _startPageNumber;
	private int _pageSize;
	private int _itemStartNumber;
	private String _query;
	private String _countQuery;
	
	//internal use only
	private List<T> _items = new ArrayList<T>();
	
	public AbstractPagingListModel(int startPageNumber, int pageSize, String query, String countQuery) {
		super();
		
		this._startPageNumber = startPageNumber;
		this._pageSize = pageSize;
		this._itemStartNumber = startPageNumber * pageSize;
		this._query=query;
		this._countQuery=countQuery;
		
		_items = getPageData(_itemStartNumber, _pageSize, _query, _countQuery);
	}
	
	public abstract int getTotalSize();
	protected abstract List<T> getPageData(int itemStartNumber, int pageSize, String query, String countQuery);
	
	@Override
	public Object getElementAt(int index) {
		return _items.get(index);
	}

	@Override
	public int getSize() {
		return _items.size();
	}
	
	public int getStartPageNumber() {
		return this._startPageNumber;
	}
	
	public int getPageSize() {
		return this._pageSize;
	}
	
	public int getItemStartNumber() {
		return _itemStartNumber;
	}


	public String get_query() {
		return _query;
	}

	public String get_countQuery() {
		return _countQuery;
	}

}
