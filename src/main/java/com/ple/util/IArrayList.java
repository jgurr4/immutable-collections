package com.ple.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Immutable
public class IArrayList<V> implements IList<V> {

  /** can't be parameterized because it may be used as an empty list of many different types */
  public static final IArrayList empty = new IArrayList(new Object[0]);

  public static <V> IArrayList<V> make(V... values) {
    return new IArrayList<>(values);
  }

  public static <V> IArrayList<V> make(List<V> list) {
    IList<V> newList = IArrayList.empty;
    for (V v : list) {
      newList = newList.add(v);
    }
    return (IArrayList<V>) newList;
  }

  public final V[] values;

  private IArrayList(V[] values) {
    this.values = values;
  }

  @Override
  public V[] toArray() {
    return values;
  }

  @Override
  public V[] toArray(V[] a) {
    if (a.length < values.length)
      // Make a new array of a's runtime type, but my contents:
      return (V[]) Arrays.copyOf(values, values.length, a.getClass());
    System.arraycopy(values, 0, a, 0, values.length);
    if (a.length > values.length)
      a[values.length] = null;
    return a;
  }

  @Override
  public IList<V> addAll(IList<V> list) {
    V[] result = Arrays.copyOf(this.values, this.values.length + list.toArray().length);
    for (int i = 0; i < list.toArray().length; i++) {
      result[result.length - list.toArray().length + i] = list.toArray()[i];
    }
    return IArrayList.make(result);
  }

  @Override
  public IList<V> add(V v) {
    V[] result = Arrays.copyOf(values, values.length + 1);
    result[values.length] = v;
    return IArrayList.make(result);
  }

  @Override
  public V get(int i) {
    return values[i];
  }

  @Override
  public IList<V> remove(V v) {
    if (!contains(v)) {
      return this;
    }
    V[] result = Arrays.copyOf(values, values.length - 1);
    for (int i = 0; i < values.length - 1; i++) {
      if (!values[i].equals(v)) {
        result[i] = values[i];
      } else {
        while (i < values.length - 1) {
          result[i] = values[i+1];
          i++;
        }
      }
    }
    return IArrayList.make(result);
  }

  @Override
  public boolean contains(V v) {
    for (V value : values) {
      if (value.equals(v)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int size() {
    return values.length;
  }

  @Override
  public Iterator<V> iterator() {
    return new Iterator<>() {
      private int currentIndex = 0;

      @Override
      public boolean hasNext() {
        return currentIndex < values.length && values[currentIndex] != null;
      }

      @Override
      public V next() {
        return values[currentIndex++];
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IArrayList)) return false;
    IArrayList<?> that = (IArrayList<?>) o;
    return Arrays.equals(values, that.values);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(values);
  }

  @Override
  public String toString() {
    return "IArrayList{" +
      "values=" + Arrays.toString(values) +
      '}';
  }

}
