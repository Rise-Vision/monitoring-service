package com.risevision.monitoring.api.response;

import com.google.api.server.spi.response.CollectionResponse;

import java.util.Collection;

public class APIResponse<T> extends CollectionResponse<T> {
	
	private final T item;
	
	public static class Builder<T> extends CollectionResponse.Builder<T>  {
		
		private T item;
		private String cursor;
		private Collection<T> items;

		public Builder() {

			this.item = null;
			this.cursor = null;
			this.items = null;
		}
		
		public Builder<T> setItem(T item) {
			this.item = item;
			return this;
		}
		
		public Builder<T> setItems(Collection<T> items) {
			this.items = items;
			return this;
		}
		
		public Builder<T> setCursor(String cursor) {
			this.cursor = cursor;
			return this;
		}

        public APIResponse<T> build() {
            return new APIResponse<T>(this.item, this.items, this.cursor);
        }
    }

	protected APIResponse(T item, Collection<T> items, String cursor) {
		
		super(items, cursor);
		this.item = item;
	}
	
	public static <T> Builder<T> builder() {
		
		return new Builder<T>();
	}
	
	public T getItem() {
		return item;
	}
	
	public String getCursor() {
		return super.getNextPageToken();
	}

	public Collection<T> getItems() {
		return super.getItems();
	}

}
