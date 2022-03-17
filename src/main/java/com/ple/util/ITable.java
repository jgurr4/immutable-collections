package com.ple.util;

import java.util.Locale;

@Immutable
public class ITable {
  public static ITable empty = ITable.make(IArrayList.empty, new Object[0]);
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
      if (columnNames.get(j).toLowerCase(Locale.ROOT).equals(columnName.toLowerCase(Locale.ROOT))) {
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

  public Object[] getRow(int rowIndex) {
    final Object[] row = new Object[columnNames.size()];
    final int destPos = 0;
    final int srcPos = rowIndex * columnNames.size();
    System.arraycopy(values, srcPos, row, destPos, columnNames.size());
    return row;
  }

  public Object get(int rowIndex, int columnIndex) {
    return values[(rowIndex * columnNames.size()) + columnIndex];
  }

  @Override
  public String toString() {
    if (values.length == 0) {
      return "ITable\n";
    }
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
