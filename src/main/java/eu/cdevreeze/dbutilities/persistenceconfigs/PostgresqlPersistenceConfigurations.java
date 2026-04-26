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

package eu.cdevreeze.dbutilities.persistenceconfigs;

import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import org.eclipse.microprofile.config.Config;

import static jakarta.persistence.Persistence.ConnectionProperties.*;

/**
 * PostgreSQL {@link PersistenceConfiguration} factory.
 *
 * @author Chris de Vreeze
 */
public class PostgresqlPersistenceConfigurations {

    private PostgresqlPersistenceConfigurations() {
        // Non-instantiable
    }

    public static PersistenceConfiguration getPersistenceConfiguration(Config config) {
        String serverName = config.getValue("postgresql.serverName", String.class);
        int portNumber = config.getValue("postgresql.portNumber", Integer.class);
        String databaseName = config.getValue("postgresql.databaseName", String.class);
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", serverName, portNumber, databaseName);

        return new PersistenceConfiguration("postgresql")
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .defaultToOneFetchType(FetchType.LAZY) // Just in case we use Entities
                // .property(JDBC_DATASOURCE, PostgresqlDataSources.getDataSource(config))
                .property(JDBC_DRIVER, "org.postgresql.Driver")
                .property(JDBC_URL, jdbcUrl)
                .property(JDBC_USER, config.getValue("postgresql.user", String.class))
                .property(JDBC_PASSWORD, config.getValue("postgresql.password", String.class))
                .property("hibernate.connection.useSSL", "false");
    }
}
