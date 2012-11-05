/*
 *  HLTTutorial.java
 *  Copyright (c) 2007- , DERI, National University of Ireland, Galway.
 * Brian Davis, 12 December 2007 
*/

package com.nlp;

/**
 * 
 * @author bridav
 * 
 */

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.creole.Transducer;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;





public class NLPtest {

	public NLPtest() {
		try {
			initializeGate();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (GateException ex) {
			ex.printStackTrace();
		}
	}

	private final String fileSeparator = System.getProperty("file.separator");
	public static String newline =("\n");

	public static final String NS = "http://sw.deri.org/2007/12/fin/spec#"; 
	public static final String FID_DATA_ORG = "http://sw.deri.org/2007/12/fin/inst/organisations/";
	public static final String contextID="<http://sw.deri.org/2007/12/fin/sources/";
	public  String context;
	public Document doc;
	public static String contextData;
	public static boolean hasURIContext=false;
	private static int firstFile = 0;
	public static String filename;

	public SerialAnalyserController  _annieController;


	private void initializeGate() throws GateException, MalformedURLException {

		System.out.println("SYSENV: "+System.getenv("GATE_HOME"));

		System.setProperty("gate.home", System.getenv("GATE_HOME"));
		System.setProperty("gate.site.config", System.getProperty("gate.home")
				+ fileSeparator + "gate.xml");
		System.setProperty("gate.user.config", System.getProperty("gate.site.config"));

		Gate.init();






		// register plugin directories

		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getGateHome().toString() + fileSeparator
						+ "plugins" + fileSeparator + "ANNIE").toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(
				new File(Gate.getGateHome().toString() + fileSeparator
						+ "plugins" + fileSeparator + "Tools").toURI().toURL());

		//Gate.getCreoleRegister().registerDirectories(
		//	new File(Gate.getGateHome().toString() + fileSeparator
		//			+ "plugins" + fileSeparator + "Jape_Compiler").toURL());

		_annieController = (SerialAnalyserController) Factory
		.createResource("gate.creole.SerialAnalyserController", Factory
				.newFeatureMap(), Factory.newFeatureMap(), "ANNIE_"
				+ Gate.genSym());

		FeatureMap params= Factory.newFeatureMap();
		params.put("keepOriginalMarkupsAS",true);
		ProcessingResource pr = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR",
				params);
		_annieController.add(pr);
		System.err.println("Loaded: Document Reset PR");


		params = Factory.newFeatureMap();
		pr = (ProcessingResource) Factory.createResource(
				"gate.creole.tokeniser.SimpleTokeniser", params);

		_annieController.add(pr);
		System.err.println("Loaded: Tokeniser");

		params = Factory.newFeatureMap();
		pr = (ProcessingResource) Factory.createResource(
				"gate.creole.splitter.SentenceSplitter", params);
		_annieController.add(pr);
		System.err.println("Loaded: Sentence Splitter");

		params = Factory.newFeatureMap();
		params.put("baseSentenceAnnotationType", "Sentence");
		params.put("baseTokenAnnotationType", "Token");
		params.put("outputAnnotationType", "Token");
		pr = (ProcessingResource) Factory.createResource(
				"gate.creole.POSTagger", params);
		_annieController.add(pr);
		System.err.println("Loaded: POS tagger");




		params = Factory.newFeatureMap();
		URL u=new File("/nlpWorkspace/language resources/gazetteer/all/all.def").toURI().toURL();
		params.put("listsURL", u);
		DefaultGazetteer gazetteer = (DefaultGazetteer) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", params);
		_annieController.add(gazetteer);

		System.err.println("Loaded: Gazetteer");

		u = new File("/nlpWorkspace/language resources/jape/main.jape").toURL();
		params = Factory.newFeatureMap();
		params.put("grammarURL", u);
		Transducer relationExtractionTransducer = (Transducer)Factory
		.createResource("gate.creole.Transducer",params);
		_annieController.add(relationExtractionTransducer);

		System.err.println("Loaded: Relation Extraction JAPE grammars, using JAPE Transducer");







	}



	public static void main(String args[]) {

		NLPtest _tutorial=new NLPtest();	 
		try{
			//File contentDir = new File(args[0]);
			File contentDir = new File("C:\\Users\\jake\\Documents\\GitHub\\nlpWorkspace\\language resources\\corpus");
			File[] list = contentDir.listFiles();
			_tutorial.extractRelations(list);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}




	private void extractRelations(File[]files) throws Exception {
		// set corpus

		//CorpusController application(CorpusController)PersistenceManager.loadObjectFromFile(gappFile);
		Corpus corpus = Factory.newCorpus("New Corpus");
		_annieController.setCorpus(corpus);



		for(int i = firstFile; i < files.length; i++) {
			// load the document (using the specified encoding if one was given)
			File file = files[i];
			filename=file.getName();
			System.out.println("currentfile count is"+i);
			if(file.isFile() && file.length()!=0)   
			{
				System.out.print("Processing document " + file + "...");
				doc = Factory.newDocument(getContents(file));




				corpus.add(doc);
				_annieController.execute();
				corpus.clear();  
                

				AnnotationSet defaultAnnotSet = doc.getAnnotations();





				AnnotationSet companytoCompany1 = defaultAnnotSet.get("companytoCompanyRelation1");


				if(!companytoCompany1.isEmpty())
				{toRDF(companytoCompany1,  1);}
				AnnotationSet companytoCompany1a = defaultAnnotSet.get("companytoCompanyRelation1a");
				if(!companytoCompany1a.isEmpty())
				{toRDF(companytoCompany1a, 7);}
				AnnotationSet companytoCompany2 = defaultAnnotSet.get("companytoCompanyRelation2");
				if(!companytoCompany2.isEmpty())
				{toRDF(companytoCompany2, 2);}


				doc.cleanup();
				if (doc!=null) Factory.deleteResource(doc);

                System.gc();


			}//endif

			else{
				System.out.print("Document " + file + " is empty! Will ignore and continue to next file!"); 
			}
		}//endfor



		/** **************************CLEANUP************************************* */
		
		Factory.deleteResource(_annieController);
		_annieController=null;
		System.out.println("Processing Complete");
		
	}// execute


	/**
	 * Print to stdout the triple
	 * @param relationAnnotationSet
	 * @param content
	 */
	private void toRDF(AnnotationSet relationAnnotationSet, int code) {		
		//get the company

		Iterator annoIter = relationAnnotationSet.iterator();

		//iterate over annotations
		while(annoIter.hasNext()){
			//get the annotation
			Annotation a = (Annotation) annoIter.next();
			//
			//debug
			//System.out.println(a.toString());

			FeatureMap features = a.getFeatures();
			//get subject
			Annotation subjectAnn = (Annotation)features.get("Company1");
			String subject="empty";
			try {
				subject = doc.getContent().getContent(subjectAnn.getStartNode().getOffset(),subjectAnn.getEndNode().getOffset()).toString();
			} catch (InvalidOffsetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Annotation predicateAnno=null;
			String predicate="empty";
			switch(code)
			{
			case 1:
				predicateAnno = (Annotation)features.get("ownerOf");
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}

			case 2:
				predicateAnno = (Annotation)features.get("customerOf");
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}
			case 3:
				predicateAnno = (Annotation)features.get("supplierOf");        
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}
			case 4:
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}
				predicateAnno = (Annotation)features.get("lenderOf");
			case 5:
				predicateAnno = (Annotation)features.get("competitorOf");
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}    		
			case 7:
				predicateAnno = (Annotation)features.get("bidFor");
				if(predicateAnno!=null)
				{predicate = predicateAnno.getType();}    		

			}


			//System.out.println("\n#"+predicateAnno+"\n");
			//String predicate = predicateAnno.getType();

			//get objectAnnoyation
			Annotation objectAnn = (Annotation)features.get("Company2");
			String object="empty";
			try {
				object = doc.getContent().getContent(objectAnn.getStartNode().getOffset(),objectAnn.getEndNode().getOffset()).toString();
			} catch (InvalidOffsetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(subject.equals("empty")|| predicate.equals("empty")|| object.equals("empty"))
			{
				System.err.println("Null triples + "+subject+" "+predicate+" "+object );
			}

			if(subject.equals(object))
			{
				System.err.println("Duplicate triples + "+subject+" "+predicate+" "+object );
			}
			else
			{	
				String subjectID=getOrgNameSpace(subject);
				String predicateID=getPredNameSpace(predicate);
				String objectID=getOrgNameSpace(object);


				if(subjectID.equals("empty"))

				{System.err.println("Error:"+ subject);}

				else if(objectID.equals("empty"))
				{System.err.println(" Error could not find name:"+ object);}

				else{

					System.out.print(subjectID+" "+predicateID+" "+objectID+" ."+newline);

					System.out.println("**********EndofTriple***********");

				}	


			}




		}



		


	}

	static String getOrgNameSpace(String name){


		name = name.toLowerCase();

		name = stripNonAlphanumeric(name);



		return "<"+FID_DATA_ORG+ name+">";

	}

	private static String getPredNameSpace(String name){

		if (name.equals("ownerOfPassive"))
		{name="ownerOf";}

		name = name.toLowerCase();

		name = stripNonAlphanumeric(name);

		return "<"+NS+name+">";

	}



	private static String stripNonAlphanumeric(String s) { 

		return s.replaceAll("[^a-zA-Z0-9]", ""); 

	}






	/**
	 * Depending on the performance of Gate we should maybe think about a hack 
	 * 
	 * -big file: breaking into chunk of 1000 lines
	 * 
	 * 
	 * @param aFile
	 * @return
	 */
	public String getContents(File aFile) {
		// ...checks on aFile are elided
		StringBuffer contents = new StringBuffer();

		// declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			input = new BufferedReader(new FileReader(aFile));
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line MINUS
			 * the newline. it returns null only for the END of the stream. it
			 * returns an empty String if two newlines appear in a row.
			 */
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					// flush and close both "input" and its underlying
					// FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}



} // class

