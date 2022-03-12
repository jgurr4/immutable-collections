package com.ple.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableTableTests {

  @Test
  public void testToString() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {"john", 33, LocalDate.of(2022,03,11), "bob", 42, LocalDate.of(2021, 11, 23)};
    final ITable userTable = ITable.make(colNames, objects);
    assertEquals("""
        ITable
        name	age	date_added
        john	33	2022-03-11
        bob	42	2021-11-23
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

  @Test
  public void testGetCellValue() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {
        "john", 33, LocalDate.now(),
        "bob", 42, LocalDate.of(2021, 11, 23),
        "suzie", 24, LocalDate.of(2003, 12, 3),
        "dalton", 17, LocalDate.of(2010, 4, 13)
    };
    final ITable userTable = ITable.make(colNames, objects);
    final Object bobsDateAdded = userTable.get(1, 2);
    final Object johnsAge = userTable.get(0, 1);
    final Object daltonsDateAdded = userTable.get(3, 2);
    final Object suziesAge = userTable.get(2, 1);
    assertEquals(LocalDate.of(2021,11,23), bobsDateAdded);
    assertEquals(33 , johnsAge);
    assertEquals(LocalDate.of(2010, 4, 13), daltonsDateAdded);
    assertEquals(24, suziesAge);
  }
}
