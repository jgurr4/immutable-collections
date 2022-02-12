package com.ple.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

@Immutable
public class IHashMap<K, V> implements IMap<K, V> {

  static public final IHashMap empty = new IHashMap(new IEntry[0], 5, 0, 6, 2);

  public static <K, V> IHashMap<K, V> from(Object... keyCommaValue) {

    if (keyCommaValue.length % 2 != 0) {
      throw new IllegalArgumentException("IHashMap.from must have an even number of parameters");
    }

    final int entriesInUse = keyCommaValue.length / 2;
    final int bucketSize = 5;
    final int entryCount = (int) (keyCommaValue.length / .3f);
    final int bucketCount = entryCount / bucketSize;
    final IEntry<K, V>[] entries = new IEntry[entryCount];
    for (int i = 0; i < keyCommaValue.length - 1; i += 2) {
      final K key = (K) keyCommaValue[i];
      final V value = (V) keyCommaValue[i + 1];
      final int hashCode = Math.abs(key.hashCode());
      final int bucketIndex = hashCode % bucketCount;
      int c = 0;
      int entryIndex = bucketIndex * bucketSize;
      while (c < entries.length) {
        if (entries[entryIndex] == null) {
          entries[entryIndex] = IEntry.from(key, value);
          break;
        }
        c++;
        entryIndex++;
        if (entryIndex >= entries.length) {
          entryIndex = 0;
        }
      }
    }
    final IHashMap<K, V> map = new IHashMap<>(entries, bucketSize, entriesInUse, bucketCount, 2);
    return map;
  }

  private final IEntry<K, V>[] entries;
  private final int bucketSize;
  private final float threshold = 0.3f;
  private final int entriesInUse;
  private final int bucketCount;
  private final int expansionFactor;

  private IHashMap(IEntry<K, V>[] entries, int bucketSize, int entriesInUse, int bucketCount, int expansionFactor) {

    this.entries = entries;
    this.bucketSize = bucketSize;
    this.entriesInUse = entriesInUse;
    this.bucketCount = bucketCount;
    this.expansionFactor = expansionFactor;

  }

  @Override
  public IMap<K, V> putAll(Iterable<IEntry<K, V>> entriesToAdd) {

    ArrayList<IEntry<K, V>> list = new ArrayList<>();
    for (IEntry<K, V> next : entriesToAdd) {
      list.add(next);
    }

    return putAll(list.toArray());

  }

  @Override
  public IHashMap<K, V> putAll(Object... keyOrValue) {

    int c = 0;
    assert keyOrValue.length % 2 == 0;
    IEntry<K, V>[] newEntries = new IEntry[entries.length];
    int newBucketCount = bucketCount;
    int newEntriesInUse = keyOrValue.length / 2 + entriesInUse;
    if (newEntriesInUse > (int) (entries.length * threshold)) {
      final int entryCount = (int) (newEntriesInUse * 2 / threshold);
      newBucketCount = entryCount / bucketSize;
      newEntries = new IEntry[entryCount];
      rehash(entries, newEntries, newBucketCount, bucketSize);
    } else {
      while (c < entries.length) {
        newEntries[c] = entries[c];
        c++;
      }
      c = 0;
    }
    for (int i = 0; i < keyOrValue.length - 1; i += 2) {
      final K key = (K) keyOrValue[i];
      final V value = (V) keyOrValue[i + 1];
      final int hashCode = Math.abs(key.hashCode());
      final int bucketIndex = hashCode % newBucketCount;
      int entryIndex = bucketIndex * bucketSize;
      while (c < newEntries.length) {
        if (newEntries[entryIndex] == null) {
          newEntries[entryIndex] = IEntry.from(key, value);
          break;
        }
        c++;
        entryIndex++;
        if (entryIndex >= newEntries.length) {
          entryIndex = 0;
        }
      }
    }
    final IHashMap<K, V> map = new IHashMap<>(newEntries, bucketSize, newEntriesInUse, newBucketCount, 2);
    return map;

  }

  /**
   * Avoid using .put() in a loop of adding multiple values. This is because it will force Java to
   * recreate the IHashMap object for each loop/entry. Instead, you should use .putAll() method since that
   * will only recreate the IHashMap one time no matter how many entries you put in.
   *
   */
  @Override
  public IHashMap<K, V> put(K key, V value) {
    int newBucketCount = bucketCount;
    IEntry<K, V>[] newEntries;
    if (entries.length == 0) {
      newEntries = new IEntry[bucketSize * 10];
    } else {
      newEntries = new IEntry[entries.length];
    }
    int c = 0;
    // Copy values from old entries[] to new entries[].
    while (c < entries.length) {
      newEntries[c] = entries[c];
      c++;
    }
    c = 0;
    final int hashCode = Math.abs(key.hashCode());
    final int bucketIndex = hashCode % newBucketCount;
    int entryIndex = bucketIndex * bucketSize;
    int newEntriesInUse = entriesInUse;
    // Add new value to newEntries[] and resize bucket if necessary.
    while (c < newEntries.length) {
      if (newEntries[entryIndex] == null) {
        newEntries[entryIndex] = IEntry.from(key, value);
        newEntriesInUse += 1;
        break;
      }
      c++;
      entryIndex++;
      if (entryIndex >= newEntries.length) {
        entryIndex = 0;
      }
    }
    if (entriesInUse > newEntries.length * threshold) {
      final int entryCount = getEntryCount();
      newBucketCount = entryCount / bucketSize;
      newEntries = new IEntry[entryCount];
    }
    final IHashMap<K, V> map = new IHashMap<>(newEntries, bucketSize, newEntriesInUse, newBucketCount, 2);
    return map;
  }

  @Override
  public IMap<K, V> remove(K key) {
    return null;
  }

  private int getEntryCount() {
    return (int) (entriesInUse * expansionFactor / threshold);
  }

  @Override
  public int size() {
    return entriesInUse;
  }

  @Override
  public V get(K key) {
    if (entriesInUse == 0 || !keys().contains(key)) {
      return null;
    }
    final int hashCode = Math.abs(key.hashCode());
    final int bucketIndex = hashCode % bucketCount;
    int entryIndex = bucketIndex * bucketSize;
    int c = 0;
    while (c < entries.length) {
      if (entries[entryIndex] == null) {
        return null;
      } else if (entries[entryIndex].key == key) {
        return entries[entryIndex].value;
      }
      c++;
      entryIndex++;
    }
    return null;
  }

  @Override
  public IList<K> keys() {
    //TODO: Consider replacing this with AbstractSet with implementation methods.
    ArrayList<K> ks = new ArrayList<>();
    for (IEntry<K, V> entry : entries) {
      if (entry == null) {
        continue;
      }
      ks.add(entry.key);
    }
    return (IList<K>) IArrayList.make(ks.toArray());
  }

  @Override
  public IList<V> values() {
    final ArrayList<V> list = new ArrayList<>();
    for (IEntry<K, V> entry : entries) {
      if (entry == null) {
        continue;
      }
      list.add(entry.value);
    }
    return (IList<V>) IArrayList.make(list.toArray());
  }

  public IHashMap<K, V> setBucketSize(int newBucketSize) {

    final IEntry<K, V>[] newEntries = new IEntry[entries.length];
    rehash(entries, newEntries, bucketCount, newBucketSize);
    return new IHashMap(newEntries, newBucketSize, entriesInUse, bucketCount, 2);

  }

  public IHashMap<K, V> setBucketCount(int newBucketCount) {

    final IEntry<K, V>[] newEntries = new IEntry[newBucketCount * bucketSize];
    rehash(entries, newEntries, newBucketCount, bucketSize);
    return new IHashMap(newEntries, bucketSize, entriesInUse, newBucketCount, 2);
  }

  private void rehash(IEntry<K, V>[] entries, IEntry<K, V>[] newEntries, int newBucketCount, int newBucketSize) {

    for (int i = 0; i < entries.length; i++) {
      if (entries[i] != null) {
        IEntry<K, V> entry = entries[i];
        final int newBucketIndex = Math.abs(entry.key.hashCode()) % newBucketCount * newBucketSize;
        for (int k = newBucketIndex; k < newEntries.length; k++) {
          if (k > newEntries.length) {
            k = 0;
          }
          if (newEntries[k] == null) {
            newEntries[k] = entry;
            break;
          }
        }
      }
    }

  }

  public int getBucketCount() {
    return bucketCount;
  }

  public int getBucketSize() {
    return bucketSize;
  }

  public IHashMap<K, V> setThreshold(double v) {

    IEntry<K, V>[] newEntries = entries;
    int newBucketCount = bucketCount;
    if (entriesInUse > entries.length * threshold) {
      newEntries = new IEntry[entries.length];
      newBucketCount = getEntryCount() / bucketSize;
      rehash(entries, newEntries, newBucketCount, bucketSize);
    }
    return new IHashMap(newEntries, bucketSize, entriesInUse, newBucketCount, expansionFactor);
  }

  @NotNull
  @Override
  public Iterator<IEntry<K, V>> iterator() {
    return new Iterator<>() {

      public int currentIndex;

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
    if (!(o instanceof IHashMap)) return false;
    IHashMap<?, ?> iHashMap = (IHashMap<?, ?>) o;
    return getBucketSize() == iHashMap.getBucketSize() && Float.compare(iHashMap.threshold, threshold) == 0 && entriesInUse == iHashMap.entriesInUse && getBucketCount() == iHashMap.getBucketCount() && expansionFactor == iHashMap.expansionFactor && Arrays.equals(entries, iHashMap.entries);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getBucketSize(), threshold, entriesInUse, getBucketCount(), expansionFactor);
    result = 31 * result + Arrays.hashCode(entries);
    return result;
  }

  @Override
  public String toString() {
    return "IHashMap{" +
      "entries=" + Arrays.toString(entries) +
      ", bucketSize=" + bucketSize +
      ", threshold=" + threshold +
      ", entriesInUse=" + entriesInUse +
      ", bucketCount=" + bucketCount +
      ", expansionFactor=" + expansionFactor +
      '}';
  }

}

