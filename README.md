# CacheSystem
Light in-memory data store backed by filesystem which can be used as a cache.
The idea behind this solution is to store the elements in memory in the form of a key value pair using the Map data structure. This solution provides a means to manage cached data of various type. The cache contents are backed up on file system to facilitate process restart. The basic idea behind the solution is as follows.

a) The elements are manually inserted in the cache and they are kept in it until they are explicitly removed or become invalidated using a time based expiration.

b) The maximum number of elements which can be stored in the cache are restricted and upon exceeding the size, LRU based eviction is used.

c) The insertion, deletion and retrieval processes of a cache are synchronized.

d) For every cache instance a separate daemon thread runs in the background which periodically scans the cache elements to identify the expired elements and subsequently remove them from the cache.

e) A separate thread is responsible for backing up all the cache instances in memory. This thread is added as a shut down hook. All the individual cache instances are backed up in their separate files. These files are named as “<Cache_Name>.ser”.

f) A cache configuration file (“CacheConfig.properties”) is used to define the different cache properties. The various properties which can be defined are as follows.
  a) maxSize: The maximum number of elements allowed in a cache.
  b) timeToLive: The maximum time (in minutes) for which an element can reside in the cache. This value is specified as a whole number and is  measured since the last write or access operation.
  c) backup: This boolean(true or false) value identifies whether the caches are backed up on file system or not.
  d) BackupPath: The path of the directory where the files will be stored.
  e) CleanupInterval: The time interval (in minutes) after which the periodic clean up process repeats.
