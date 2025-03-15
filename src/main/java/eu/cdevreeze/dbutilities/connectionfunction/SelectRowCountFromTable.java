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
import jakarta.json.JsonObject;
import jakarta.json.spi.JsonProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link ConnectionToJsonObjectFunction} that selects the row count from a given table.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public final class SelectRowCountFromTable implements ConnectionToJsonObjectFunction {

    private final String tableName;

    public SelectRowCountFromTable(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public JsonObject apply(Connection connection) {
        // Unlike Json, JsonProvider does not involve a lookup each time it is used
        JsonProvider jsonProvider = JsonProvider.provider();

        // TODO Protect against SQL injection

        try (PreparedStatement ps = connection.prepareStatement("select count(*) from " + tableName)) {
            try (ResultSet rs = ps.executeQuery()) {
                long rowCount;
                if (rs.next()) {
                    rowCount = rs.getLong(1);
                } else {
                    throw new RuntimeException("Missing result row");
                }
                return jsonProvider.createObjectBuilder().add("rowCount", rowCount).build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
