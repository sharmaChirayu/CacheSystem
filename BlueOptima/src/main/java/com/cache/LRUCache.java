package com.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class LRUCache is an concrete class which extends the Cache class and
 * represents a cache which uses LRU (Least recently used) purging mechanism to
 * take the decision of identifying which element should be removed if the
 * storage limit exceeds.
 *
 * @param <V>
 *            the value type of CacheElement
 */
public class LRUCache<V> extends Cache<V> {

	/** The Constant DEFAULT_CACHE_SIZE. */
	private static final int DEFAULT_CACHE_SIZE = 1000;

	/** The Constant DEFAULT_TIME_TO_LIVE.(in minutes) */
	private static final int DEFAULT_TIME_TO_LIVE = 2;

	/**
	 * The lock object to use for synchronizing the insertion, deletion and
	 * retrieval of elements.
	 */
	private final Object lock = new Object();

	/**
	 * Instantiates a new LRU cache.
	 *
	 * @param name
	 *            the name of cache
	 */
	protected LRUCache(String name) {
		this(name, DEFAULT_CACHE_SIZE, DEFAULT_TIME_TO_LIVE);
	}

	/**
	 * Instantiates a new LRU cache.
	 *
	 * @param name
	 *            the name
	 * @param size
	 *            the maximum number of elements allowed in this cache
	 * @param ttl
	 *            the maximum amount of time an elements reside in this cache
	 */
	protected LRUCache(String name, int size, int ttl) {
		cacheName = name;
		cacheSize = size;
		timeToLive = ttl;
		cacheMap = new LinkedHashMap<CacheKey, CacheElement<V>>(cacheSize, 1.01f, true) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<CacheKey, CacheElement<V>> eldest) {
				return size() > cacheSize;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cache#insert(com.cache.CacheElement)
	 */
	@Override
	public void insert(CacheElement<V> cacheElement) {
		synchronized (lock) {
			cacheMap.put(cacheElement.getId(), cacheElement);
			cacheElement.setExpiration(timeToLive);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cache#get(com.cache.CacheKey)
	 */
	@Override
	public CacheElement<V> get(CacheKey cachekey) {
		synchronized (lock) {
			CacheElement<V> cacheElement = (CacheElement<V>) cacheMap.get(cachekey);
			if (cacheElement == null) {
				return null;
			} else {
				if (cacheElement.isExpired()) {
					remove(cacheElement.getId());
				}
			}
			cacheElement.setExpiration(timeToLive);
			return cacheElement;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cache#remove(com.cache.CacheKey)
	 */
	@Override
	public boolean remove(CacheKey cacheKey) {
		synchronized (lock) {
			boolean isRemoved = false;
			if (cacheMap.containsKey(cacheKey)) {
				cacheMap.remove(cacheKey);
				isRemoved = true;
			}
			return isRemoved;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cache#getName()
	 */
	@Override
	public String getName() {
		return cacheName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cache#removeAllExpired()
	 */
	@Override
	public void removeAllExpired() {
		if (cacheMap.isEmpty()) {
			return;
		}
		synchronized (lock) {
			Set<Entry<CacheKey, CacheElement<V>>> entries = cacheMap.entrySet();
			Iterator<Entry<CacheKey, CacheElement<V>>> itr = entries.iterator();
			while (itr.hasNext()) {
				Entry<CacheKey, CacheElement<V>> entry = itr.next();
				if (entry.getValue().isExpired()) {
					System.out.println("Evicting the entry::" + entry.getValue().getValue());
					itr.remove();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return cacheMap.entrySet().stream().map(e -> e.getValue().toString()).collect(Collectors.joining(","));
	}

}
