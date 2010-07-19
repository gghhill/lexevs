package org.lexevs.cts2.query;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.Entity;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.relations.AssociationEntity;
import org.junit.Test;


public class CodeSystemQueryOperationImplTest {
	 
	private CodeSystemQueryOperationImpl query = new CodeSystemQueryOperationImpl();
	
	@Test
	public void testListCodeSystems() {
		
		// an empty query example, fetch all codingschemes.
		CodingSchemeSummary queryByExample = new CodingSchemeSummary();
		CodingSchemeRenderingList results  = query.listCodeSystems(queryByExample);
		assertEquals(2, results.getCodingSchemeRenderingCount());
		
		// search by uri
		queryByExample = new CodingSchemeSummary();
		queryByExample.setCodingSchemeURI("1.2.3");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(1, results.getCodingSchemeRenderingCount());
		assertEquals("colors coding scheme", results.getCodingSchemeRendering(0).getCodingSchemeSummary().getFormalName());
		
		// search by version
		queryByExample = new CodingSchemeSummary();
		queryByExample.setRepresentsVersion("1.1");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(1, results.getCodingSchemeRenderingCount());
		assertEquals("Automobiles", results.getCodingSchemeRendering(0).getCodingSchemeSummary().getLocalName());
		
		// search by formalName
		queryByExample = new CodingSchemeSummary();
		queryByExample.setFormalName("test2");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(1, results.getCodingSchemeRenderingCount());
		assertEquals("Automobiles", results.getCodingSchemeRendering(0).getCodingSchemeSummary().getLocalName());

		// search by localName
		queryByExample = new CodingSchemeSummary();
		queryByExample.setLocalName("Automobiles");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(1, results.getCodingSchemeRenderingCount());
		assertEquals("Automobiles", results.getCodingSchemeRendering(0).getCodingSchemeSummary().getLocalName());
		
		// search by localName & version
		queryByExample = new CodingSchemeSummary();
		queryByExample.setLocalName("Automobiles");
		queryByExample.setCodingSchemeURI("1.0");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(0, results.getCodingSchemeRenderingCount());
		
		queryByExample = new CodingSchemeSummary();
		queryByExample.setLocalName("Automobiles");
		queryByExample.setCodingSchemeURI("urn:oid:11.11.0.1");
		results  = query.listCodeSystems(queryByExample);
		assertEquals(1, results.getCodingSchemeRenderingCount());
		
	}
	@Test
	public void testGetCodeSystemDetailsTest() {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		versionOrTag.setVersion("1.1");
		CodingScheme result = query.getCodeSystemDetails("Automobiles", versionOrTag);
		assertEquals("Automobiles", result.getCodingSchemeName());
		
		//null codingschemeName
		versionOrTag.setVersion("1.1");
		result = query.getCodeSystemDetails(null, versionOrTag);
		assertEquals(null, result);
		
	}
	@Test
	public void testListCodeSystemConcepts() {
		fail("");
	}
	@Test
	public void testGetConceptDetails() {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		versionOrTag.setVersion("1.1");
		Entity entity = query.getConceptDetails("Automobiles", versionOrTag, "C0001", "Automobiles");
		assertEquals("C0001",entity.getEntityCode());
		
	}
	@Test
	public void testListAssociationTypes() {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		versionOrTag.setVersion("1.1");
		List<SupportedAssociation> associationList = query.listAssociationTypes("Automobiles", versionOrTag);
		assertEquals(false, associationList.isEmpty());
		
	}
	@Test
	public void testGetAssociationTypeDetails() {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		versionOrTag.setVersion("1.1");
		AssociationEntity associationEntity = query.getAssociationTypeDetails("Automobiles", versionOrTag, "uses");
		assertEquals("uses", associationEntity.getEntityCode());
		
	}
	
}