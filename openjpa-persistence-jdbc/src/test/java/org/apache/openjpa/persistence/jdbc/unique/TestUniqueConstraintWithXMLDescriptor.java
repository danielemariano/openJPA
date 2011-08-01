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
package org.apache.openjpa.persistence.jdbc.unique;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.openjpa.persistence.jdbc.SQLSniffer;
import org.apache.openjpa.persistence.test.SQLListenerTestCase;

/**
 * Tests unique constraints specified via XML Descriptor for primary/secondary
 * table, sequence generator, join tables have been defined on database by
 * examining DDL statements.
 * 
 * @see resources/org/apache/openjpa/persistence/jdbc/unique/orm.xml 
 * defines the ORM mapping. 
 * 
 * @author Pinaki Poddar
 *
 */
public class TestUniqueConstraintWithXMLDescriptor extends SQLListenerTestCase {
	@Override
	public void setUp(Object... props) {
		super.setUp(DROP_TABLES, UniqueA.class, UniqueB.class);
	}

	protected String getPersistenceUnitName() {
		return "test-unique-constraint";
	}

	public void testMapping() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.getTransaction().commit();
		em.close();
		// The above should trigger schema definition

		List<String> sqls = super.sql;
		assertFalse("No SQL DDL registered", sqls.isEmpty());

		// Following verification techniques is fragile as databases DDL
		// syntax vary greatly on UNIQUE CONSTRAINT
		assertSQLFragnments(sqls, "CREATE TABLE UNIQUE_A_XML",
				"UNIQUE \\w*\\(a1x, a2x\\)", 
				"UNIQUE \\w*\\(a3x, a4x\\)");
		assertSQLFragnments(sqls, "CREATE TABLE UNIQUE_B_XML",
				"UNIQUE \\w*\\(b1x, b2x\\)");
		assertSQLFragnments(sqls, "CREATE TABLE UNIQUE_SECONDARY_XML",
				"UNIQUE \\w*\\(sa1x\\)");
		assertSQLFragnments(sqls, "CREATE TABLE UNIQUE_GENERATOR_XML",
				"UNIQUE \\w*\\(GEN1_XML, GEN2_XML\\)");
		assertSQLFragnments(sqls, "CREATE TABLE UNIQUE_JOINTABLE_XML",
				"UNIQUE \\w*\\(FK_A_XML, FK_B_XML\\)");
	}

	void assertSQLFragnments(List<String> list, String... keys) {
		if (SQLSniffer.matches(list, keys))
			return;
		int i = 0;
		for (String sql : list) {
			i++;
			System.out.println("" + i + ":" + sql);
		}
		fail("None of the " + sql.size() + " SQL contains all keys "
				+ Arrays.toString(keys));
	}
}