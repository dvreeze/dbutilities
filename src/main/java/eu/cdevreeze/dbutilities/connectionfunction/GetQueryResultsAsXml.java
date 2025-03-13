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

import eu.cdevreeze.dbutilities.ConnectionToElementFunction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link ConnectionToElementFunction} that returns the results of a query as XML.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public class GetQueryResultsAsXml extends AbstractGetQueryResultsAsXml {

    private final Path queryFile;

    public GetQueryResultsAsXml(Path queryFile) {
        this.queryFile = queryFile;
    }

    // TODO Protect against SQL injection

    @Override
    protected String getQueryString() {
        try {
            return Files.readString(queryFile).strip();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
