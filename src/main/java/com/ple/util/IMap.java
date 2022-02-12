package com.ple.util;

@Immutable
public interface IMap<K, V> extends Iterable<IEntry<K, V>> {

  IMap<K, V> putAll(Iterable<IEntry<K, V>> entriesToAdd);

  IMap<K, V> putAll(Object... keyOrValue);

  IMap<K, V> put(K key, V value);

  IMap<K, V> remove(K key);

  int size();

  V get(K key);

  IList<K> keys();

  IList<V> values();

}
