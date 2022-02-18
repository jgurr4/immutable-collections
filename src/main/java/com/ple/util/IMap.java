package com.ple.util;

import java.util.Iterator;

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

  default String toKVString() {
    final StringBuilder b = new StringBuilder();
    Iterator<IEntry<K, V>> iterator = iterator();
    boolean first = true;
    while (iterator.hasNext()) {
      IEntry<K, V> entry = iterator.next();
      if (!first) {
        b.append(" ");
      }
      b.append(entry.key + "=" + entry.value);
      first = false;
    }
    return b.toString();
  }

}
