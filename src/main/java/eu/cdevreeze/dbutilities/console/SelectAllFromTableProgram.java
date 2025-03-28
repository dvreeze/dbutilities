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

package eu.cdevreeze.dbutilities.console;

import eu.cdevreeze.dbutilities.connectionfunction.SelectAllFromTable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Program that calls {@link SelectAllFromTable} and shows the result.
 * <p>
 * The only program argument is the table name.
 *
 * @author Chris de Vreeze
 */
public final class SelectAllFromTableProgram {

    public static void main(String... args) {
        Objects.checkIndex(0, args.length);
        String tableName = args[0];
        Objects.requireNonNull(tableName);

        JdbcProgramReturningJson.run(
                SelectAllFromTable.class.getSimpleName(),
                Arrays.stream(args).toList()
        );
    }
}
