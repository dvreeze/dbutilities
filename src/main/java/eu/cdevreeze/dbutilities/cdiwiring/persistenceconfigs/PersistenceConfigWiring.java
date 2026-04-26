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

package eu.cdevreeze.dbutilities.cdiwiring.persistenceconfigs;

import eu.cdevreeze.dbutilities.persistenceconfigs.Db2PersistenceConfigurations;
import eu.cdevreeze.dbutilities.persistenceconfigs.OraclePersistenceConfigurations;
import eu.cdevreeze.dbutilities.persistenceconfigs.PostgresqlPersistenceConfigurations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.persistence.PersistenceConfiguration;
import org.eclipse.microprofile.config.Config;

/**
 * CDI wiring of {@link PersistenceConfiguration} instances.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class PersistenceConfigWiring {

    @Produces
    @Named("db2")
    @ApplicationScoped
    public PersistenceConfiguration getDb2PersistenceConfiguration(Config config) {
        return Db2PersistenceConfigurations.getPersistenceConfiguration(config);
    }

    @Produces
    @Named("oracle")
    @ApplicationScoped
    public PersistenceConfiguration getOraclePersistenceConfiguration(Config config) {
        return OraclePersistenceConfigurations.getPersistenceConfiguration(config);
    }

    @Produces
    @Named("postgresql")
    @ApplicationScoped
    public PersistenceConfiguration getPostgresqlPersistenceConfiguration(Config config) {
        return PostgresqlPersistenceConfigurations.getPersistenceConfiguration(config);
    }
}
