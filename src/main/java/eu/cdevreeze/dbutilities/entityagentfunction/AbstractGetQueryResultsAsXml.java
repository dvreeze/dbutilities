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

package eu.cdevreeze.dbutilities.entityagentfunction;

import module java.base;
import module java.sql;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.dbutilities.entityagentfunction.internal.PreparedStatements;
import eu.cdevreeze.dbutilities.entityagentfunction.internal.QueryParameter;
import eu.cdevreeze.dbutilities.function.EntityAgentToElementFunction;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Nodes;
import jakarta.persistence.EntityAgent;

import static eu.cdevreeze.yaidom4j.dom.immutabledom.Nodes.elem;

/**
 * Abstract {@link EntityAgentToElementFunction} that returns the results of a query as XML.
 *
 * @author Chris de Vreeze
 */
public abstract class AbstractGetQueryResultsAsXml implements EntityAgentToElementFunction {

    protected abstract String getQueryString();

    protected abstract List<QueryParameter> getQueryParameters();

    @Override
    public final Element apply(EntityAgent entityAgent) {
        return entityAgent.callWithConnection((Connection conn) -> apply(conn));
    }

    private Element apply(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(getQueryString())) {
            PreparedStatements.setParameters(ps, getQueryParameters());

            try (ResultSet rs = ps.executeQuery()) {
                List<Element> rows = new ArrayList<>();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                while (rs.next()) {
                    List<Element> columns = new ArrayList<>();
                    IntStream.rangeClosed(1, rsMetaData.getColumnCount())
                            .forEach(i -> {
                                try {
                                    String columnValue = rs.getString(i);
                                    columns.add(
                                            elem(rsMetaData.getColumnLabel(i))
                                                    .withAttributes(
                                                            ImmutableMap.of(new QName("null"), "true")
                                                                    .entrySet()
                                                                    .stream()
                                                                    .filter(ignored -> columnValue == null)
                                                                    .collect(
                                                                            ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)
                                                                    )
                                                    )
                                                    .plusChildOption(
                                                            Optional.ofNullable(columnValue)
                                                                    .map(Nodes::text)
                                                    )
                                    );
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    rows.add(
                            elem("row").withChildren(ImmutableList.copyOf(columns))
                    );
                }
                return elem("rows").withChildren(ImmutableList.copyOf(rows));
            }
        }
    }
}
