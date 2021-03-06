/*
 *  Java Information Dynamics Toolkit (JIDT)
 *  Copyright (C) 2017, Joseph T. Lizier
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * This class was originally distributed as part of the Apache Commons
 *  Math3 library (3.6.1), under the Apache License Version 2.0, which is 
 *  copied below. This Apache 2 software is now included as a derivative
 *  work in the GPLv3 licensed JIDT project, as per:
 *  http://www.apache.org/licenses/GPL-compatibility.html
 *  
 * The original Apache source code has been modified as follows:
 * -- We have modified package names to sit inside the JIDT structure.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package infodynamics.utils.commonsmath3;

/**
 * Interface representing a <a href="http://mathworld.wolfram.com/Field.html">field</a>.
 * <p>
 * Classes implementing this interface will often be singletons.
 * </p>
 * @param <T> the type of the field elements
 * @see FieldElement
 * @since 2.0
 */
public interface Field<T> {

    /** Get the additive identity of the field.
     * <p>
     * The additive identity is the element e<sub>0</sub> of the field such that
     * for all elements a of the field, the equalities a + e<sub>0</sub> =
     * e<sub>0</sub> + a = a hold.
     * </p>
     * @return additive identity of the field
     */
    T getZero();

    /** Get the multiplicative identity of the field.
     * <p>
     * The multiplicative identity is the element e<sub>1</sub> of the field such that
     * for all elements a of the field, the equalities a &times; e<sub>1</sub> =
     * e<sub>1</sub> &times; a = a hold.
     * </p>
     * @return multiplicative identity of the field
     */
    T getOne();

    /**
     * Returns the runtime class of the FieldElement.
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     */
    Class<? extends FieldElement<T>> getRuntimeClass();

}
