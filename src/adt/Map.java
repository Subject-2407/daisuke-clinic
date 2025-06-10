package adt;

import java.util.Objects;

// Generic Map

public class Map<K, V> {
    static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private static final int DEFAULT_CAPACITY = 16; // initial capacity
    private static final float LOAD_FACTOR = 0.75f; // automatically resize when 75% full
    private int size = 0;

    @SuppressWarnings("unchecked")
    public Map() {
        buckets = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
    }

    // hashing
    private int getBucketIndex(K key) {
        return Math.abs(Objects.hashCode(key)) % buckets.length;
    }

    // insert or update a key-value pair
    public void put(K key, V value) {
        if ((float) size / buckets.length >= LOAD_FACTOR) {
            resize();
        }

        int index = getBucketIndex(key);
        Entry<K, V> head = buckets[index];

        while (head != null) {
            if (Objects.equals(head.key, key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value, buckets[index]);
        buckets[index] = newEntry;
        size++;
    }

    // gets the value of a key
    public V get(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> head = buckets[index];

        while (head != null) {
            if (Objects.equals(head.key, key)) {
                return head.value;
            }
            head = head.next;
        }
        return null;
    }

    // check if map contains specific key
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // removes a key
    public V remove(K key) {
        int index = getBucketIndex(key);
        Entry<K, V> head = buckets[index];
        Entry<K, V> prev = null;

        while (head != null) {
            if (Objects.equals(head.key, key)) {
                if (prev != null) {
                    prev.next = head.next;
                } else {
                    buckets[index] = head.next;
                }
                size--;
                return head.value;
            }
            prev = head;
            head = head.next;
        }
        return null;
    }

    // get all keys
    public Object[] keySet() {
        Object[] keys = new Object[size];
        int index = 0;
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                keys[index++] = current.key;
                current = current.next;
            }
        }
        return keys;
    }

    // resizes when almost full
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[oldBuckets.length * 2];
        size = 0;

        for (Entry<K, V> head : oldBuckets) {
            while (head != null) {
                put(head.key, head.value); // rehash
                head = head.next;
            }
        }
    }

}
