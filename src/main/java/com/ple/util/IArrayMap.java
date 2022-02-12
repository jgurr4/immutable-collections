package com.ple.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

@Immutable
public class IArrayMap<K, V> implements IMap<K, V> {

  public static final IArrayMap empty = new IArrayMap(new IEntry[0]);

  public static <K, V> IArrayMap<K, V> make(Object... keyCommaValue) {

    assert keyCommaValue.length % 2 == 0;
    final int entriesInUse = keyCommaValue.length / 2;
    final IEntry[] entries = new IEntry[entriesInUse];

    for (int i = 0; i < keyCommaValue.length - 1; i += 2) {
      final K key = (K) keyCommaValue[i];
      final V value = (V) keyCommaValue[i + 1];
      entries[i >> 1] = IEntry.from(key, value);
    }

    final IArrayMap<K, V> map = new IArrayMap<>(entries);
    return map;

  }

  private final IEntry<K, V>[] entries;

  private IArrayMap(IEntry<K, V>[] entries) {
    this.entries = entries;
  }

  @Override
  public IArrayMap<K, V> putAll(Iterable<IEntry<K, V>> entriesToAdd) {

    final IEntry<K, V>[] oldEntries = new IEntry[entries.length];
    System.arraycopy(entries, 0, oldEntries, 0, entries.length);
    final List<IEntry<K, V>> newEntries = new ArrayList<>();

    for (IEntry<K, V> entry : entriesToAdd) {
      int i = getEntryIndex(entry.key);
      if (i >= 0) {
        oldEntries[i] = entry;
      } else {
        newEntries.add(entry);
      }
    }

    final IEntry<K, V>[] finalEntries = new IEntry[entries.length + newEntries.size()];
    System.arraycopy(oldEntries, 0, finalEntries, 0, oldEntries.length);
    System.arraycopy(newEntries.toArray(), 0, finalEntries, oldEntries.length, newEntries.size());
    return new IArrayMap<>(finalEntries);

  }

  private int getEntryIndex(K key) {
    for (int i = 0; i < entries.length; i++) {
      IEntry<K, V> entry = entries[i];
      if (Objects.equals(entry.key, key)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public IArrayMap<K, V> putAll(Object... keyCommaValue) {

    if (keyCommaValue.length % 2 != 0) {
      throw new IllegalArgumentException("IArrayMap.putAll must have an even number of parameters");
    }

    final int entryCount = keyCommaValue.length >> 1;
    final IEntry<K, V>[] newEntries = new IEntry[entryCount];

    for (int i = 0; i < keyCommaValue.length - 1; i += 2) {
      final K key = (K) keyCommaValue[i];
      final V value = (V) keyCommaValue[i + 1];
      entries[i >> 1] = IEntry.from(key, value);
    }

    return putAll((Object[])newEntries);

  }

  /**
   * Avoid using .put() in a loop of adding multiple values. This is because it will force Java to
   * recreate the IArrayMap object for each loop/entry. Instead, you should use .putAll() method since that
   * will only recreate the IArrayMap one time no matter how many entries you put in.
   */
  @Override
  public IArrayMap<K, V> put(K key, V value) {

    final IEntry<K, V>[] oldEntries = new IEntry[entries.length];
    System.arraycopy(entries, 0, oldEntries, 0, entries.length);
    final List<IEntry<K, V>> newEntries = new ArrayList<>();

    int i = getEntryIndex(key);
    final IEntry entry = IEntry.from(key, value);
    if (i >= 0) {
      oldEntries[i] = entry;
    } else {
      newEntries.add(entry);
    }

    final IEntry<K, V>[] finalEntries = new IEntry[entries.length + newEntries.size()];
    System.arraycopy(oldEntries, 0, finalEntries, 0, oldEntries.length);
    System.arraycopy(newEntries.toArray(), 0, finalEntries, oldEntries.length, newEntries.size());
    return new IArrayMap<>(finalEntries);

  }

  @Override
  public IMap<K, V> remove(K key) {
    return null;
  }

  @Override
  public int size() {
    return entries.length;
  }

  @Override
  @Nullable
  public V get(K key) {
    int i = getEntryIndex(key);
    if (i >= 0) {
      return entries[i].value;
    } else {
      return null;
    }
  }

  @Override
  public IList<K> keys() {
    K[] ks = (K[]) new Object[entries.length];
    for (int i = 0; i < ks.length; i++) {
      ks[i] = entries[i].key;
    }
    return IArrayList.make(ks);
  }

  @Override
  public IList<V> values() {
    V[] vs = (V[]) new Object[entries.length];
    for (int i = 0; i < vs.length; i++) {
      vs[i] = entries[i].value;
    }
    return IArrayList.make(vs);
  }

  @NotNull
  @Override
  public Iterator<IEntry<K, V>> iterator() {
    return new Iterator<>() {

      public int currentIndex = -1;

      @Override
      public boolean hasNext() {
        return currentIndex < entries.length;
      }

      @Override
      public IEntry<K, V> next() {
        if (hasNext()) {
          currentIndex++;
          return entries[currentIndex];
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

