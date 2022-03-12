package com.ple.util;

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

  public Object[] getColumn(String columnName) {
    int columnIndex = 0;
    for (int j = 0; j < columnNames.size(); j++) {
      if (columnNames.get(j).equals(columnName)) {
        columnIndex = j;
      }
    }
    return getColumn(columnIndex);
  }

  public Object[] getColumn(int columnIndex) {
    Object[] columnValues = new Object[values.length / columnNames.size()];
    int j = 0;
    for (int i = 0; i < values.length; i++) {
      if (i == columnIndex + j*columnNames.size()) {
        columnValues[j] = values[i];
        j++;
      }
    }
    return columnValues;
  }
  //This returns the next row of resultList.values. Which is a Object[]. Row 1 starts at 0. If there are 5 columns row 2 starts at 5.
  // The user specifies rowNum, then result is Object[5]{rowNum*5 + rowNum*5 + 5}
  public Object[] getRow(int rowIndex) {
    return null;
  }

  // This returns a specific element from a specific row. get(0, 0) will return the first value, get(2,3) will return the 4th column value of the 3rd row.
  public Object get(int rowIndex, int columnIndex) {
    final int i = (rowIndex * columnNames.size()) + columnIndex;
    return values[i];
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
    int a = 0;
    for (int i = 0; i < values.length / columnNames.size(); i++) {
      a = i * columnNames.size();
      while (j < columnNames.size()) {
        string += separator + values[a];
        separator = "\t";
        a++;
        j++;
      }
      j = 0;
      separator = "";
      string += "\n";
    }
    return string;
  }
}
