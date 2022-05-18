package com.ple.util;

import java.util.*;

@Immutable
public class IArrayMap<K, V> implements IMap<K, V> {

  public static final IArrayMap empty = new IArrayMap(new Object[0]);

  public static <K, V> IArrayMap<K, V> make(Object... keyCommaValue) {
    assert keyCommaValue.length % 2 == 0;
    if (keyCommaValue.length == 0) return empty;
    return new IArrayMap<K, V>(keyCommaValue);
  }

  private final Object[] entries;

  private IArrayMap(Object[] entries) {
    this.entries = entries;
  }

  @Override
  public IArrayMap<K, V> putAll(Iterable<IEntry<K, V>> entriesToAdd) {

    ArrayList<Object> list = new ArrayList<>();
    for (IEntry<K, V> next : entriesToAdd) {
      list.add(next.key);
      list.add(next.value);
    }

    return putAll(list.toArray());

  }

  private int getEntryIndex(K key) {
    for (int i = 0; i < entries.length; i += 2) {
      if (Objects.equals(entries[i], key)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public IArrayMap<K, V> putAll(Object... keyCommaValue) {

    if (keyCommaValue.length % 2 != 0) {
      throw new IllegalArgumentException("IHashMap.putAll must have an even number of parameters");
    }

    final Object[] oldEntries = new Object[entries.length];
    System.arraycopy(entries, 0, oldEntries, 0, entries.length);
    final Object[] newEntries = new Object[keyCommaValue.length];
    int newEntryIndex = 0;

    for (int i = 0; i < keyCommaValue.length; i += 2) {

      K key = (K) keyCommaValue[i];
      V value = (V) keyCommaValue[i + 1];

      int existing = getEntryIndex(key);
      if (existing >= 0) {
        oldEntries[existing] = key;
        oldEntries[existing + 1] = value;
      } else {
        newEntries[newEntryIndex] = key;
        newEntries[newEntryIndex + 1] = value;
        newEntryIndex += 2;
      }
    }

    final Object[] finalEntries = new Object[entries.length + newEntryIndex];
    System.arraycopy(oldEntries, 0, finalEntries, 0, oldEntries.length);
    System.arraycopy(newEntries, 0, finalEntries, oldEntries.length, newEntryIndex);

    return new IArrayMap<>(finalEntries);

  }

  /**
   * Avoid using .put() in a loop of adding multiple values. This is because it will force Java to
   * recreate the IArrayMap object for each loop/entry. Instead, you should use .putAll() method since that
   * will only recreate the IArrayMap one time no matter how many entries you put in.
   */
  @Override
  public IArrayMap<K, V> put(K key, V value) {

    int i = getEntryIndex(key);

    Object[] newEntries;
    if (i >= 0) {

      newEntries = new Object[entries.length];
      System.arraycopy(entries, 0, newEntries, 0, entries.length);
      newEntries[i] = key;
      newEntries[i + 1] = value;

    } else {

      newEntries = new Object[entries.length + 2];
      System.arraycopy(entries, 0, newEntries, 0, entries.length);
      newEntries[entries.length] = key;
      newEntries[entries.length + 1] = value;

    }

    return new IArrayMap<>(newEntries);

  }

  @Override
  public IMap<K, V> remove(K key) {

    int i = getEntryIndex(key);

    final IMap<K, V> result;

    if (i >= 0) {

      Object[] newEntries;
      newEntries = new Object[entries.length - 2];
      System.arraycopy(entries, 0, newEntries, 0, i);
      System.arraycopy(entries, i + 2, newEntries, i, entries.length - i - 2);
      result = IArrayMap.make(newEntries);

    } else {

      result = this;

    }

    return result;

  }

  @Override
  public int size() {
    return entries.length / 2;
  }

  @Override
  @Nullable
  public V get(K key) {
    int i = getEntryIndex(key);
    if (i >= 0) {
      return (V) entries[i + 1];
    } else {
      return null;
    }
  }

  @Override
  public IList<K> keys() {
    K[] ks = (K[]) new Object[entries.length / 2];
    for (int i = 0; i < ks.length; i++) {
      ks[i] = (K) entries[i*2];
    }
    return IArrayList.make(ks);
  }

  @Override
  public IList<V> values() {
    V[] vs = (V[]) new Object[entries.length / 2];
    for (int i = 0; i < vs.length; i++) {
      vs[i] = (V) entries[i*2 + 1];
    }
    return IArrayList.make(vs);
  }

  @Override
  public Iterator<IEntry<K, V>> iterator() {
    return new Iterator<>() {

      public int currentIndex = 0;

      @Override
      public boolean hasNext() {
        return currentIndex < entries.length;
      }

      @Override
      public IEntry<K, V> next() {
        if (hasNext()) {
          IEntry<K, V> entry = IEntry.make((K) entries[currentIndex], (V) entries[currentIndex + 1]);
          currentIndex += 2;
          return entry;
        } else {
          throw new NoSuchElementException();
        }
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IArrayMap<?, ?> iArrayMap = (IArrayMap<?, ?>) o;
    return Arrays.equals(entries, iArrayMap.entries);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(entries);
  }

  @Override
  public String toString() {
    return "IArrayMap{" +
      "entries=" + Arrays.toString(entries) +
      '}';
  }

}

