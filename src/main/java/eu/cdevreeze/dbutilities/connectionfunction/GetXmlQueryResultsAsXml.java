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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.dbutilities.ConnectionToElementFunction;
import eu.cdevreeze.dbutilities.connectionfunction.internal.QueryParameter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParser;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

/**
 * {@link ConnectionToElementFunction} that returns the results of an XML-returning query as XML.
 * It is expected that each row has just one column, and that it is of type XML.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public final class GetXmlQueryResultsAsXml implements ConnectionToElementFunction {

    private final GetQueryResultsAsXml delegate;

    public GetXmlQueryResultsAsXml(Path queryFile, List<QueryParameter> queryParameters) {
        this.delegate = new GetQueryResultsAsXml(queryFile, queryParameters);
    }

    @Override
    public Element apply(Connection connection) {
        Element rawResult = delegate.apply(connection);

        Preconditions.checkArgument(rawResult.name().equals(new QName("rows")));
        Preconditions.checkArgument(rawResult.childElementStream().allMatch(e -> e.name().equals(new QName("row"))));

        DocumentParser docParser = DocumentParsers.builder().removingInterElementWhitespace().build();

        return rawResult.transformDescendantElements(elem -> {
            if (elem.name().equals(new QName("row"))) {
                Preconditions.checkArgument(elem.childElementStream().count() == 1);
                Element childElement = elem.childElementStream().findFirst().orElseThrow();

                String nestedXmlString = childElement.text();

                Element nestedElement = docParser.parse(new InputSource(new StringReader(nestedXmlString))).documentElement();
                return elem.withChildren(ImmutableList.of(nestedElement));
            } else {
                return elem;
            }
        });
    }
}
