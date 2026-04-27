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

package eu.cdevreeze.dbutilities.cdiwiring.entityagentfunction;

import eu.cdevreeze.dbutilities.entityagentfunction.*;
import eu.cdevreeze.dbutilities.function.EntityAgentFunctionFactory;
import eu.cdevreeze.dbutilities.function.EntityAgentToElementFunctionFactory;
import eu.cdevreeze.dbutilities.function.EntityAgentToJsonObjectFunctionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Named;

/**
 * CDI wiring of {@link EntityAgentFunctionFactory} instances.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class EntityAgentFunctionWiring {

    @Produces
    @ApplicationScoped
    @Named("GetJsonQueryResults")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetJsonQueryResultsFactory getJsonQueryResultsFactory() {
        return new GetJsonQueryResultsFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetQueryResultsAsXml")
    @Typed({EntityAgentToElementFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetQueryResultsAsXmlFactory getQueryResultsAsXmlFactory() {
        return new GetQueryResultsAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetQueryResults")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetQueryResultsFactory getQueryResultsFactory() {
        return new GetQueryResultsFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetTableColumnsMetaData")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetTableColumnsMetaDataFactory getTableColumnsMetaDataFactory() {
        return new GetTableColumnsMetaDataFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetTableMetaData")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetTableMetaDataFactory getTableMetaDataFactory() {
        return new GetTableMetaDataFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetXmlQueryResultsAsXml")
    @Typed({EntityAgentToElementFunctionFactory.class, EntityAgentFunctionFactory.class})
    public GetXmlQueryResultsAsXmlFactory getXmlQueryResultsAsXmlFactory() {
        return new GetXmlQueryResultsAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectAllFromTableAsXml")
    @Typed({EntityAgentToElementFunctionFactory.class, EntityAgentFunctionFactory.class})
    public SelectAllFromTableAsXmlFactory selectAllFromTableAsXmlFactory() {
        return new SelectAllFromTableAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectAllFromTable")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public SelectAllFromTableFactory selectAllFromTableFactory() {
        return new SelectAllFromTableFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectRowCountFromTable")
    @Typed({EntityAgentToJsonObjectFunctionFactory.class, EntityAgentFunctionFactory.class})
    public SelectRowCountFromTableFactory selectRowCountFromTableFactory() {
        return new SelectRowCountFromTableFactory();
    }
}
