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

import eu.cdevreeze.dbutilities.ConnectionToJsonObjectFunctionFactory;

import java.util.List;
import java.util.Objects;

/**
 * Factory of {@link SelectAllFromTable} objects.
 *
 * @author Chris de Vreeze
 */
public final class SelectAllFromTableFactory implements ConnectionToJsonObjectFunctionFactory {

    @Override
    public SelectAllFromTable apply(List<String> args) {
        Objects.checkIndex(0, args.size());
        String tableName = Objects.requireNonNull(args.get(0));
        return new SelectAllFromTable(tableName);
    }
}
