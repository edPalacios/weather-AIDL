package ep.weather.operations;

/**
 * Created by Eduardo on 03/06/2015.
 * NOT IN USE
 */
public interface TimeoutCache <K, V>  {
    /**
     * Gets the @a value from the cache at the designated @a key.
     *
     * @param key
     * @return value
     */
    V get(K key);

    /**
     * Put the @a value into the cache at the designated @a key.
     *
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * Put the @a value into the cache at the designated @a key with a
     * timeout after which the data will expire.
     *
     * @param key
     * @param value
     * @param timeout in seconds
     */
    void put(K key, V obj, int timeout);

    /**
     * Removes the value associated with a key.
     *
     * @param key
     */
    void remove(K key);

    /**
     * Get the size of the cache.
     *
     * @return size
     */
    int size();
}

