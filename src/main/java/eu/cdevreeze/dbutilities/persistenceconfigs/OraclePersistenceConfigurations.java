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

import eu.cdevreeze.dbutilities.datasource.OracleDataSources;
import jakarta.persistence.FetchType;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import org.eclipse.microprofile.config.Config;

import static jakarta.persistence.Persistence.ConnectionProperties.JDBC_DATASOURCE;
import static jakarta.persistence.Persistence.ConnectionProperties.JDBC_DRIVER;

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
        // TODO Set JDBC URL rather than dialect; and make it work
        return new PersistenceConfiguration("oracle")
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .defaultToOneFetchType(FetchType.LAZY) // Just in case we use Entities
                .property(JDBC_DATASOURCE, OracleDataSources.getDataSource(config))
                .property(JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver")
                .property("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
    }
}
