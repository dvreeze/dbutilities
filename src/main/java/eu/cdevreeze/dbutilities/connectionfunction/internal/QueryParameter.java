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

package eu.cdevreeze.dbutilities.connectionfunction.internal;

import com.google.common.base.Preconditions;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameter data to be set on a {@link java.sql.PreparedStatement}.
 *
 * @author Chris de Vreeze
 */
public record QueryParameter(Object object, SQLType sqlType) {

    public QueryParameter {
        if (sqlType == JDBCType.NULL) {
            // Just to be sure. Maybe it is not needed.
            object = null;
        }
    }

    public static QueryParameter from(Object object, String sqlTypeAsString) {
        return new QueryParameter(object, JDBCType.valueOf(sqlTypeAsString.toUpperCase()));
    }

    public static List<QueryParameter> parseParameters(List<String> args) {
        Preconditions.checkArgument(
                args.size() % 2 == 0,
                "Expected even number of arguments (pairs of parameter value and JDBC type, such as VARCHAR, BOOLEAN, INTEGER or DECIMAL)"
        );

        if (args.isEmpty()) {
            return List.of();
        } else {
            QueryParameter firstParameter = from(args.get(0), args.get(1));
            List<QueryParameter> result = new ArrayList<>();
            result.add(firstParameter);
            // Recursion
            result.addAll(parseParameters(args.subList(2, args.size())));
            return result.stream().toList();
        }
    }
}
