/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.slice.jdbc;

import java.lang.reflect.Constructor;
import java.sql.Statement;

import org.apache.openjpa.lib.util.ConcreteClassGenerator;

/**
 * A virtual Statement that delegates to many actual Statements.
 * 
 * @author Pinaki Poddar
 * 
 */
public abstract class DistributedStatement extends
    DistributedTemplate<Statement> {
    static final Constructor<DistributedStatement> concreteImpl;

    static {
        try {
            concreteImpl = ConcreteClassGenerator.getConcreteConstructor(DistributedStatement.class, 
                DistributedConnection.class);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public DistributedStatement(DistributedConnection c) {
        super(c);
    }

    public static DistributedStatement newInstance(DistributedConnection conn) {
        return ConcreteClassGenerator.newInstance(concreteImpl, conn);
    }
}