package com.cache.test;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.cache.Cache;
import com.cache.CacheElement;
import com.cache.CacheKey;
import com.cache.CacheManager;
import com.exception.ConfFileNotFoundException;

public class CacheTest {

	private CacheManager cacheManager;

	@Before
	public void setUp() throws Exception {
		/*
		 * Create a cache manager
		 */
		try {
			cacheManager = CacheManager.getCacheManager();
		} catch (ConfFileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void cacheMemberTest() {
		Cache<String> stringCache = cacheManager.<String> getCache("StringCache");

		stringCache.insert(new CacheElement<String>(new CacheKey("1"), "String1", 1));
		stringCache.insert(new CacheElement<String>(new CacheKey("2"), "String2", 2));
		stringCache.insert(new CacheElement<String>(new CacheKey("3"), "String3", 1));
		stringCache.insert(new CacheElement<String>(new CacheKey("4"), "String4", 5));
		stringCache.insert(new CacheElement<String>(new CacheKey("5"), "String5", 1));
		stringCache.insert(new CacheElement<String>(new CacheKey("6"), "String6", 5));
		stringCache.insert(new CacheElement<String>(new CacheKey("7"), "String7", 1));
		stringCache.insert(new CacheElement<String>(new CacheKey("8"), "String8", 5));
		stringCache.insert(new CacheElement<String>(new CacheKey("9"), "String9", 5));
		stringCache.insert(new CacheElement<String>(new CacheKey("10"), "String10", 1));

		CacheElement<String> cacheElement = stringCache.get(new CacheKey("7"));
		assert (cacheElement.getValue().equals("String7"));
	}

	@Test
	public void cacheMemberEqualityTest() {
		Cache<Date> dateCache = cacheManager.<Date> getCache("DateCache");

		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();

		dateCache.insert(new CacheElement<Date>(new CacheKey("1"), date));

		CacheElement<Date> cacheElement = dateCache.get(new CacheKey("1"));

		assert (cacheElement.getValue() == date);
	}

	@Test
	public void cacheMembershipTest() {
		Cache<Double> doubleCache = cacheManager.<Double> getCache("DoubleCache");

		doubleCache.insert(new CacheElement<Double>(new CacheKey("1"), 2.5));

		CacheElement<Double> cacheElement = doubleCache.get(new CacheKey("8"));

		assert (cacheElement == null);
	}

	@Test
	public void cacheEvictionTest() {
		Cache<Float> floatCache = cacheManager.<Float> getCache("FloatCache");

		// The cache size is 10
		floatCache.insert(new CacheElement<Float>(new CacheKey("1"), 1.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("2"), 2.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("3"), 3.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("4"), 4.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("5"), 5.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("6"), 6.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("7"), 7.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("8"), 8.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("9"), 9.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("10"), 10.5f));

		// Print the contents of cache in the order of the access[The element at
		// the
		// end is the least recently accessed. In this case, it's 10.5f]
		System.out.println(floatCache);

		// Retrieve the element with ID 7
		CacheElement<Float> cacheElement = floatCache.get(new CacheKey("7"));

		// Now the element 7.5f should be the most recently accessed and
		// therefore should appear
		// at the end
		System.out.println(floatCache);

		// insert a new element
		floatCache.insert(new CacheElement<Float>(new CacheKey("11"), 11.5f));

		// The element with the key, "1" should be evicted from the cache

		cacheElement = floatCache.get(new CacheKey("1"));
		assert (cacheElement == null);
	}

	@Test
	public void cacheRestoreTest() {

		// Restore the cache of float values
		Cache<Float> restoredFloatCache = cacheManager.restoreCache("FloatCache");
		System.out.println(restoredFloatCache);

		restoredFloatCache.insert(new CacheElement<Float>(new CacheKey("13"), 13.5f));
		System.out.println(restoredFloatCache);
		assert (restoredFloatCache != null);
	}

	@Test
	public void multipleCacheTest() {

		// Cache for float values
		Cache<Float> floatCache = cacheManager.<Float> getCache("FloatCache");

		floatCache.insert(new CacheElement<Float>(new CacheKey("1"), 1.5f));
		floatCache.insert(new CacheElement<Float>(new CacheKey("2"), 2.5f));

		Cache<String> stringCache = cacheManager.<String> getCache("StringCache");
		stringCache.insert(new CacheElement<String>(new CacheKey("1"), "String1", 1));
		stringCache.insert(new CacheElement<String>(new CacheKey("2"), "String2", 2));

		System.out.println(floatCache);

		System.out.println(stringCache);

		assert (!(floatCache.toString().equals(stringCache.toString())));
	}

}
