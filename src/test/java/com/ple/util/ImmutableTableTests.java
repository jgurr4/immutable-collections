package com.ple.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableTableTests {

  @Test
  public void testToString() {
    final IArrayList<String> colNames = IArrayList.make("name", "age", "date_added");
    final Object[] objects = {"john", 33, LocalDate.now()};
    final ITable userTable = ITable.make(colNames, objects);
    System.out.println(userTable);
    assertEquals("""
        ITable
        name	age	date_added
        john	33	2022-03-11
        """, userTable.toString());
  }
}
