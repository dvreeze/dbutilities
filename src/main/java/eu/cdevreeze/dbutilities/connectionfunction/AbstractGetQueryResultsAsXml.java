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

package eu.cdevreeze.dbutilities.connectionfunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.dbutilities.ConnectionToElementFunction;
import eu.cdevreeze.dbutilities.connectionfunction.internal.PreparedStatements;
import eu.cdevreeze.dbutilities.connectionfunction.internal.QueryParameter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Nodes;

import javax.xml.namespace.QName;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static eu.cdevreeze.yaidom4j.dom.immutabledom.Nodes.elem;

/**
 * Abstract {@link ConnectionToElementFunction} that returns the results of a query as XML.
 *
 * @author Chris de Vreeze
 */
public abstract class AbstractGetQueryResultsAsXml implements ConnectionToElementFunction {

    protected abstract String getQueryString();

    protected abstract List<QueryParameter> getQueryParameters();

    @Override
    public final Element apply(Connection connection) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
