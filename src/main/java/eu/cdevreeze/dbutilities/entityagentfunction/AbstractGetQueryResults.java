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

package eu.cdevreeze.dbutilities.entityagentfunction;

import module java.base;
import module java.sql;
import eu.cdevreeze.dbutilities.entityagentfunction.internal.PreparedStatements;
import eu.cdevreeze.dbutilities.entityagentfunction.internal.QueryParameter;
import eu.cdevreeze.dbutilities.function.EntityAgentToJsonObjectFunction;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.persistence.EntityAgent;

/**
 * Abstract {@link EntityAgentToJsonObjectFunction} that returns the results of a query as JSON.
 *
 * @author Chris de Vreeze
 */
public abstract class AbstractGetQueryResults implements EntityAgentToJsonObjectFunction {

    protected abstract String getQueryString();

    protected abstract List<QueryParameter> getQueryParameters();

    @Override
    public final JsonObject apply(EntityAgent entityAgent) {
        return entityAgent.callWithConnection((Connection conn) -> apply(conn));
    }

    private JsonObject apply(Connection connection) throws SQLException {
        // Unlike class Json, class JsonProvider does not involve a lookup each time it is used
        JsonProvider jsonProvider = JsonProvider.provider();

        try (PreparedStatement ps = connection.prepareStatement(getQueryString())) {
            PreparedStatements.setParameters(ps, getQueryParameters());

            try (ResultSet rs = ps.executeQuery()) {
                JsonArrayBuilder rowsJsonArr = jsonProvider.createArrayBuilder();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                while (rs.next()) {
                    JsonObjectBuilder row = jsonProvider.createObjectBuilder();
                    IntStream.rangeClosed(1, rsMetaData.getColumnCount())
                            .forEach(i -> {
                                try {
                                    row.add(
                                            rsMetaData.getColumnLabel(i),
                                            Optional.ofNullable(rs.getString(i))
                                                    .map(v -> (JsonValue) jsonProvider.createValue(v))
                                                    .orElse(JsonValue.NULL)
                                    );
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    rowsJsonArr.add(row);
                }
                return jsonProvider.createObjectBuilder()
                        .add("rows", rowsJsonArr)
                        .build();
            }
        }
    }
}
