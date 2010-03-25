/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.lexevs.dao.database.service.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Entity;
import org.junit.Test;
import org.lexevs.dao.database.hibernate.registry.HibernateRegistryDao;
import org.lexevs.dao.database.service.entity.EntityService;
import org.lexevs.dao.database.service.entity.VersionableEventEntityService;
import org.lexevs.dao.database.service.event.DatabaseServiceEventListener;
import org.lexevs.dao.database.service.event.property.PropertyUpdateEvent;
import org.lexevs.dao.database.service.listener.DefaultServiceEventListener;
import org.lexevs.dao.database.utility.DaoUtility;
import org.lexevs.dao.test.LexEvsDbUnitTestBase;
import org.lexevs.registry.model.RegistryEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class VersionableEntityServiceTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class VersionablePropertyServiceTest extends LexEvsDbUnitTestBase {

	/** The service. */
	@Resource
	private VersionableEventPropertyService service;
	
	@Resource
	private VersionableEventEntityService entityService;

	@Resource
	private HibernateRegistryDao registryDao;
	
	@Resource
	private HibernateTransactionManager txmgr;
	

	/**
	 * Insert entity.
	 * 
	 * @throws Exception the exception
	 */
	@Test
	public void updateProperty() throws Exception{

		JdbcTemplate template = new JdbcTemplate(getDataSource());

		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyId) " +
		"values ('pguid', 'eguid', 'entity', 'pid', 'pvalue', 'propId')");

		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
		"values ('csguid', 'csname', 'csuri', 'csversion')");

		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
		"values ('eguid', 'csguid', 'ecode', 'ens')");

		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("csuri");
		entry.setResourceVersion("csversion");
		entry.setDbSchemaVersion("2.0");
		registryDao.insertRegistryEntry(entry);

		List<RegistryEntry> entries = registryDao.getAllRegistryEntries();
		assertEquals(1,entries.size());

		Property property = new Property();
		property.setPropertyId("propId");
		property.setValue(DaoUtility.createText("updated prop value"));

		List<DatabaseServiceEventListener> original = service.getDatabaseServiceEventListeners();

		TestListener testListener = new TestListener(entityService, false);

		List<DatabaseServiceEventListener> testListeners = new ArrayList<DatabaseServiceEventListener>();
		testListeners.add(testListener);
		service.setDatabaseServiceEventListeners(testListeners);

		service.updateEntityProperty("csuri", "csversion", "ecode", "ens", "propId", property);

		assertTrue(testListener.foundUpdate);

		service.setDatabaseServiceEventListeners(original);
	}

	
	@Test
	public void updatePropertyRollbackInListener() throws Exception{

		JdbcTemplate template = new JdbcTemplate(getDataSource());

		template.execute("Insert into property (propertyGuid, referenceGuid, referenceType, propertyName, propertyValue, propertyId) " +
		"values ('pguid', 'eguid', 'entity', 'pid', 'pvalue', 'propId')");

		template.execute("Insert into codingScheme (codingSchemeGuid, codingSchemeName, codingSchemeUri, representsVersion) " +
		"values ('csguid', 'csname', 'csuri', 'csversion')");

		template.execute("Insert into entity (entityGuid, codingSchemeGuid, entityCode, entityCodeNamespace) " +
		"values ('eguid', 'csguid', 'ecode', 'ens')");

		RegistryEntry entry = new RegistryEntry();
		entry.setResourceUri("csuri");
		entry.setResourceVersion("csversion");
		entry.setDbSchemaVersion("2.0");
		registryDao.insertRegistryEntry(entry);

		List<RegistryEntry> entries = registryDao.getAllRegistryEntries();
		assertEquals(1,entries.size());

		Property property = new Property();
		property.setPropertyId("propId");
		property.setValue(DaoUtility.createText("updated prop value"));

		List<DatabaseServiceEventListener> original = service.getDatabaseServiceEventListeners();

		TestListener testListener = new TestListener(entityService, true);

		List<DatabaseServiceEventListener> testListeners = new ArrayList<DatabaseServiceEventListener>();
		testListeners.add(testListener);
		service.setDatabaseServiceEventListeners(testListeners);

		boolean exceptionThrown = false;
		try {
			service.updateEntityProperty("csuri", "csversion", "ecode", "ens", "propId", property);
		} catch (Exception e) {
			exceptionThrown = true;
		} finally {
			assertTrue(exceptionThrown);
		}

		assertTrue(testListener.foundUpdate);
		
		template.queryForObject("Select * from property", new RowMapper(){

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				assertEquals("pvalue", rs.getString(13));

				return null;
			}
		});

		service.setDatabaseServiceEventListeners(original);
	}

	private static class TestListener extends DefaultServiceEventListener {
		boolean foundUpdate = false;
		EntityService service;
		boolean throwException = false;
		
		TestListener(EntityService service, boolean throwException){
			this.service = service;
			this.throwException = throwException;
		}
		
		public boolean onPropertyUpdate(PropertyUpdateEvent event) {
			foundUpdate = true;
			Entity entity = service.getEntity("csuri", "csversion", "ecode", "ens");
			assertEquals("updated prop value", entity.getProperty()[0].getValue().getContent());
			
			if(this.throwException) {
				throw new RuntimeException();
			}
			return true;
		}
	}
}
