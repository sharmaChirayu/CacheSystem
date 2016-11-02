package com.cache;

/**
 * The Class CacheCleanup is used to evict entries from a cache following a time
 * based eviction which is measured since the last access or last write. An
 * instance of this class runs as a daemon thread and periodically scans the
 * cache to identify the entries eligible for eviction.
 */
public class CacheCleanup extends Thread {

	/** The cache instance */
	private Cache<?> cache;

	/** The cleanup interval. */
	private int cleanupInterval;

	/**
	 * Instantiates a new cache cleanup.
	 *
	 * @param cache
	 *            The cache to be cleaned up.
	 * @param interval
	 *            The time interval(in minutes) at which this thread runs
	 */
	protected CacheCleanup(Cache<?> cache, int interval) {
		this.cache = cache;
		this.cleanupInterval = interval;
		setDaemon(true);
		setName("Cleanup Daemon");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			cache.removeAllExpired();
			try {
				sleep(cleanupInterval * 60 * 1000);
			} catch (InterruptedException e) {
				System.err
						.println("Exception occurred while cleaning up the cache::" + cache.getName() + e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
