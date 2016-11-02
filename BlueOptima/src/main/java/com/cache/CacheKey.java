package com.cache;

import java.io.Serializable;

/**
 * The Class CacheKey.
 */
public final class CacheKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8486764429840157972L;

	/** The key. */
	private final String key;

	/**
	 * Instantiates a new cache key.
	 *
	 * @param key
	 *            the key
	 */
	public CacheKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CacheKey)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		CacheKey k1 = (CacheKey) obj;
		return key.equals(k1.getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return key.hashCode();
	}

}
