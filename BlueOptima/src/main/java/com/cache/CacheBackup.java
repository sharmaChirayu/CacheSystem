package com.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The Class CacheBackup is used to backup the cache values on the file system
 * which then later, can be used to restore the cache.
 */
public class CacheBackup extends Thread {

	/** The cache backup. */
	private static volatile CacheBackup cacheBackup;

	/** The backup path. */
	private Path backupPath;

	/** The cache set. */
	private Set<Cache<?>> cacheSet;

	/**
	 * Instantiates a new cache backup.
	 *
	 * @param path
	 *            the path
	 */
	private CacheBackup(Path path) {
		backupPath = path;
		cacheSet = new HashSet<Cache<?>>();
	}

	/**
	 * Gets the cache backup.
	 *
	 * @param path
	 *            the path
	 * @return the cache backup
	 */
	protected static CacheBackup getCacheBackup(Path path) {
		if (cacheBackup == null) {
			synchronized (CacheBackup.class) {
				if (cacheBackup == null) {
					cacheBackup = new CacheBackup(path);
				}
			}
		}
		return cacheBackup;
	}

	/**
	 * Adds the cache.
	 *
	 * @param <T>
	 *            the generic type
	 * @param cache
	 *            the cache
	 */
	protected <T> void addCache(Cache<T> cache) {
		cacheSet.add(cache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Iterator<Cache<?>> itr = cacheSet.iterator();
		while (itr.hasNext()) {
			backupCache(itr.next());
		}
	}

	/**
	 * Backup cache.
	 *
	 * @param <T>
	 *            the generic type
	 * @param cache
	 *            the cache
	 */
	private <T> void backupCache(Cache<T> cache) {
		File backupFile = backupPath.resolve(cache.getName() + ".ser").toFile();
		FileOutputStream fw;
		ObjectOutputStream ostream;
		try {
			List<CacheElement<T>> objectList = (List<CacheElement<T>>) cache.getValues();
			if (objectList.size() > 0) {
				fw = new FileOutputStream(backupFile);
				ostream = new ObjectOutputStream(fw);
				ostream.writeObject(objectList);
				ostream.flush();
				ostream.close();
				fw.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Backup file not found" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO exception while backing up the cache" + e.getMessage());
			e.printStackTrace();
		}
	}
}
