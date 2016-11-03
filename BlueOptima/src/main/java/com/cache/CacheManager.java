package com.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.exception.ConfFileNotFoundException;

/**
 * The Class CacheManager is a builder which generates instances of Cache. This
 * class is a singleton and is responsible for reading the configuration file to
 * determine the cache properties. The caches are created with the following
 * characteristics
 * <ul>
 * <li>The number of entries in a cache are limited by the maxSize property in
 * the configuration file.
 * <li>Least-recently-used eviction when a maximum size is exceeded
 * <li>Time-based expiration of entries, measured since last access or last
 * write.
 * <li>File system backup of the cache to facilitate restoration on restart.
 * </ul>
 *
 */
public class CacheManager {

	/** The Constant CACHE_CONFIG_FILE. */
	private static final String CACHE_CONFIG_FILE = "resources/CacheConfig.properties";

	/** The Constant CACHE_SIZE. */
	private static final String CACHE_SIZE = "maxSize";

	/** The Constant TIME_TO_LIVE. */
	private static final String TIME_TO_LIVE = "timeToLive";

	/** The Constant BACKUP. */
	private static final String BACKUP = "backup";

	/** The Constant CLEANUP_INTERVAL. */
	private static final String CLEANUP_INTERVAL = "CleanupInterval";

	/** The Constant BACKUP_PATH. */
	private static final String BACKUP_PATH = "BackupPath";

	/** The cache manager. */
	private static volatile CacheManager cacheManager;

	/** The cache properties. */
	private Properties cacheProperties;

	/** The cache backup. */
	private CacheBackup cacheBackup;

	/** The list of caches. */
	private HashMap<String, Cache<?>> listOfCaches;

	/** The maximum cache size. */
	private int cacheSize;

	/** The time to live. */
	private int timeToLive;

	/** The time interval after which a cleanup of the cache is triggered. */
	private int cleanupInterval;

	/** The path where all the caches are backed up. */
	private String backupPath;

	/** The path where all the caches are backed up. */
	private Path cacheBackupPath;

	/** The flag to indicate whether backup has been enabled or not. */
	private boolean isBackupEnabled;

	/**
	 * Instantiates a new cache manager.
	 *
	 * @throws ConfFileNotFoundException
	 *             The cache configuration file is not found
	 */
	private CacheManager() throws ConfFileNotFoundException {
		/*
		 * Read the configuration file
		 */
		String cacheConfigFile = Thread.currentThread().getContextClassLoader().getResource(CACHE_CONFIG_FILE)
				.getFile();
		if (cacheConfigFile.equals("") || cacheConfigFile == null) {
			throw new ConfFileNotFoundException("Cache configuration file not found");
		}
		cacheProperties = new Properties();
		try {
			cacheProperties.load(new FileInputStream(cacheConfigFile));
			/*
			 * Read the cache properties
			 */
			initialize();
		} catch (FileNotFoundException e) {
			System.err.println("Cache configuration file not found" + e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O exception occurred while reading cache configuration file" + e.getMessage());
		}

	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		cacheSize = Integer.parseInt(cacheProperties.getProperty(CACHE_SIZE));
		timeToLive = Integer.parseInt(cacheProperties.getProperty(TIME_TO_LIVE));
		cleanupInterval = Integer.parseInt(cacheProperties.getProperty(CLEANUP_INTERVAL));

		backupPath = cacheProperties.getProperty(BACKUP_PATH);
		cacheBackupPath = Paths.get(backupPath);

		isBackupEnabled = Boolean.parseBoolean(cacheProperties.getProperty(BACKUP));

		/**
		 * Initialize the cache backup process as a shutdown hook
		 */
		if (isBackupEnabled) {
			cacheBackup = CacheBackup.getCacheBackup(cacheBackupPath);
			Runtime.getRuntime().addShutdownHook(cacheBackup);
		}

		listOfCaches = new HashMap<String, Cache<?>>();
	}

	/**
	 * Gets the CacheManager instance.
	 *
	 * @return the cache manager
	 * @throws ConfFileNotFoundException
	 *             the configuration file not found exception
	 */
	public static CacheManager getCacheManager() throws ConfFileNotFoundException {
		if (cacheManager == null) {
			synchronized (CacheManager.class) {
				if (cacheManager == null) {
					cacheManager = new CacheManager();
				}
			}
		}
		return cacheManager;
	}

	/**
	 * Gets an instance of a cache.
	 *
	 * @param <T>
	 *            the generic type
	 * @param name
	 *            the name
	 * @return the cache
	 */
	@SuppressWarnings("unchecked")
	public <T> Cache<T> getCache(String name) {
		if (listOfCaches.containsKey(name)) {
			return (Cache<T>) listOfCaches.get(name);
		}
		Cache<T> cache = null;
		cache = new LRUCache<T>(name, cacheSize, timeToLive);

		if (isBackupEnabled) {
			addCacheForBackup(cache);
		}

		/*
		 * Initialize the cleanup thread
		 */
		intializeCacheCleanup(cache);

		listOfCaches.put(name, cache);

		return cache;
	}

	/**
	 * Create and start the cleanup thread for a cache.
	 *
	 * @param cache
	 *            the cache instance
	 */
	private void intializeCacheCleanup(Cache<?> cache) {
		/*
		 * Initialize the cleanup thread
		 */
		CacheCleanup cacheCleanup = new CacheCleanup(cache, cleanupInterval);
		cacheCleanup.start();
	}

	/**
	 * Mark the cache to be backed up on file system.
	 *
	 * @param cache
	 *            the cache instance
	 */
	private void addCacheForBackup(Cache<?> cache) {
		/*
		 * Initialize the cleanup thread
		 */
		if (isBackupEnabled) {
			cacheBackup.addCache(cache);
		}
	}

	/**
	 * Restore the cache from the backup file.
	 *
	 * @param cacheName
	 *            the cache name
	 * @return the cache
	 */
	@SuppressWarnings("unchecked")
	public <T> Cache<T> restoreCache(String cacheName) {
		File backupFile = cacheBackupPath.resolve(cacheName + ".ser").toFile();
		FileInputStream fi;
		ObjectInputStream ostream;
		Cache<T> cache = null;
		try {
			fi = new FileInputStream(backupFile);
			ostream = new ObjectInputStream(fi);
			List<CacheElement<T>> objectList;

			try {
				objectList = (List<CacheElement<T>>) ostream.readObject();
				Iterator<CacheElement<T>> itr = objectList.iterator();
				cache = getCache(cacheName);
				while (itr.hasNext()) {
					cache.insert((CacheElement<T>) itr.next());
				}
			} catch (ClassNotFoundException e) {
				System.err.println("CacheElement class not found" + e.getMessage());
				e.printStackTrace();
			}
			ostream.close();
			fi.close();
			removeBackupFile(backupFile);
		} catch (FileNotFoundException e) {
			System.err.println("Backup file " + backupFile + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO exception while backing up the cache" + e.getMessage());
			e.printStackTrace();
		}
		return cache;
	}

	/**
	 * Removes the backup file.
	 *
	 * @param backupFile
	 *            the backup file
	 */
	private void removeBackupFile(File backupFile) {
		if (backupFile.isFile()) {
			backupFile.delete();
		}
	}
}
