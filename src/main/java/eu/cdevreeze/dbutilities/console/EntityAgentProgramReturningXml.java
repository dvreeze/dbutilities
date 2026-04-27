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

import module java.base;
import com.google.common.base.Preconditions;
import eu.cdevreeze.dbutilities.function.EntityAgentToElementFunction;
import eu.cdevreeze.dbutilities.function.EntityAgentToElementFunctionFactory;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import org.eclipse.microprofile.config.Config;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Console program using a {@link EntityAgentToElementFunction}. The first program argument
 * is the name of the {@link EntityAgentToElementFunction} to create and run,
 * and the remaining program arguments are passed to the {@link EntityAgentToElementFunctionFactory}
 * to create a {@link EntityAgentToElementFunction}, which is subsequently run.
 *
 * @author Chris de Vreeze
 */
public final class EntityAgentProgramReturningXml {

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

            Instance<PersistenceConfiguration> persistenceConfigInstance = CDI.current().select(PersistenceConfiguration.class, NamedLiteral.of(dataSourceName));

            Preconditions.checkArgument(
                    persistenceConfigInstance.isResolvable(),
                    String.format("Could not resolve PersistenceConfiguration with name '%s'", dataSourceName)
            );

            Instance<EntityAgentToElementFunctionFactory> functionFactoryInstance =
                    CDI.current().select(EntityAgentToElementFunctionFactory.class, NamedLiteral.of(connectionFunctionName));

            Preconditions.checkArgument(
                    functionFactoryInstance.isResolvable(),
                    String.format("Could not resolve function with name '%s'", connectionFunctionName)
            );

            EntityAgentToElementFunction function = functionFactoryInstance.get().apply(factoryArgs);

            // Do the actual work within a JDBC Connection
            Element result;
            try (EntityManagerFactory emf = persistenceConfigInstance.get().createEntityManagerFactory()) {
                result = emf.callInTransaction(EntityAgent.class, function);
            }

            DocumentPrinter docPrinter = DocumentPrinters.instance();
            String xmlString = docPrinter.print(result);

            System.out.println(xmlString);
        }
    }
}
