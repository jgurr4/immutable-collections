package com.ple.util;

@Immutable
public interface IList<T> extends Iterable<T> {

  T[] toArray();

  IList<T> addAll(IList<T> list);

  IList<T> add(T t);

  T get(int i);

  IList<T> remove(T t);

  boolean contains(T t);

  int size();

}
