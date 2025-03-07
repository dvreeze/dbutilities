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
 * {@link ConnectionToJsonObjectFunction} that retrieves metadata of a table in the database.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public class GetTableMetaData implements ConnectionToJsonObjectFunction {

    private final String tableName;

    public GetTableMetaData(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public JsonObject apply(Connection connection) {
        // Unlike Json, JsonProvider does not involve a lookup each time it is used
        JsonProvider jsonProvider = JsonProvider.provider();
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            try (ResultSet rs = dbMetaData.getTables(null, null, tableName, null)) {
                JsonArrayBuilder tablesJsonArr = jsonProvider.createArrayBuilder();
                while (rs.next()) {
                    tablesJsonArr.add(
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
                                    .add("tableType", rs.getString("TABLE_TYPE"))
                                    .add(
                                            "remarks",
                                            Optional.ofNullable(rs.getString("REMARKS"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "typeCat",
                                            Optional.ofNullable(rs.getString("TYPE_CAT"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "typeSchema",
                                            Optional.ofNullable(rs.getString("TYPE_SCHEM"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "typeName",
                                            Optional.ofNullable(rs.getString("TYPE_NAME"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "selfReferencingColName",
                                            Optional.ofNullable(rs.getString("SELF_REFERENCING_COL_NAME"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                                    .add(
                                            "refGeneration",
                                            Optional.ofNullable(rs.getString("REF_GENERATION"))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    )
                    );
                }
                return jsonProvider.createObjectBuilder()
                        .add("tables", tablesJsonArr)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
