package com.ple.util;

import java.util.Arrays;

@Immutable
public class ITable {
  public final IList<String> columnNames;
  public final Object[] values;

  public ITable(IList<String> columnNames, Object[] values) {
    this.columnNames = columnNames;
    this.values = values;
  }

  public static ITable make(IList<String> columnNames, Object[] values) {
    return new ITable(columnNames, values);
  }

  // Returns an array of all the objects inside a specified column. For example `name` column may be the 3rd column.
  // So getColumn("name") will return all the values of get(n, 2) which would be range of 0-numRows.
  // So (0,2), (1,2), (2,2) etc...
  public Object[] getColumn(String columnName) {
    return null;
  }

  public Object[] getColumn(int columnIndex) {
    return null;
  }
  //This returns the next row of resultList.values. Which is a Object[]. Row 1 starts at 0. If there are 5 columns row 2 starts at 5.
  // The user specifies rowNum, then result is Object[5]{rowNum*5 + rowNum*5 + 5}
  public Object[] getRow(int rowIndex) {
    return null;
  }

  // This returns a specific element from a specific row. get(0, 0) will return the first value, get(2,3) will return the 4th column value of the 3rd row.
  public Object get(int rowIndex, int columnIndex) {
    return null;
  }

  @Override
  public String toString() {
    String string = "ITable\n";
    int j = 0;
    String separator = "";
    while (j < columnNames.size()) {
      string += separator + columnNames.get(j);
      j++;
      separator = "\t";
    }
    separator = "";
    string += "\n";
    j = 0;
    for (int i = 0; i < values.length; i++) {
      while (j < columnNames.size()) {
        string += separator + values[i];
        separator = "\t";
        i++;
        j++;
      }
      separator = "";
      string += "\n";
    }
    return string;
  }
}
