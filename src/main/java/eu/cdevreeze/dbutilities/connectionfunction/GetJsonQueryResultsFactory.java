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

import eu.cdevreeze.dbutilities.ConnectionFunctionFactory;
import eu.cdevreeze.dbutilities.ConnectionToJsonObjectFunctionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Named;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Factory of {@link GetJsonQueryResults} objects.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
@Named("GetJsonQueryResults")
@Typed({ConnectionToJsonObjectFunctionFactory.class, ConnectionFunctionFactory.class})
public class GetJsonQueryResultsFactory implements ConnectionToJsonObjectFunctionFactory {

    @Override
    public GetJsonQueryResults apply(List<String> args) {
        Objects.checkIndex(0, args.size());
        Path queryFile = Path.of(Objects.requireNonNull(args.get(0)));
        return new GetJsonQueryResults(queryFile);
    }
}
