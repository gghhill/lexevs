package org.lexevs.dao.index.lucene.v2010.metadata;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.MetadataPropertyList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.MetadataProperty;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.lexevs.dao.index.access.metadata.MetadataDao;
import org.lexevs.dao.index.lucenesupport.LuceneIndexTemplate;
import org.lexevs.dao.index.lucenesupport.BaseLuceneIndexTemplate.IndexReaderCallback;
import org.lexevs.dao.index.metadata.BaseMetaDataLoader;

public class LuceneMetadataDao implements MetadataDao {
	
	private BaseMetaDataLoader baseMetaDataLoader;
	
	private LuceneIndexTemplate luceneIndexTemplate;
	
	@Override
	public void addDocuments(String codingSchemeUri, String version,
			List<Document> documents, Analyzer analyzer) {
		this.luceneIndexTemplate.addDocuments(documents, analyzer);
	}

	@Override
	public AbsoluteCodingSchemeVersionReferenceList listCodingSchemes() {
	       AbsoluteCodingSchemeVersionReferenceList result = new AbsoluteCodingSchemeVersionReferenceList();

           try {
        	   TermEnum te = luceneIndexTemplate.executeInIndexReader(new IndexReaderCallback<TermEnum>() {

				@Override
				public TermEnum doInIndexReader(IndexReader indexReader)
						throws Exception {
					return indexReader.terms(new Term("codingSchemeNameVersion", ""));
				}  
        	   });

			   boolean hasNext = true;
			   while (hasNext && te.term().field().equals("codingSchemeNameVersion")) {
			       Query temp = new TermQuery(new Term(te.term().field(), te.term().text()));

			       List<ScoreDoc> d = this.luceneIndexTemplate.search(temp, null);
			       if (d.size() > 0) {

			           ScoreDoc doc = d.get(0);
			           AbsoluteCodingSchemeVersionReference acsvr = new AbsoluteCodingSchemeVersionReference();
			           
			           Document document = luceneIndexTemplate.getDocumentById(doc.doc);
			           acsvr.setCodingSchemeURN(document.get("codingSchemeRegisteredName"));
			           acsvr.setCodingSchemeVersion(document.get("codingSchemeVersion"));

			           result.addAbsoluteCodingSchemeVersionReference(acsvr);
			       }
			       hasNext = te.next();
			   }
			   te.close();
			   
			   return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public void removeMetadata(String codingSchemeUri, String version) {
		this.luceneIndexTemplate.removeDocuments(
				new Term("codingSchemeNameVersion",
						codingSchemeUri
						+ BaseMetaDataLoader.CONCATINATED_VALUE_SPLIT_TOKEN + version));
	}

	@Override
	public MetadataPropertyList search(Query query) {
		
		 List<ScoreDoc> docs = this.luceneIndexTemplate.search(query, null);
	
		 MetadataPropertyList mdpl = new MetadataPropertyList();

         // assemble the result object
         for (ScoreDoc doc : docs) {
        	 Document d = luceneIndexTemplate.getDocumentById(doc.doc);
        	 
             MetadataProperty curr = new MetadataProperty();
             curr.setCodingSchemeURI(d.get("codingSchemeRegisteredName"));
             curr.setCodingSchemeVersion(d.get("codingSchemeVersion"));
             curr.setName(d.get("propertyName"));
             curr.setValue(d.get("propertyValue"));

             String temp = d.get("parentContainers");
             curr.setContext(temp.split(BaseMetaDataLoader.STRING_TOKEINZER_TOKEN));

             mdpl.addMetadataProperty(curr);
         }
         
         return mdpl;

	}

	public void setBaseMetaDataLoader(BaseMetaDataLoader baseMetaDataLoader) {
		this.baseMetaDataLoader = baseMetaDataLoader;
	}

	public BaseMetaDataLoader getBaseMetaDataLoader() {
		return baseMetaDataLoader;
	}

	public void setLuceneIndexTemplate(LuceneIndexTemplate luceneIndexTemplate) {
		this.luceneIndexTemplate = luceneIndexTemplate;
	}

	public LuceneIndexTemplate getLuceneIndexTemplate() {
		return luceneIndexTemplate;
	}
	
	
}
