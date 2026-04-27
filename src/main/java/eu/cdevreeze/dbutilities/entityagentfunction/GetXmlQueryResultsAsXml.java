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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.dbutilities.entityagentfunction.internal.QueryParameter;
import eu.cdevreeze.dbutilities.function.EntityAgentToElementFunction;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParser;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import jakarta.persistence.EntityAgent;

/**
 * {@link EntityAgentToElementFunction} that returns the results of an XML-returning query as XML.
 * It is expected that each row has just one column, and that it is of type XML.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public final class GetXmlQueryResultsAsXml implements EntityAgentToElementFunction {

    private final GetQueryResultsAsXml delegate;

    public GetXmlQueryResultsAsXml(Path queryFile, List<QueryParameter> queryParameters) {
        this.delegate = new GetQueryResultsAsXml(queryFile, queryParameters);
    }

    @Override
    public Element apply(EntityAgent entityAgent) {
        Element rawResult = delegate.apply(entityAgent);

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
