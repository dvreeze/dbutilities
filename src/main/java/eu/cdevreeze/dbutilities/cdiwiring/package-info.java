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

/**
 * Localized namespace where CDI wiring occurs. This leaves most code "unaffected by CDI".
 * As a result, the conceptual complexities of CDI are localized, and most code remains plain Java code.
 * This also means that most code is not affected by limitations imposed on CDI beans, and that most
 * code contains no CDI proxies.
 * <p>
 * See <a href="https://alexn.org/blog/2022/09/19/java-cultural-problem/">Java cultural problem</a> for an
 * excellent explanation of why localizing use of CDI is a good idea.
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.dbutilities.cdiwiring;
