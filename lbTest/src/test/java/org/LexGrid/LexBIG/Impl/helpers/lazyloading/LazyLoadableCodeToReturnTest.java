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
package org.LexGrid.LexBIG.Impl.helpers.lazyloading;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.Reader;

import junit.framework.TestCase;

import org.LexGrid.util.sql.lgTables.SQLTableConstants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.AbstractField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.lexevs.exceptions.MissingResourceException;

/**
 * The Class LazyLoadableCodeToReturnTest.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class LazyLoadableCodeToReturnTest extends TestCase {

    /** The code to return. */
    protected LazyLoadableCodeToReturn codeToReturn;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() {
        try {
            buildCodeToReturn();
            hydrate();
        } catch (Exception e) {
           fail(e.getMessage());
        }
    }
    
    /**
     * Hydrate.
     */
    protected void hydrate(){
        try {
            codeToReturn.hydrate();
        } catch (Exception e) {
           fail(e.getMessage());
        }
    }
    
    /**
     * Builds the code to return.
     * 
     * @throws Exception the exception
     */
    protected void buildCodeToReturn() throws Exception {
        LazyLoadableCodeToReturn codeToReturn = new TestLazyLoadableCodeToReturn();
        codeToReturn.setDocumentId(1);
        this.codeToReturn = codeToReturn;
    }

    /**
     * Test uri.
     */
    public void testUri(){
        assertTrue(codeToReturn.getUri().equals("cs"));
    }
    
    /**
     * Test entity description.
     */
    public void testEntityDescription(){
        assertTrue(codeToReturn.getEntityDescription().equals("description"));
    }
    
    /**
     * Test namespace.
     */
    public void testNamespace(){
        assertTrue(codeToReturn.getNamespace().equals("namespace"));
    }
    
    /**
     * Test entity id.
     */
    public void testEntityId(){
        assertTrue(codeToReturn.getCode().equals("id"));
    }
    
    /**
     * Test entity type.
     */
    public void testEntityType(){
        assertTrue(codeToReturn.getEntityTypes().length == 2);
        assertTrue(ArrayUtils.contains(codeToReturn.getEntityTypes(), "type1"));
        assertTrue(ArrayUtils.contains(codeToReturn.getEntityTypes(), "type2"));
    }
    
    
    /**
     * The Class TestField.
     * 
     * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
     */
    private class TestField extends AbstractField {

        /** The value. */
        private String value;
        
        /**
         * Instantiates a new test field.
         * 
         * @param name the name
         * @param value the value
         */
        public TestField(String name, String value){
            this.name = name;
            this.value = value;
            super.name = name;
        }
        
        /* (non-Javadoc)
         * @see org.apache.lucene.document.Fieldable#binaryValue()
         */
        public byte[] binaryValue() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.document.Fieldable#readerValue()
         */
        public Reader readerValue() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.document.Fieldable#stringValue()
         */
        public String stringValue() {
            return value;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.document.Fieldable#tokenStreamValue()
         */
        public TokenStream tokenStreamValue() {
            return null;
        }
    }
    
    /**
     * The Class TestSQLTableConstants.
     * 
     * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
     */
    private class TestSQLTableConstants extends SQLTableConstants {
 
        /**
         * Instantiates a new test sql table constants.
         * 
         * @param version the version
         * @param tablePrefix the table prefix
         */
        public TestSQLTableConstants(String version, String tablePrefix) {
            super(null, null);
        }
        
        /* (non-Javadoc)
         * @see org.LexGrid.util.sql.lgTables.SQLTableConstants#supports2009Model()
         */
        @Override
        public boolean supports2009Model(){
            return true;
        }    
    }   
    
    /**
     * The Class TestLazyLoadableCodeToReturn.
     * 
     * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
     */
    private class TestLazyLoadableCodeToReturn extends LazyLoadableCodeToReturn {

        /* (non-Javadoc)
         * @see org.LexGrid.LexBIG.Impl.helpers.lazyloading.LazyLoadableCodeToReturn#getIndexReader()
         */
        @Override
        protected Document buildDocument(){
            Document doc = new Document();
            doc.add(new TestField(SQLTableConstants.TBLCOL_ENTITYCODE, "id"));  
            doc.add(new TestField("codingSchemeId","cs"));  
            doc.add(new TestField(SQLTableConstants.TBLCOL_ENTITYDESCRIPTION,"description"));  
            doc.add(new TestField(SQLTableConstants.TBLCOL_ENTITYCODENAMESPACE,"namespace"));  
            doc.add(new TestField("entityType","type1"));
            doc.add(new TestField("entityType","type2"));
            
            return doc;
        }  
    }
}
