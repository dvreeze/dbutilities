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

import com.google.common.base.Preconditions;
import eu.cdevreeze.dbutilities.ConnectionToJsonObjectFunction;
import eu.cdevreeze.dbutilities.connectionfunction.internal.QueryParameter;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.spi.JsonProvider;

import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * {@link ConnectionToJsonObjectFunction} that returns the results of a JSON-returning query as JSON.
 * It is expected that each row has just one column, and that it is of type "JSON object".
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public class GetJsonQueryResults implements ConnectionToJsonObjectFunction {

    private final GetQueryResults delegate;

    public GetJsonQueryResults(Path queryFile, List<QueryParameter> queryParameters) {
        this.delegate = new GetQueryResults(queryFile, queryParameters);
    }

    @Override
    public JsonObject apply(Connection connection) {
        JsonObject rawResult = delegate.apply(connection);

        Preconditions.checkArgument(rawResult.containsKey("rows"));
        Preconditions.checkArgument(rawResult.get("rows") instanceof JsonArray);
        JsonArray rows = rawResult.get("rows").asJsonArray();

        Preconditions.checkArgument(rows.stream().allMatch(v -> v.asJsonObject().containsKey("json_object")));

        JsonProvider jsonProvider = JsonProvider.provider();
        JsonReaderFactory jsonReaderFactory = jsonProvider.createReaderFactory(Map.of());

        return jsonProvider.createObjectBuilder()
                .add(
                        "rows",
                        jsonProvider.createArrayBuilder(
                                rows.stream()
                                        .map(row -> (JsonString) row.asJsonObject().get("json_object"))
                                        .map(escapedJson -> jsonReaderFactory.createReader(new StringReader(escapedJson.getString())).readObject())
                                        .toList()
                        )
                )
                .build();
    }
}
