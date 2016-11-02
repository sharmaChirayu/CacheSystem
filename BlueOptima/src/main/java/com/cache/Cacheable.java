package com.cache;

/**
 * The Interface Cacheable represents an element which can be stored
 * in a cache.
 */
public interface Cacheable {

	/**
	 * Return the CacheKey of the current element.
	 *
	 * @return the id of the element.
	 */
	public CacheKey getId();

	/**
	 * Check whether the current element has expired or not.
	 *
	 * @return true, if it is expired.
	 */
	public boolean isExpired();

}
