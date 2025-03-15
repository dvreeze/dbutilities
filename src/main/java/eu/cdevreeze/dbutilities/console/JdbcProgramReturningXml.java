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
import eu.cdevreeze.dbutilities.ConnectionToElementFunction;
import eu.cdevreeze.dbutilities.ConnectionToElementFunctionFactory;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import org.eclipse.microprofile.config.Config;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Console program using a {@link ConnectionToElementFunction}. The first program argument
 * is the name of the {@link ConnectionToElementFunction} to create and run,
 * and the remaining program arguments are passed to the {@link ConnectionToElementFunctionFactory}
 * to create a {@link ConnectionToElementFunction}, which is subsequently run.
 *
 * @author Chris de Vreeze
 */
public class JdbcProgramReturningXml {

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

            Instance<ConnectionToElementFunctionFactory> functionFactoryInstance =
                    CDI.current().select(ConnectionToElementFunctionFactory.class, NamedLiteral.of(connectionFunctionName));

            Preconditions.checkArgument(
                    functionFactoryInstance.isResolvable(),
                    String.format("Could not resolve function with name '%s'", connectionFunctionName)
            );

            ConnectionToElementFunction function = functionFactoryInstance.get().apply(factoryArgs);

            // Do the actual work within a JDBC Connection
            Element result;
            try (Connection conn = dataSourceInstance.get().getConnection()) {
                result = function.apply(conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            DocumentPrinter docPrinter = DocumentPrinters.instance();
            String xmlString = docPrinter.print(result);

            System.out.println(xmlString);
        }
    }
}
