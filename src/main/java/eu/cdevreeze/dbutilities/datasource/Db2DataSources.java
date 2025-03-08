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

import com.ibm.db2.jcc.DB2SimpleDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.Config;

import javax.sql.DataSource;

/**
 * CDI-injectable Db2 {@link DataSource}.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class Db2DataSources {

    @Produces
    @Named("db2")
    @ApplicationScoped
    public DataSource getDataSource(Config config) {
        var dataSource = new DB2SimpleDataSource();

        dataSource.setDriverType(config.getValue("db2.driverType", Integer.class));
        dataSource.setServerName(config.getValue("db2.serverName", String.class));
        dataSource.setPortNumber(config.getValue("db2.portNumber", Integer.class));
        dataSource.setCurrentSchema(config.getValue("db2.currentSchema", String.class));
        dataSource.setDatabaseName(config.getValue("db2.databaseName", String.class));
        dataSource.setUser(config.getValue("db2.user", String.class));
        dataSource.setPassword(config.getValue("db2.password", String.class));

        return dataSource;
    }
}
