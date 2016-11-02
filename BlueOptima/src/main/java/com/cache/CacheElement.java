package com.cache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * CacheElement is the class which represents the elements which can be stored
 * in a cache.
 * 
 * @param <V>
 *            the value type for all cache elements stored in it.
 */
public class CacheElement<V> implements Cacheable, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6015331521879386327L;

	/** The id of the cache element */
	private CacheKey id;

	/** The maximum time(in minutes) it resides in the cache. */
	private int timeToLive;

	/** The expiration time. */
	private Date expirationTime;

	/**
	 * The flag which specifies whether this element lives indefinitely. in the
	 * cache or not.
	 */
	private boolean livesIndefinitely;

	/** The value of the element */
	private V value;

	/**
	 * Instantiates a new cache element.
	 *
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 * @param timeToLive
	 *            the time to live
	 */
	public CacheElement(CacheKey id, V value, int timeToLive) {
		this.id = id;
		this.value = value;
		this.timeToLive = timeToLive;
		/*
		 * timeToLive 0 means that the element doesn't expire from cache and
		 * lives indefinitely.
		 */
		if (timeToLive > 0) {
			this.livesIndefinitely = false;
		} else {
			this.livesIndefinitely = true;
			expirationTime = null;
		}
	}

	/**
	 * Instantiates a new cache element.
	 *
	 * @param id
	 *            the id
	 * @param value
	 *            the value
	 */
	public CacheElement(CacheKey id, V value) {
		this(id, value, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cacheable#getId()
	 */
	@Override
	public CacheKey getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cache.Cacheable#isExpired()
	 */
	@Override
	public boolean isExpired() {
		boolean isExpired = false;
		if (!livesIndefinitely) {
			Date currentTime = new Date();
			if (expirationTime.before(currentTime)) {
				isExpired = true;
			}
		}
		return isExpired;
	}

	/**
	 * Sets the expiration time based on the time to live value.
	 */
	public void setExpiration() {
		expirationTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(expirationTime);
		calendar.add(Calendar.MINUTE, timeToLive);
		expirationTime = calendar.getTime();
	}

	/**
	 * Sets the expiration time based on the given time to live value.
	 *
	 * @param ttl
	 *            time to live(in minutes)
	 */
	public void setExpiration(int ttl) {
		if (ttl <= 0) {
			livesIndefinitely = true;
			return;
		}
		expirationTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(expirationTime);
		calendar.add(Calendar.MINUTE, ttl);
		expirationTime = calendar.getTime();
	}

	/**
	 * Gets the value.
	 *
	 * @return the value of cache element
	 */
	public V getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}

}
