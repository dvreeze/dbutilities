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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * CDI-injectable Postgresql {@link DataSource}.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class PostgresqlDataSources {

    @Produces
    @Named("postgresql")
    @ApplicationScoped
    public DataSource getDataSource(Config config) {
        var datasource = new PGSimpleDataSource();

        datasource.setServerNames(new String[]{config.getValue("postgresql.serverName", String.class)});
        datasource.setPortNumbers(new int[]{config.getValue("postgresql.portNumber", Integer.class)});
        datasource.setDatabaseName(config.getValue("postgresql.databaseName", String.class));
        datasource.setUser(config.getValue("postgresql.user", String.class));
        datasource.setPassword(config.getValue("postgresql.password", String.class));
        datasource.setSsl(config.getValue("postgresql.ssl", Boolean.class));
        config.getOptionalValue("postgresql.sslfactory", String.class)
                .ifPresent(datasource::setSslfactory);

        return datasource;
    }
}
