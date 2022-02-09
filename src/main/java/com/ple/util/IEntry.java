package com.ple.util;

@Immutable
public interface IEntry<K, V> {

  K getKey();

  V getValue();

}
