/*
 * Copyright 2025-2025 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.dbutilities.connectionfunction;

import eu.cdevreeze.dbutilities.ConnectionToJsonObjectFunction;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * {@link ConnectionToJsonObjectFunction} that retrieves metadata of a table's columns in the database.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public final class GetTableColumnsMetaData implements ConnectionToJsonObjectFunction {

    private final String tableName;

    public GetTableColumnsMetaData(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public JsonObject apply(Connection connection) {
        // Unlike Json, JsonProvider does not involve a lookup each time it is used
        JsonProvider jsonProvider = JsonProvider.provider();
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            try (ResultSet rs = dbMetaData.getColumns(null, null, tableName, null)) {
                JsonArrayBuilder columnsJsonArr = jsonProvider.createArrayBuilder();
                while (rs.next()) {
                    columnsJsonArr.add(
                            jsonProvider.createObjectBuilder()
                                    .add(
                                            "tableCat",
                                            Optional.ofNullable(rs.getString("TABLE_CAT"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "tableSchema",
                                            Optional.ofNullable(rs.getString("TABLE_SCHEM"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add("tableName", rs.getString("TABLE_NAME"))
                                    .add("columnName", rs.getString("COLUMN_NAME"))
                                    .add("dataType", rs.getInt("DATA_TYPE"))
                                    .add("typeName", rs.getString("TYPE_NAME"))
                                    .add("columnSize", rs.getInt("COLUMN_SIZE"))
                                    .add("decimalDigits", Optional.ofNullable(rs.getString("DECIMAL_DIGITS"))
                                            .map(Integer::parseInt).map(v -> (JsonValue) jsonProvider.createValue(v)).orElse(JsonValue.NULL))
                                    .add("numPrecRadix", Optional.ofNullable(rs.getString("NUM_PREC_RADIX"))
                                            .map(Integer::parseInt).map(v -> (JsonValue) jsonProvider.createValue(v)).orElse(JsonValue.NULL))
                                    .add("nullable", Optional.ofNullable(rs.getString("NULLABLE"))
                                            .map(Integer::parseInt).map(v -> (JsonValue) jsonProvider.createValue(v)).orElse(JsonValue.NULL))
                                    .add(
                                            "remarks",
                                            Optional.ofNullable(rs.getString("REMARKS"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "columnDef",
                                            Optional.ofNullable(rs.getString("COLUMN_DEF"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add("charOctetLength", Optional.ofNullable(rs.getString("CHAR_OCTET_LENGTH"))
                                            .map(Integer::parseInt).map(v -> (JsonValue) jsonProvider.createValue(v)).orElse(JsonValue.NULL))
                                    .add("ordinalPosition", Optional.ofNullable(rs.getString("ORDINAL_POSITION"))
                                            .map(Integer::parseInt).map(v -> (JsonValue) jsonProvider.createValue(v)).orElse(JsonValue.NULL))
                                    .add("isNullable", rs.getString("IS_NULLABLE"))
                                    .add("isAutoIncrement", rs.getString("IS_AUTOINCREMENT"))
                                    .add("isGeneratedColumn", rs.getString("IS_GENERATEDCOLUMN"))
                    );
                }
                return jsonProvider.createObjectBuilder()
                        .add("table", tableName)
                        .add("columns", columnsJsonArr)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
