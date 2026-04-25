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

package eu.cdevreeze.dbutilities.console;

import com.google.common.base.Preconditions;
import eu.cdevreeze.dbutilities.JdbcConnectionToJsonObjectFunction;
import eu.cdevreeze.dbutilities.JdbcConnectionToJsonObjectFunctionFactory;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import org.eclipse.microprofile.config.Config;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.sql.DataSource;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.*;

/**
 * Console program using a {@link JdbcConnectionToJsonObjectFunction}. The first program argument
 * is the name of the {@link JdbcConnectionToJsonObjectFunction} to create and run,
 * and the remaining program arguments are passed to the {@link JdbcConnectionToJsonObjectFunctionFactory}
 * to create a {@link JdbcConnectionToJsonObjectFunction}, which is subsequently run.
 *
 * @author Chris de Vreeze
 */
public final class JdbcProgramReturningJson {

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        String connectionFunctionName = args[0];

        List<String> factoryArgs = Arrays.stream(args).skip(1).toList();

        run(connectionFunctionName, factoryArgs);
    }

    public static void run(String connectionFunctionName, List<String> factoryArgs) {
        Weld weld = new Weld();

        try (WeldContainer weldContainer = weld.initialize()) {
            Instance<Config> configInstance = CDI.current().select(Config.class, Default.Literal.INSTANCE);

            Preconditions.checkArgument(
                    configInstance.isResolvable(),
                    String.format("Could not resolve Config with required qualifier '%s'", Default.Literal.INSTANCE)
            );

            Config config = configInstance.get();

            // Typically, system property "dataSourceName" has been passed to the program
            String dataSourceName =
                    config.getOptionalValue("dataSourceName", String.class).orElseThrow();

            Instance<DataSource> dataSourceInstance = CDI.current().select(DataSource.class, NamedLiteral.of(dataSourceName));

            Preconditions.checkArgument(
                    dataSourceInstance.isResolvable(),
                    String.format("Could not resolve DataSource with name '%s'", dataSourceName)
            );

            Instance<JdbcConnectionToJsonObjectFunctionFactory> functionFactoryInstance =
                    CDI.current().select(JdbcConnectionToJsonObjectFunctionFactory.class, NamedLiteral.of(connectionFunctionName));

            Preconditions.checkArgument(
                    functionFactoryInstance.isResolvable(),
                    String.format("Could not resolve function with name '%s'", connectionFunctionName)
            );

            JdbcConnectionToJsonObjectFunction function = functionFactoryInstance.get().apply(factoryArgs);

            // Do the actual work within a JDBC Connection
            JsonObject result;
            try (Connection conn = dataSourceInstance.get().getConnection()) {
                result = function.apply(conn);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            StringWriter sw = new StringWriter();
            Map<String, Object> props = new HashMap<>();
            props.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(props);
            try (JsonWriter jsonWriter = jsonWriterFactory.createWriter(sw)) {
                jsonWriter.writeObject(result);
            }
            String resultAsString = sw.toString();

            System.out.println(resultAsString);
        }
    }
}
