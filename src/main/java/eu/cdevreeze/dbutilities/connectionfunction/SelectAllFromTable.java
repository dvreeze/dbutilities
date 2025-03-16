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

import eu.cdevreeze.dbutilities.ConnectionToJsonObjectFunction;
import eu.cdevreeze.dbutilities.connectionfunction.internal.QueryParameter;

import java.util.List;

/**
 * {@link ConnectionToJsonObjectFunction} that selects all data from a given table.
 * <p>
 * Instances are created by a dedicated factory object, and not by CDI injection.
 *
 * @author Chris de Vreeze
 */
public final class SelectAllFromTable extends AbstractGetQueryResults {

    private final String tableName;

    public SelectAllFromTable(String tableName) {
        this.tableName = checkTableNameWrtSqlInjection(tableName);
    }

    @Override
    protected String getQueryString() {
        return "select * from " + tableName;
    }

    @Override
    protected List<QueryParameter> getQueryParameters() {
        return List.of();
    }

    private static String checkTableNameWrtSqlInjection(String tableName) {
        if (tableName.chars().anyMatch(Character::isWhitespace)) {
            throw new RuntimeException("Table name with whitespace not allowed (to prevent SQL injection)");
        } else {
            return tableName;
        }
    }
}
