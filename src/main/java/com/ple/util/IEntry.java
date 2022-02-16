package com.ple.util;

import java.util.Objects;

@Immutable
class IEntry<K, V> {

  public static <K, V> IEntry<K, V> make(K key, V value) {
    return new IEntry<>(key, value);
  }

  public final K key;
  public final V value;

  private IEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public IEntry<K, V> setKey(K k) {
    return new IEntry<>(k, value);
  }

  public IEntry<K, V> setValue(V v) {
    return new IEntry<>(key, v);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IEntry)) return false;
    IEntry<?, ?> that = (IEntry<?, ?>) o;
    return key.equals(that.key) && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return "IEntry{" + key + ", " + value + "}";
  }

}
