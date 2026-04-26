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
 * Oracle {@link PersistenceConfiguration} factory.
 *
 * @author Chris de Vreeze
 */
public class OraclePersistenceConfigurations {

    private OraclePersistenceConfigurations() {
        // Non-instantiable
    }

    public static PersistenceConfiguration getPersistenceConfiguration(Config config) {
        String serverName = config.getValue("oracle.serverName", String.class);
        int portNumber = config.getValue("oracle.portNumber", Integer.class);
        String serviceName = config.getValue("oracle.serviceName", String.class);
        String jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:%s", serverName, portNumber, serviceName);

        return new PersistenceConfiguration("oracle")
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .defaultToOneFetchType(FetchType.LAZY) // Just in case we use Entities
                // .property(JDBC_DATASOURCE, OracleDataSources.getDataSource(config))
                .property(JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver")
                .property(JDBC_URL, jdbcUrl)
                .property(JDBC_USER, config.getValue("oracle.user", String.class))
                .property(JDBC_PASSWORD, config.getValue("oracle.password", String.class));
    }
}
