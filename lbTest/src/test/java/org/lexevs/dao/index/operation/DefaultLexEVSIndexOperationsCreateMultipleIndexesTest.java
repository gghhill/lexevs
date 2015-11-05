package org.lexevs.dao.index.operation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.types.ProcessState;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Impl.loaders.LexGridMultiLoaderImpl;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lexevs.locator.LexEvsServiceLocator;
import org.lexevs.registry.model.RegistryEntry;
import org.lexevs.registry.service.Registry;

public class DefaultLexEVSIndexOperationsCreateMultipleIndexesTest {
	static List<AbsoluteCodingSchemeVersionReference> references = new ArrayList<AbsoluteCodingSchemeVersionReference>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LexBIGServiceManager lbsm = ServiceHolder.instance().getLexBIGService()
				.getServiceManager(null);

		LexGridMultiLoaderImpl loader = (LexGridMultiLoaderImpl) lbsm
				.getLoader("LexGrid_Loader");

		loader.load(new File("resources/testData/Automobiles.xml").toURI(),
				true, true);

		while (loader.getStatus().getEndTime() == null) {
			Thread.sleep(1000);
		}
		
		LexGridMultiLoaderImpl GMPloader = (LexGridMultiLoaderImpl) lbsm
				.getLoader("LexGrid_Loader");

		GMPloader.load(new File("resources/testData/German_Made_Parts.xml").toURI(),
				true, true);

		while (GMPloader.getStatus().getEndTime() == null) {
			Thread.sleep(1000);
		}
		
		LexGridMultiLoaderImpl A2loader = (LexGridMultiLoaderImpl) lbsm
				.getLoader("LexGrid_Loader");

		A2loader.load(new File("resources/testData/Automobiles2.xml").toURI(),
				true, true);

		while (A2loader.getStatus().getEndTime() == null) {
			Thread.sleep(1000);
		}
		assertTrue(loader.getStatus().getState().equals(ProcessState.COMPLETED));
		assertFalse(loader.getStatus().getErrorsLogged().booleanValue());
		Registry registry = LexEvsServiceLocator.getInstance().getRegistry();
		List<RegistryEntry> entries = registry.getAllRegistryEntries();
		for(RegistryEntry re: entries){
			AbsoluteCodingSchemeVersionReference ref = new AbsoluteCodingSchemeVersionReference();
			ref.setCodingSchemeURN(re.getResourceUri());
			ref.setCodingSchemeVersion(re.getResourceVersion());
			references.add(ref);
			LexEvsServiceLocator.getInstance().getIndexServiceManager().getEntityIndexService().dropIndex(ref);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		LexBIGServiceManager lbsm = ServiceHolder.instance().getLexBIGService()
				.getServiceManager(null);
		for(AbsoluteCodingSchemeVersionReference ref: references){
			lbsm.removeCodingSchemeVersion(ref);
		}

	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testMultipleIndexCreation() throws LBParameterException {
		int count = 0;
		DefaultLexEvsIndexOperations ops = (DefaultLexEvsIndexOperations) LexEvsServiceLocator.getInstance().getLexEvsIndexOperations();
		assertFalse(ops.getConcurrentMetaData().getCodingSchemeList().size() > 0);
		for(AbsoluteCodingSchemeVersionReference reference: references){
		assertFalse(ops.doesIndexExist(reference));
		LexEvsServiceLocator.getInstance().getLexEvsIndexOperations().
		registerCodingSchemeEntityIndex(reference.getCodingSchemeURN(), reference.getCodingSchemeVersion());
		assertNotNull(ops.getConcurrentMetaData().getCodingSchemeList().get(count));
		assertTrue(ops.doesIndexExist(reference));
		count++;
		}
	}

}
