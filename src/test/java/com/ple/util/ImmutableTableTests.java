package com.ple.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableTableTests {

  @Test
  public void testToString() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {"john", 33, LocalDate.of(2022,03,11)};
    final ITable userTable = ITable.make(colNames, objects);
    System.out.println(userTable);
    assertEquals("""
        ITable
        name	age	date_added
        john	33	2022-03-11
        """, userTable.toString());
  }

  @Test
  public void testGetColumn() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {"john", 33, LocalDate.now(), "bob", 42, LocalDate.of(2021, 11, 23)};
    final ITable userTable = ITable.make(colNames, objects);
    final Object[] ages = userTable.getColumn("age");
    assertArrayEquals(new Object[]{33, 42}, ages);
  }

  @Test
  public void testGetColumnByIndex() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {"john", 33, LocalDate.now(), "bob", 42, LocalDate.of(2021, 11, 23)};
    final ITable userTable = ITable.make(colNames, objects);
    final Object[] ages = userTable.getColumn(0);
    assertArrayEquals(new Object[]{"john", "bob"}, ages);
  }
}
