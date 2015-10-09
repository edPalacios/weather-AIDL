package ep.weather.operations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Eduardo on 03/06/2015.
 * NOT IN USE
 *
 * Timeout cache that uses thread-safe concurrent HashMap to cache
 * data and uses a ScheduledExecutorService to execute a Runnable
 * after a designated timeout to remove expired cache entries.
 */

public class ExecutorServiceTimeoutCache <K, V>  implements TimeoutCache<K, V>  {
    /**
     * The timeout for an instance of this class.
     */
    private int mDefaultTimeout;

    /**
     * Thread-safe HashMap that supports full concurrency of
     * retrievals and high expected concurrency for updates. It is
     * used to store CacheValue objects.
     */
    private ConcurrentHashMap<K, CacheValues> mResults =
            new ConcurrentHashMap<>();

    /**
     * Executor service that will execute Runnable after certain
     * timeouts to remove expired CacheValues.
     */
    private ScheduledExecutorService mScheduledExecutorService =
            Executors.newScheduledThreadPool(1);

    /**
     * Datatype that represents the contents of the cache.  It
     * contains the value of the cache entity and a future that
     * executes a runnable after certain time period elapses to remove
     * expired CacheValue objects.
     */
    class CacheValues {
        /**
         * Value of the cache.
         */
        public V mValue;

        /**
         * Result of an asynchronous computation.  It references a
         * runnable that has been scheduled to execute after certain
         * time period elapses.
         */
        public ScheduledFuture<?> mFuture;

        /**
         * Constructor for CacheValue.
         *
         * @param value   The cache entry
         * @param future  A ScheduledFuture that can be used to cancel a Runnable
         */
        public CacheValues(V value, ScheduledFuture<?> future) {
            mValue = value;
            mFuture = future;
        }
    }

    /**
     * This constructor sets the default timeout to the designated @a
     * timeout parameter.
     */
    public ExecutorServiceTimeoutCache(int timeout) {
        mDefaultTimeout = timeout;
    }

    /**
     * Helper method that puts a @a value into the cache at the
     * designated @a key with a certain timeout, after which
     * CacheValue expires.
     *
     * @param key        The key for the cache entry
     * @param value      The value of the cache entry
     * @param timeout    The timeout period in seconds
     */
    private void putImpl(final K key,
                         V value,
                         int timeout) {
        // Runnable that when executed will remove a CacheValue which
        // is expired.

        // Create a ScheduledFuture that will execute the
        // cleanupCacheRunnable after the designated timeout.


        // Put a new CacheValues object into the ConcurrentHashMap
        // associated with the key and return the previous value.


        // If there is any previous cache value then cancel the
        // future immediately.

    }

    /**
     * Put the @a value into the cache at the designated @a key with
     * the default timeout.
     *
     * @param key        The key for the cache entry
     * @param value      The value of the cache entry
     */
    public void put(final K key, V value) {
        putImpl(key,
                value,
                mDefaultTimeout);
    }

    /**
     * Put the @a value into the cache at the designated @a key with a
     * certain timeout after which the CacheValue will expire.
     *
     * @param key        The key for the cache entry
     * @param value      The value of the cache entry
     * @param timeout    The timeout period in seconds
     */
    @Override
    public void put(K key, V value, int timeout) {
        putImpl(key,
                value,
                timeout);
    }

    /**
     * Gets the @a value from the cache at the designated @a key.
     *
     * @param key     The key for the cache entry
     * @return value  The value associated with the key, Which may be
     *                null if there's no key in the cache
     */
    @Override
    public final V get(K key) {
       return null;
    }

    /**
     * Removes the value associated with the designated @a key.
     *
     * @param key     The key for the cache entry
     */
    @Override
    public void remove(K key) {
        mResults.remove(key);
    }

    /**
     * Return the current number of entries in the cache.
     *
     * @return size
     */
    @Override
    public final int size() {
        return mResults.size();
    }
}
