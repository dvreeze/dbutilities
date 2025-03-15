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

package eu.cdevreeze.dbutilities.datasource;

import com.google.common.base.Preconditions;
import oracle.jdbc.datasource.impl.OracleDataSource;
import org.eclipse.microprofile.config.Config;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Oracle {@link DataSource} factory.
 *
 * @author Chris de Vreeze
 */
public final class OracleDataSources {

    private OracleDataSources() {
    }

    public static DataSource getDataSource(Config config) {
        try {
            var dataSource = new OracleDataSource();

            dataSource.setDriverType("thin");
            dataSource.setServerName(config.getValue("oracle.serverName", String.class));
            dataSource.setPortNumber(config.getValue("oracle.portNumber", Integer.class));

            // Either service name or database name is needed, but service name is preferred.
            // Using the service name (rather than the SID) can help prevent ORA-12505 errors.
            Optional<String> serviceNameOption = config.getOptionalValue("oracle.serviceName", String.class);
            // JDBC database name corresponds to SID in Oracle, if I am not mistaken
            Optional<String> databaseNameOption = config.getOptionalValue("oracle.databaseName", String.class);

            Preconditions.checkArgument(serviceNameOption.isPresent() || databaseNameOption.isPresent());

            serviceNameOption.ifPresent(dataSource::setServiceName);

            if (serviceNameOption.isEmpty()) {
                String databaseName = databaseNameOption.orElseThrow();
                dataSource.setDatabaseName(databaseName);
            }

            dataSource.setUser(config.getValue("oracle.user", String.class));
            dataSource.setPassword(config.getValue("oracle.password", String.class));

            // Consider debugging if needed by printing out the JDBC URL created so far

            return dataSource;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
