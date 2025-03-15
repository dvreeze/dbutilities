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

import org.eclipse.microprofile.config.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * Postgresql {@link DataSource} factory.
 *
 * @author Chris de Vreeze
 */
public class PostgresqlDataSources {

    private PostgresqlDataSources() {
    }

    public static DataSource getDataSource(Config config) {
        var dataSource = new PGSimpleDataSource();

        dataSource.setServerNames(new String[]{config.getValue("postgresql.serverName", String.class)});
        dataSource.setPortNumbers(new int[]{config.getValue("postgresql.portNumber", Integer.class)});
        dataSource.setDatabaseName(config.getValue("postgresql.databaseName", String.class));
        dataSource.setUser(config.getValue("postgresql.user", String.class));
        dataSource.setPassword(config.getValue("postgresql.password", String.class));
        dataSource.setSsl(config.getValue("postgresql.ssl", Boolean.class));
        config.getOptionalValue("postgresql.sslfactory", String.class)
                .ifPresent(dataSource::setSslfactory);

        return dataSource;
    }
}
