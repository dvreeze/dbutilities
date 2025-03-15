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

package eu.cdevreeze.dbutilities.cdiwiring.datasource;

import eu.cdevreeze.dbutilities.datasource.Db2DataSources;
import eu.cdevreeze.dbutilities.datasource.OracleDataSources;
import eu.cdevreeze.dbutilities.datasource.PostgresqlDataSources;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.Config;

import javax.sql.DataSource;

/**
 * CDI wiring of {@link javax.sql.DataSource} instances.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class DataSourceWiring {

    @Produces
    @Named("db2")
    @ApplicationScoped
    public static DataSource getDb2DataSource(Config config) {
        return Db2DataSources.getDataSource(config);
    }

    @Produces
    @Named("oracle")
    @ApplicationScoped
    public static DataSource getOracleDataSource(Config config) {
        return OracleDataSources.getDataSource(config);
    }

    @Produces
    @Named("postgresql")
    @ApplicationScoped
    public static DataSource getPostgresqlDataSource(Config config) {
        return PostgresqlDataSources.getDataSource(config);
    }
}
