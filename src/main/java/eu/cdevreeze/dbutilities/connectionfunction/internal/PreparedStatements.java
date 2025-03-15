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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Support for setting parameters on a {@link java.sql.PreparedStatement}.
 * <p>
 * This support for setting query parameters is far from complete, but should work in most
 * rather straightforward cases.
 *
 * @author Chris de Vreeze
 */
public class PreparedStatements {

    private PreparedStatements() {
    }

    public static void setParameter(PreparedStatement ps, int parameterIndex, QueryParameter parameter) {
        try {
            // The overloaded variant passing a SQLType is not always implemented
            ps.setObject(parameterIndex, parameter.object(), parameter.sqlType().getVendorTypeNumber());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setParameters(PreparedStatement ps, List<QueryParameter> parameters) {
        for (int idx = 0; idx < parameters.size(); idx++) {
            // In JDBC, parameters are 1-based
            setParameter(ps, idx + 1, parameters.get(idx));
        }
    }
}
