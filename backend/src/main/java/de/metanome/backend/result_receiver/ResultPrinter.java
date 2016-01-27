/*
 * Copyright 2014 by the Metanome project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.metanome.backend.result_receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.metanome.algorithm_integration.result_receiver.ColumnNameMismatchException;
import de.metanome.algorithm_integration.result_receiver.CouldNotReceiveResultException;
import de.metanome.algorithm_integration.results.*;
import de.metanome.backend.results_db.ResultType;

import java.io.*;
import java.util.*;

/**
 * Writes all received Results to disk. When all results were received, the results are read again
 * and returned.
 */
public class ResultPrinter extends ResultReceiver {

  protected static final String TABLE_MARKER = "# TABLES";
  protected static final String COLUMN_MARKER = "# COLUMN";
  protected static final String RESULT_MARKER = "# RESULTS";

  protected EnumMap<ResultType, PrintStream> openStreams;
  protected EnumMap<ResultType, Boolean> headerWritten;
  protected Map<String, String> columnMapping;
  protected Map<String, String> tableMapping;

  public ResultPrinter(String algorithmExecutionIdentifier, List<String> acceptedColumns)
    throws FileNotFoundException {
    super(algorithmExecutionIdentifier, acceptedColumns);
    this.headerWritten = new EnumMap<>(ResultType.class);
    this.openStreams = new EnumMap<>(ResultType.class);
    this.columnMapping = new HashMap<>();
    this.tableMapping = new HashMap<>();

    this.initializeMappings();
  }

  protected ResultPrinter(String algorithmExecutionIdentifier, List<String> acceptedColumns, Boolean test)
    throws FileNotFoundException {
    super(algorithmExecutionIdentifier, acceptedColumns, test);
    this.headerWritten = new EnumMap<>(ResultType.class);
    this.openStreams = new EnumMap<>(ResultType.class);
    this.columnMapping = new HashMap<>();
    this.tableMapping = new HashMap<>();

    this.initializeMappings();
  }

  @Override
  public void receiveResult(BasicStatistic statistic)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(statistic)) {
      try {
        JsonConverter<BasicStatistic> jsonConverter = new JsonConverter<>();
        getStream(ResultType.STAT).println(jsonConverter.toJsonString(statistic));
      } catch (JsonProcessingException e) {
        throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  @Override
  public void receiveResult(FunctionalDependency functionalDependency)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(functionalDependency)) {
      if (this.acceptedColumns != null) {
        // write a customize string
        if (!getHeaderWritten(ResultType.FD)) {
          this.writeHeader(ResultType.FD);
        }
        String str = functionalDependency.toString(this.tableMapping, this.columnMapping);
        getStream(ResultType.FD).println(str);
      } else {
        // write JSON to file
        // the acceptableColumnNames are null, that means a database connection was used
        // we do not know which columns are in the result
        try {
          JsonConverter<FunctionalDependency> jsonConverter = new JsonConverter<>();
          getStream(ResultType.FD).println(jsonConverter.toJsonString(functionalDependency));
        } catch (JsonProcessingException e) {
          throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
        }
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  @Override
  public void receiveResult(InclusionDependency inclusionDependency)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(inclusionDependency)) {
      try {
        JsonConverter<InclusionDependency> jsonConverter = new JsonConverter<>();
        getStream(ResultType.IND).println(jsonConverter.toJsonString(inclusionDependency));
      } catch (JsonProcessingException e) {
        throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  @Override
  public void receiveResult(UniqueColumnCombination uniqueColumnCombination)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(uniqueColumnCombination)) {
      try {
        JsonConverter<UniqueColumnCombination> jsonConverter = new JsonConverter<>();
        getStream(ResultType.UCC).println(jsonConverter.toJsonString(uniqueColumnCombination));
      } catch (JsonProcessingException e) {
        throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  @Override
  public void receiveResult(ConditionalUniqueColumnCombination conditionalUniqueColumnCombination)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(conditionalUniqueColumnCombination)) {
      try {
        JsonConverter<ConditionalUniqueColumnCombination> jsonConverter = new JsonConverter<>();
        getStream(ResultType.CUCC)
          .println(jsonConverter.toJsonString(conditionalUniqueColumnCombination));
      } catch (JsonProcessingException e) {
        throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  @Override
  public void receiveResult(OrderDependency orderDependency)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    if (this.acceptedResult(orderDependency)) {
      try {
        JsonConverter<OrderDependency> jsonConverter = new JsonConverter<>();
        getStream(ResultType.OD).println(jsonConverter.toJsonString(orderDependency));
      } catch (JsonProcessingException e) {
        throw new CouldNotReceiveResultException("Could not convert the result to JSON!");
      }
    } else {
      throw new ColumnNameMismatchException("The table/column name does not match the names in the input!");
    }
  }

  protected PrintStream getStream(ResultType type) throws CouldNotReceiveResultException {
    if (!openStreams.containsKey(type)) {
      openStreams.put(type, openStream(type.getEnding()));
    }
    return openStreams.get(type);
  }

  protected PrintStream openStream(String fileSuffix) throws CouldNotReceiveResultException {
    try {
      return new PrintStream(new FileOutputStream(getOutputFilePathPrefix() + fileSuffix), true);
    } catch (FileNotFoundException e) {
      throw new CouldNotReceiveResultException("Could not open result file for writing", e);
    }
  }

  protected Boolean getHeaderWritten(ResultType type) throws CouldNotReceiveResultException {
    if (!this.headerWritten.containsKey(type)) {
      this.headerWritten.put(type, false);
    }
    return this.headerWritten.get(type);
  }

  private void writeHeader(ResultType resultType) throws CouldNotReceiveResultException {
    PrintStream stream = getStream(resultType);

    stream.println(TABLE_MARKER);
    for (Map.Entry<String, String> entry : this.tableMapping.entrySet()) {
      stream.println(entry.getKey() + '\t' + entry.getValue());
    }

    stream.println(COLUMN_MARKER);
    for (Map.Entry<String, String> entry : this.columnMapping.entrySet()) {
      stream.println(entry.getKey() + '\t' + entry.getValue());
    }

    stream.println(RESULT_MARKER);

    this.headerWritten.put(resultType, true);
  }

  @Override
  public void close() throws IOException {
    for (PrintStream stream : openStreams.values()) {
      stream.close();
    }
  }

  /**
   * Reads the results from disk and returns them.
   *
   * @return all results
   * @throws java.io.IOException if file could not be read
   */
  public List<Result> getResults() throws IOException {
    List<Result> results = new ArrayList<>();

    for (ResultType type : openStreams.keySet()) {
      if (existsFile(type.getEnding())) {
        String fileName = getOutputFilePathPrefix() + type.getEnding();
        results.addAll(ResultReader.readResultsFromFile(fileName, type.getName()));
      }
    }

    return results;
  }

  private Boolean existsFile(String fileSuffix) {
    return new File(getOutputFilePathPrefix() + fileSuffix).exists();
  }

  private void initializeMappings() throws IndexOutOfBoundsException {
    int tableCounter = 1;
    int columnCounter = 1;

    for (String name : this.acceptedColumns) {
      String[] parts = name.split("\t");
      String tableName = parts[0];
      String columnName = parts[1];

      if (!this.tableMapping.containsKey(tableName)) {
        this.tableMapping.put(tableName, String.valueOf(tableCounter));
        tableCounter++;
      }

      String tableValue = this.tableMapping.get(tableName);
      columnName = tableValue + "." + columnName;

      if (!this.columnMapping.containsKey(columnName)) {
        this.columnMapping.put(columnName, String.valueOf(columnCounter));
        columnCounter++;
      }
    }
  }

}
