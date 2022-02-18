package com.ple.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

@Immutable
public class IHashMap<K, V> implements IMap<K, V> {

  static public final IHashMap empty = new IHashMap(new Object[0], 0, 0.3);

  public static <K, V> IHashMap<K, V> make(Object... keyCommaValue) {
    if (keyCommaValue.length % 2 != 0) {
      throw new IllegalArgumentException("IHashMap.make must have an even number of parameters");
    }
    if (keyCommaValue.length == 0) return empty;
    return empty.putAll(keyCommaValue);
  }

  /**
   * length of this array must be an even number, since it is full of key/value pairs
   */
  private final Object[] table;
  private final int entriesInUse;
  private final double density;

  private IHashMap(Object[] table, int entriesInUse, double density) {
    this.table = table;
    this.entriesInUse = entriesInUse;
    this.density = density;
  }

  @Override
  public IMap<K, V> putAll(Iterable<IEntry<K, V>> entriesToAdd) {

    ArrayList<Object> list = new ArrayList<>();
    for (IEntry<K, V> next : entriesToAdd) {
      list.add(next.key);
      list.add(next.value);
    }

    return putAll(list.toArray());

  }

  @Override
  public IHashMap<K, V> putAll(Object... keyCommaValue) {

    if (keyCommaValue.length % 2 != 0) {
      throw new IllegalArgumentException("IHashMap.putAll must have an even number of parameters");
    }

    final int newEntriesToAdd = keyCommaValue.length / 2;
    final double preTableSize = (newEntriesToAdd + entriesInUse) / density;
    final int tableSize = (int) (Math.pow(2, 1 + Math.ceil(Math.log(preTableSize) / Math.log(2))));
    int totalEntries = entriesInUse;
    boolean changed = false;
    Object[] table = new Object[tableSize];

    if (tableSize != this.table.length) {
      changed = true;
      totalEntries = rehash(this.table, table);
    } else {
      System.arraycopy(this.table, 0, table, 0, tableSize);
    }

    for (int i = 0; i < keyCommaValue.length; i += 2) {

      final K key = (K) keyCommaValue[i];
      final V value = (V) keyCommaValue[i + 1];

      ChangeType change = putIntoTable(key, value, table);
      if (change == ChangeType.create) totalEntries++;
      if (change == ChangeType.create || change == ChangeType.update) changed = true;

    }

    final IHashMap<K, V> map;
    if (changed) {
      map = new IHashMap<K, V>(table, totalEntries, density);
    } else {
      map = this;
    }

    return map;

  }

  private ChangeType putIntoTable(K key, V value, Object[] table) {

    final int bucketIndex = getExpectedBucketIndex(key, table);
    ChangeType change = ChangeType.none;

    int bucketIndexForKey = findBucketIndexForKey(key, bucketIndex, table);

    if (bucketIndexForKey == -1) {
      bucketIndexForKey = findNextEmptyBucket(bucketIndex, table);
      if (bucketIndexForKey == -1) {
        throw new InternalError("FATAL: Bug in IHashMap.putAll: could not find empty bucket for new key");
      }
      table[bucketIndexForKey] = key;
      change = ChangeType.create;
    }

    if (!Objects.equals(table[bucketIndexForKey + 1], value)) {
      table[bucketIndexForKey + 1] = value;
      if (change == ChangeType.none) change = ChangeType.update;
    }

    return change;

  }

  private int getExpectedBucketIndex(K key, Object[] table) {
    if (table.length < 2) return 0;
    final int hashCode = Math.abs(key.hashCode());
    final int bucketCount = table.length / 2;
    final int bucketIndex = hashCode % bucketCount * 2;
    return bucketIndex;
  }

  private int findNextEmptyBucket(int startingIndex, Object[] table) {
    assert startingIndex % 2 == 0;
    int foundIndex = -1;
    for (int i = 0; i < table.length; i += 2) {
      int adjustedIndex = (i + startingIndex) % table.length;
      if(table[adjustedIndex] == null) {
        foundIndex = adjustedIndex;
        break;
      }
    }
    return foundIndex;
  }

  private int findBucketIndexForKey(K key, int startingIndex, Object[] table) {
    assert startingIndex % 2 == 0;
    int foundIndex = -1;
    for (int i = 0; i < table.length; i += 2) {
      int adjustedIndex = (i + startingIndex) % table.length;
      K k = (K) table[adjustedIndex];
      if (Objects.equals(k, key)) {
        foundIndex = adjustedIndex;
        break;
      }
    }
    return foundIndex;
  }

  /**
   * Avoid using .put() in a loop of adding multiple values. This is because it will force Java to
   * recreate the IHashMap object for each loop/entry. Instead, you should use .putAll() method since that
   * will only recreate the IHashMap one time no matter how many entries you put in.
   *
   */
  @Override
  public IHashMap<K, V> put(K key, V value) {
    return putAll(key, value);
  }

  @Override
  public IMap<K, V> remove(K key) {
    throw new InternalError("Not implemented yet");
  }

  @Override
  public int size() {
    return entriesInUse;
  }

  @Override
  public V get(K key) {
    int expectedBucketIndex = getExpectedBucketIndex(key, table);
    int index = findBucketIndexForKey(key, expectedBucketIndex, this.table);
    if (index == -1) {
      return null;
    } else {
      return (V) table[index + 1];
    }
  }

  @Override
  public IList<K> keys() {
    ArrayList<K> ks = new ArrayList<>();
    for (int i = 0; i < table.length; i += 2) {
      K key = (K) table[i];
      if (key == null) {
        continue;
      }
      ks.add(key);
    }
    return (IList<K>) IArrayList.make(ks.toArray());
  }

  @Override
  public IList<V> values() {
    ArrayList<V> vs = new ArrayList<>();
    for (int i = 0; i < table.length; i += 2) {
      V value = (V) table[i + 1];
      if (value == null) {
        continue;
      }
      vs.add(value);
    }
    return (IList<V>) IArrayList.make(vs.toArray());
  }

  public int getBucketCount() {
    return table.length / 2;
  }

  public IHashMap<K, V> setBucketCount(int newBucketCount) {
    final Object[] newTable = new Object[newBucketCount * 2];
    int entryCount = rehash(table, newTable);
    return new IHashMap(newTable, entryCount, density);
  }

  private int rehash(Object[] oldTable, Object[] newTable) {
    int totalEntries = 0;
    for (int i = 0; i < oldTable.length; i += 2) {
      K key = (K) oldTable[i];
      V value = (V) oldTable[i + 1];
      if (key != null) {
        ChangeType change = putIntoTable(key, value, newTable);
        if (change == ChangeType.create) totalEntries++;
      }
    }
    return totalEntries;
  }

  public IHashMap<K, V> setDensity(double density) {
    return new IHashMap<>(table, entriesInUse, density);
  }

  @NotNull
  @Override
  public Iterator<IEntry<K, V>> iterator() {
    return new Iterator<>() {

      public int currentIndex;

      @Override
      public boolean hasNext() {
        return currentIndex < table.length;
      }

      @Override
      public IEntry<K, V> next() {
        if (hasNext()) {
          currentIndex += 2;
          return IEntry.make((K)table[currentIndex], (V)table[currentIndex + 1]);
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
    IHashMap<?, ?> iHashMap = (IHashMap<?, ?>) o;
    return entriesInUse == iHashMap.entriesInUse && Double.compare(iHashMap.density, density) == 0 && Arrays.equals(table, iHashMap.table);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(entriesInUse, density);
    result = 31 * result + Arrays.hashCode(table);
    return result;
  }

  @Override
  public String toString() {
    return "IHashMap{" +
      "table=" + Arrays.toString(table) +
      ", entriesInUse=" + entriesInUse +
      ", density=" + density +
      '}';
  }

}

