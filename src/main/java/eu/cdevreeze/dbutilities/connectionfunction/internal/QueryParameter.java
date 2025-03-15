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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameter data to be set on a {@link java.sql.PreparedStatement}.
 * <p>
 * This support for setting query parameters is far from complete, but should work in most
 * rather straightforward cases.
 *
 * @author Chris de Vreeze
 */
public record QueryParameter(Object object, SQLType sqlType) {

    public QueryParameter sanitized() {
        if (sqlType() == JDBCType.NULL) {
            // Just to be sure. Maybe it is not needed.
            return new QueryParameter(null, sqlType);
        } else {
            return this;
        }
    }

    public void setOnPreparedStatement(PreparedStatement ps, int parameterIndex) {
        try {
            // The overloaded variant passing a SQLType is not always implemented
            ps.setObject(parameterIndex, object(), sqlType().getVendorTypeNumber());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static QueryParameter from(Object object, String sqlTypeAsString) {
        return new QueryParameter(object, JDBCType.valueOf(sqlTypeAsString.toUpperCase())).sanitized();
    }

    public static List<QueryParameter> parseParameters(List<String> args) {
        Preconditions.checkArgument(
                args.size() % 2 == 0,
                "Expected even number of arguments (pairs of parameter value and JDBC type)"
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

    public static void setParametersOnPreparedStatement(
            List<QueryParameter> parameters,
            PreparedStatement ps
    ) {
        for (int idx = 0; idx < parameters.size(); idx++) {
            // In JDBC, parameters are 1-based
            parameters.get(idx).setOnPreparedStatement(ps, idx + 1);
        }
    }
}
