package com.ple.util;

import java.util.Objects;

@Immutable
class IHashMapEntry<K, V> implements IEntry<K, V> {

  private final K key;
  private final V value;
  public static IHashMapEntry empty = new IHashMapEntry(new IHashMapEntry[0], 10);

  private IHashMapEntry(K key, V value) {

    this.key = key;
    this.value = value;
  }

  public static <K, V> IHashMapEntry<K, V> from(K key, V value) {

    return new IHashMapEntry<>(key, value);
  }

  public K getKey() {

    return key;
  }

  public V getValue() {

    return value;
  }

  public IHashMapEntry<K, V> setValue(V v) {

    return new IHashMapEntry<K, V>(key, v);
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (!(o instanceof IHashMapEntry)) return false;
    IHashMapEntry<?, ?> that = (IHashMapEntry<?, ?>) o;
    return getKey().equals(that.getKey()) && getValue().equals(that.getValue());
  }

  @Override
  public int hashCode() {

    return Objects.hash(getKey(), getValue());
  }

  @Override
  public String toString() {

    return "IHashMapEntry{" +
      "key=" + key +
      ", value=" + value +
      '}';
  }

}
