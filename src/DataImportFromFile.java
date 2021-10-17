import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DataImportFromFile {
//	private static SQLConnection con;
	private static DatabaseEditor dbe;
	private static File file;
	private static ArrayList<Edit> edits = new ArrayList<Edit>();
	private static ArrayList<EditHistorical> historicalEdits = new ArrayList<EditHistorical>();
	private static ArrayList<GeneralEdit> allEdits = new ArrayList<GeneralEdit>();
//	private static ArrayList<FastImport> fastEdits = new ArrayList<FastImport>(); //attempts to minimize going to wikipedia
	private boolean firstTime = true;
	private static ArrayList<String> filesToCheck = new ArrayList<String>();
	private boolean listFile = false;
//	private boolean usedFastImport = false;
	
	public DataImportFromFile(DatabaseEditor editor) {
		dbe = editor;
		file = null;
	}
	public void setFile(File file) {
		DataImportFromFile.file = file;
	}
	public void readFile() {
		BufferedReader reader = null;
		try {//rawr
			Gson gson = new Gson();
			String line = "";
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				try { //I have not learned patterns, or wrappers. So this will be hardcoded for the moment
					if(file.toString().contains("historical")) {
						historicalEdits.add(gson.fromJson(line, EditHistorical.class));
						allEdits.add(gson.fromJson(line, EditHistorical.class));
					} 
//					else if(file.toString().contains("fast")) {
//						//do something special, probably just fast import if possible
//						FastImport ft = gson.fromJson(line, FastImport.class);
//						fastEdits.add(ft);
//						usedFastImport = true;
//						allEdits.addAll(ft.allEdits);
//					}
					else if(file.toString().contains("list")) {
						listFile=true;
						filesToCheck.add(line);
					}
					else {
						edits.add(gson.fromJson(line, Edit.class));		
						allEdits.add(gson.fromJson(line, Edit.class));
					}
				} catch (JsonSyntaxException e) {
					JOptionPane.showMessageDialog(null, "Invalid JSON file");
					e.printStackTrace();
					break;
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not find that file");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Issue with reading that file");
		} finally {
			if(reader!=null) {
				try {
					reader.close();
					System.out.println("Closing reader");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//Now call the insertFileContents
		if(!listFile) { //if it was not a list, do that
			this.insertFileContents();			
		} else if(firstTime){ //if it was a list, go through and call each add then do the insert
			this.handleMultipleFiles();
		}
	}
	
	private void handleMultipleFiles() {
		firstTime = false;
		for(String s: filesToCheck) {
			file = new File(s);
			this.readFile(); //will read the file, then do it again with the next read file, etc.
		}
		filesToCheck.clear();
		firstTime = true; //weird, but need to set it back
		this.insertFileContents();
	}
	private void insertFileContents() {
		ArrayList<Integer> results = new ArrayList<Integer>();
		Selenium selenium = new Selenium();
		if(firstTime) {dbe.generateOrganizations(); firstTime=false;}
		//could reduce duplicate work a bit further
		for(GeneralEdit e: allEdits) {
			selenium.fillInMissingInfo(e);
			selenium.findSuperCategories(e); //probably going to be slow.......
			results.add(dbe.addAutoEdit(e)); //should do edit before the categories as edit can create the page if needed
			dbe.addCategories(e);
	    }
		int pageProblems = 0;
		int editorProblems = 0;
		int editProblems = 0;
		int successful = 0;
		int unknownProblems = 0;
		for(int returnValue: results) {
			if(returnValue==1) {
				pageProblems++;
			} else if(returnValue==2) {
				editorProblems++;
			} else if(returnValue==3) {
				editProblems++;
			} else if(returnValue == 0) {
				successful++;
			}	else {
				unknownProblems++;
			}
		}
		JOptionPane.showMessageDialog(null, successful + " successful import(s). Imports that could not be executed: " 
				+ pageProblems + " invalid page(s) given, "+ 
				editorProblems + " problem(s) with editor, " + editProblems +
				" problem(s) with edit, " + unknownProblems + " unknown problem(s).\n"
				+ "Times called Wikipedia to retrieve missing information " + selenium.getNumberOfWikipediaCalls() + ".");
		selenium.quiteSelenium();
//		if(!usedFastImport) {
//			Gson gson = new Gson();
//			String writeFile = "src/newData.json";
//			Writer writer;
//			try {
//				writer = new BufferedWriter(new FileWriter(writeFile, true)); //true makes it appendable
//				gson.toJson(allEdits, writer);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
////			not really anything to do here
//			} 			
//		}
		allEdits.clear(); //now should be inserted
	}
}
