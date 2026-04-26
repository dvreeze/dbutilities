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

package eu.cdevreeze.dbutilities.cdiwiring.connectionfunction;

import eu.cdevreeze.dbutilities.connectionfunction.*;
import eu.cdevreeze.dbutilities.function.JdbcConnectionFunctionFactory;
import eu.cdevreeze.dbutilities.function.JdbcConnectionToElementFunctionFactory;
import eu.cdevreeze.dbutilities.function.JdbcConnectionToJsonObjectFunctionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Named;

/**
 * CDI wiring of {@link JdbcConnectionFunctionFactory} instances.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class ConnectionFunctionWiring {

    @Produces
    @ApplicationScoped
    @Named("GetJsonQueryResults")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetJsonQueryResultsFactory getJsonQueryResultsFactory() {
        return new GetJsonQueryResultsFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetQueryResultsAsXml")
    @Typed({JdbcConnectionToElementFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetQueryResultsAsXmlFactory getQueryResultsAsXmlFactory() {
        return new GetQueryResultsAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetQueryResults")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetQueryResultsFactory getQueryResultsFactory() {
        return new GetQueryResultsFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetTableColumnsMetaData")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetTableColumnsMetaDataFactory getTableColumnsMetaDataFactory() {
        return new GetTableColumnsMetaDataFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetTableMetaData")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetTableMetaDataFactory getTableMetaDataFactory() {
        return new GetTableMetaDataFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("GetXmlQueryResultsAsXml")
    @Typed({JdbcConnectionToElementFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public GetXmlQueryResultsAsXmlFactory getXmlQueryResultsAsXmlFactory() {
        return new GetXmlQueryResultsAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectAllFromTableAsXml")
    @Typed({JdbcConnectionToElementFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public SelectAllFromTableAsXmlFactory selectAllFromTableAsXmlFactory() {
        return new SelectAllFromTableAsXmlFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectAllFromTable")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public SelectAllFromTableFactory selectAllFromTableFactory() {
        return new SelectAllFromTableFactory();
    }

    @Produces
    @ApplicationScoped
    @Named("SelectRowCountFromTable")
    @Typed({JdbcConnectionToJsonObjectFunctionFactory.class, JdbcConnectionFunctionFactory.class})
    public SelectRowCountFromTableFactory selectRowCountFromTableFactory() {
        return new SelectRowCountFromTableFactory();
    }
}
