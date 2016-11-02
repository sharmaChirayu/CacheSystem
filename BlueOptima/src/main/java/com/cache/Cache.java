package com.cache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cache is the abstract base class for all caches which allow an application to
 * maintain a cache for itself. The cache entries are manually added using
 * {@link #insert(CacheElement)}, and are stored in the cache until removed
 * manually or become invalidated based on the timeToLive property.
 * 
 * @param <V>
 *            the value type for all cache elements stored in it.
 */
public abstract class Cache<V> {

	/** The cache name. */
	protected String cacheName;

	/** The cache map. */
	protected Map<CacheKey, CacheElement<V>> cacheMap;

	/** The cache size. */
	protected int cacheSize;

	/** The time to live. */
	protected int timeToLive;

	/**
	 * Insert a new element in the cache.
	 *
	 * @param cacheElement
	 *            the cache element to be inserted
	 */
	public abstract void insert(CacheElement<V> cacheElement);

	/**
	 * Retrieve the element with the given key from the cache.
	 *
	 * @param cachekey
	 *            the ID
	 * @return the cache element
	 */
	public abstract CacheElement<V> get(CacheKey cachekey);

	/**
	 * Remove the element with the given key from the cache.
	 *
	 * @param cacheKey
	 *            the cache key
	 * @return true, if successful
	 */
	public abstract boolean remove(CacheKey cacheKey);

	/**
	 * Remove all the elements from the cache which have expired.
	 */
	public abstract void removeAllExpired();

	/**
	 * Return the name of the cache.
	 *
	 * @return the cache name
	 */
	public abstract String getName();

	/**
	 * Return all the objects in the cache.
	 *
	 * @return the values
	 */
	public List<CacheElement<V>> getValues() {
		return cacheMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
	}
}
