import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DateFormatter;

import com.microsoft.sqlserver.jdbc.SQLServerException;

public class DatabaseEditor {//Based off RestaurantService
	private static Vector<Object> header = new Vector<Object>();
	private SQLConnection dbService = null;
	
	public DatabaseEditor(SQLConnection dbService) {
		this.dbService = dbService;
	}
	
	public static boolean validateInputString(String s) { //makes sure it starts with a letter
		 	Pattern pattern = Pattern.compile(s.charAt(0)+"", Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher("abcdefghijklmnopqrstuvwxz");
		    boolean startsWithLetter = matcher.find();
		    return startsWithLetter;
		
	}
	
	public DefaultTableModel generateTable(SQLConnection c, final int idx, String filter, String filterBy) {
		Statement s;
		PreparedStatement prepstmt;
		ResultSet rs;
		try {
			s = c.getConnection().createStatement();
		} catch (SQLException e1) {
			System.out.println("did not connect for views");
			e1.printStackTrace();
			return null;
		}
		String query = "SELECT * FROM ";
		switch(idx) {
		case 0: query += "[Account]";
				break;
		case 1: query += "[Category]";
				break;
		case 2: query += "[Editor]";
				break;
		case 3: query += "[Edits]";
				break;
		case 4: query += "[In]";
				break;
		case 5: query += "[IP_Address]";
				break;
		case 6: query += "[Organization]";
				break;
		case 7: query += "[Page]";
				break;
		default: query = null;
				break;
		}
			
		if (filter != null && filterBy != null) {
			query = query + " WHERE " + filterBy + " LIKE ?";
		}
		
		
		try {
			 prepstmt = dbService.getConnection().prepareStatement(query);
			 if (filter != null && filterBy != null) {
				 prepstmt.setString(1, filter + "%");
			 }
			 rs = prepstmt.executeQuery(); 
			 ResultSetMetaData metaData = rs.getMetaData();
			 System.out.println(rs);
			 Vector<Object> columnNames = new Vector<Object>();
			 int columnCount = metaData.getColumnCount();
			 for (int column = 1; column <= columnCount; column++) {
			        columnNames.add(metaData.getColumnName(column));
			    }
			 Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			 data.add(columnNames);
			    while (rs.next()) {
			        Vector<Object> vector = new Vector<Object>();
			        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			            vector.add(rs.getObject(columnIndex));
			        }
			        data.add(vector);
			    }
			   
			
			 return new DefaultTableModel(data, columnNames) {
				private static final long serialVersionUID = 8954594004483609890L;

				@Override
				 public boolean isCellEditable(int row, int column) {//Prevents editing cells that could damage data integrity or update feature
					 boolean editable = true;
					 if (row == 0) {
						 return false;
					 }
					 switch (idx) {
					 case 0: if(column == 0) editable = false;
					 break;
					 case 1: editable = false;
					 break;
					 case 2: if(column == 0 || column == 3) editable = false;
					 break;
					 case 3: if(column == 0) editable = false;
					 break;
					 case 4: editable = false;
					 break;
					 case 5: editable = false;
					 break;
					 case 6: if(column == 0) editable = false;
					 break;
					 case 7: if(column == 0) editable = false;
					 break;
					 default: break;
					 }
					 return editable;
				 }
			 };
			 
		} catch (SQLException e) {
			System.out.println("fail");
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public DefaultTableModel generateView(SQLConnection c, int idx, String filter, String filterBy) {
		Statement s;
		PreparedStatement prepstmt;
		ResultSet rs;
		try {
			s = c.getConnection().createStatement();
		} catch (SQLException e1) {
			System.out.println("did not connect for views");
			e1.printStackTrace();
			return null;
		}
		String query = "SELECT * FROM ";
		switch(idx) {
		case 0: query += "EditorView";
				break;
		case 1: query += "EditsView";
				break;
		case 2: query += "OrganizationView";
				break;
		case 3: query += "CategoryView";
				break;
		case 4: query += "AccountView";
				break;
		case 5: query += "IP_AddressView";
				break;
		default: query = null;
				break;
		}
			
		if (filter != null && filterBy != null) {
			query = query + " WHERE " + filterBy + " LIKE ?";
		}
		
		
		try {
			 prepstmt = dbService.getConnection().prepareStatement(query);
			 if (filter != null && filterBy != null) {
				 prepstmt.setString(1, filter + "%");
			 }
			 rs = prepstmt.executeQuery(); 
			 ResultSetMetaData metaData = rs.getMetaData();
			 Vector<Object> columnNames = new Vector<Object>();
			 int columnCount = metaData.getColumnCount();
			 for (int column = 1; column <= columnCount; column++) {
			        columnNames.add(metaData.getColumnName(column));
			    }
			 Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			 data.add(columnNames);
			    while (rs.next()) {
			        Vector<Object> vector = new Vector<Object>();
			        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			            vector.add(rs.getObject(columnIndex));
			        }
			        data.add(vector);
			    }
			   
			header = columnNames;
			 return new DefaultTableModel(data, columnNames);//columnNames
			 
		} catch (SQLException e) {
			System.out.println("fail");
			e.printStackTrace();
			return null;
		}
		
		
	}

	
	public boolean addAccount(String username, String standing, boolean isRobot, String organizationName, String creationDate) {
		CallableStatement cs = null;
		int successfulInsert = -1;
		int returnValue = 5;
		
		//TODO: regex for username, organization
		if (creationDate != null) {
			creationDate = creationDate.strip();//Date format checks
			creationDate = creationDate.replace("-", "");
			creationDate = creationDate.replace("/", "");
			try {
				Integer.parseInt(creationDate);
			}
			catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "ERROR: Date doesn't appear to be a number");
				JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
				return false;
			}
			if(creationDate.length() != 8 || Integer.parseInt(creationDate.substring(4, 6)) > 12 || Integer.parseInt(creationDate.substring(6,8)) > 31) {//Incorrect date format check
				JOptionPane.showMessageDialog(null, "ERROR: Invalid Date Format");
				JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
				return false;
			}
		}
		
		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_Account(?,?,?,?,?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.registerOutParameter(7, java.sql.Types.INTEGER); //Needed to prevent breaking
			cs.setString(2, username);
			cs.setString(3, standing);
			if (isRobot){//boolean from checkBox
				cs.setInt(4,1);
			}
			else {
				cs.setInt(4, 0);
			}
			cs.setString(5, organizationName);
			cs.setString(6, creationDate);

			try {
				successfulInsert = cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			
			if(successfulInsert >= 0) {
				JOptionPane.showMessageDialog(null, "Successfully Added");
				return true;
			}
			returnValue = cs.getInt(1);
			if (returnValue == 1) {
				JOptionPane.showMessageDialog(null, "ERROR: Username cannot be null");
			}
			else if(returnValue == 2 ) {
				JOptionPane.showMessageDialog(null, "ERROR: Username already exists");
			}
			else if(returnValue == 3 ) {
				JOptionPane.showMessageDialog(null, "ERROR: Date cannot be null");
			}
			else if(returnValue == 4) {
				JOptionPane.showMessageDialog(null, "ERROR: Impossible Date Value");
			}
			
			cs.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error");
			System.out.println("Failed to call procedure");
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
		
		return false;
	}
	
	public boolean addOrganization(String name, String lean)
	{
		if(lean != null && !lean.isBlank()) {
		if(!validateInputString(lean))
		{
			JOptionPane.showMessageDialog(null, "ERROR: Invalid political lean, must start with a letter");
			return false;
		}
		}
		else {
			lean = null;
		}
		CallableStatement cs = null;
		int successfulInsert = -1;
		int returnValue = 5;
		
		
		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_Organization(?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, name);
			cs.setString(3, lean);

			try {
				successfulInsert = cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			
			if(successfulInsert >= 0) {
				JOptionPane.showMessageDialog(null, "Successfully Added");
				return true;
			}
			returnValue = cs.getInt(1);
			if (returnValue == 1) {
				JOptionPane.showMessageDialog(null, "ERROR: Organization name must be inserted");
			}
			if(returnValue ==2 ) {
				JOptionPane.showMessageDialog(null, "ERROR: Organization name already exists");
			}
			//Seems to really want to break
			cs.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error");
			System.out.println("Failed to call procedure");
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
		
		return false;
				
	}
	
	public boolean addPage(String url, String name)
	{
		CallableStatement cs = null;
		int successfulInsert = -1;
		int returnValue = 5;
		
		
		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_page(?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, url);
			cs.setString(3, name);

			try {
				successfulInsert = cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			
			if(successfulInsert >= 0) {
				JOptionPane.showMessageDialog(null, "Successfully Added");
				return true;
			}
			returnValue = cs.getInt(1);
			if (returnValue == 1) {
				JOptionPane.showMessageDialog(null, "ERROR: URL must be inserted");
			}
			if(returnValue ==2 ) {
				JOptionPane.showMessageDialog(null, "ERROR: Page name must be inserted");
			}
			if (returnValue == 3) {
				JOptionPane.showMessageDialog(null, "ERROR: URL already exists");
			}
			//Seems to really want to break
			cs.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error");
			System.out.println("Failed to call procedure");
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
		
		return false;
				
	}
	
	
	public boolean addCategory(String catname, String supercat)
	{
		CallableStatement cs = null;
		int successfulInsert = -1;
		int returnValue = 5;
		
		if(validateInputString(catname))
			System.out.println("yesss it worked");
		else
			System.out.println("cap");
		
		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_Category(?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, catname);
			cs.setString(3, supercat);

			try {
				successfulInsert = cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			
			if(successfulInsert >= 0) {
				JOptionPane.showMessageDialog(null, "Successfully Added");
				return true;
			}
			returnValue = cs.getInt(1);
			if (returnValue == 1) {
				JOptionPane.showMessageDialog(null, "ERROR: Category must be inserted");
			}
			if(returnValue ==2 ) {
				JOptionPane.showMessageDialog(null, "ERROR: Category already exists");
			}
			if (returnValue == 3) {
				JOptionPane.showMessageDialog(null, "ERROR: Invalid Super Category");
			}
			//Seems to really want to break
			cs.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error");
			System.out.println("Failed to call procedure");
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
		
		return false;
				
	}
	
	
	public boolean addIPAddress(String standing, boolean isRobot, String OrganizationName,String IPv4) {
		CallableStatement cs = null;
		int successfulInsert = -1;
		int returnValue = 5;
		//TODO: regex for 12 digit pattern
		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_IP_Address(?,?,?,?,?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.registerOutParameter(7, java.sql.Types.INTEGER); //may be needed to stop errors
			cs.setBytes(2, null);//Currently ignores non IPv4
			cs.setString(3, standing);
			if (isRobot){
				cs.setInt(4,1);
			}
			else {
				cs.setInt(4, 0);
			}
			cs.setString(5, OrganizationName);
			cs.setString(6, IPv4);

			try {
				successfulInsert = cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			
			if(successfulInsert >= 0) {
				JOptionPane.showMessageDialog(null, "Successfully Added");
				return true;
			}
			returnValue = cs.getInt(1);
			if (returnValue == 1) {
				JOptionPane.showMessageDialog(null, "ERROR: IP cannot be null");
			}
			if(returnValue ==2 ) {
				JOptionPane.showMessageDialog(null, "ERROR: IP already exists");
			}
			//Seems to really want to break
			cs.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error");
			System.out.println("Failed to call procedure");
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Unsuccessful Addition");
		
		return false;
	}
	
	public int addAutoEdit(GeneralEdit edit) {
		System.out.println("Going to call procedure with " + edit.toString());
		CallableStatement cs = null;
		//TODO does not behave like transaction; seems we are approved to just leave them as is, for I made the problem basically impossible to happen
		try {
			//Simpler version, Username (or IPv4String), pageUrl, pageName, editUrl, comment, anonymous, dateTime. Organization. 
			//creationDate, isRobot, standing
			//Supposably prepare call is expensive to do, and if possible, re-using would be nice
			cs = dbService.getConnection().prepareCall("{? = call automated_insert(?,?,?,?,?,?,?,?,?,?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, edit.getUser());
			cs.setString(3, edit.getPageUrl());
			cs.setString(4, edit.getPageName());
			cs.setString(5, edit.getUrl());
			cs.setString(6, edit.getComment());
			//these two very based on what type of edit it is
			int org = 0;
			switch(edit.type) {
			case HISTORICAL:
				cs.setInt(7, 1); //only anonymouse historical edits exist
				org = edit.inRange();
				cs.setNull(10, java.sql.Types.DATE); //no creation date for ip addresses
				cs.setNull(11, java.sql.Types.INTEGER); //no data on if they are a robot or not
//				cs.setNull(12, java.sql.Types.VARCHAR); //no status for ip that I know of
				cs.setString(12, "Good"); //ARBITRARILY DECIDE DEFUALT STATUS IS GOOD
				break;
			case NEW:
				Edit newEdit = (Edit) edit;
				cs.setInt(7, newEdit.getAnonymouse()); //could be anonymous or not, Edit class auto converts boolean to int
				if(newEdit.getAnonymouse()==1) { //is anonymous
					org = edit.inRange();
					cs.setNull(10, java.sql.Types.DATE);
					cs.setString(12, "Good"); //ARBITRARILY DECIDE DEFUALT STATUS IS GOOD
				} else { //not means it is an account
					cs.setString(10, newEdit.getCreationDate());
					if(newEdit.getStatus()!=null) {
						cs.setString(12, newEdit.getStatus()); //only if there is something do we put a status
					} else {
						cs.setString(12, "Good"); //ARBITRARILY DECIDE DEFUALT STATUS IS GOOD
					}
				}
				cs.setInt(11, newEdit.isRobot()); //for these can figure out if they are a robot or not
				break;
			default: //Never happens
				break;
			}
			//the timeStamp may or may not exist depending on if selenium could retrive the missing data
			if(edit.getTimeStamp()!=null) { cs.setString(8, edit.getTimeStamp());}
			else {cs.setNull(8, java.sql.Types.VARCHAR);}
			//organization is semi-hardcoded; however, this is really the only way to do it without creating a config file. Which
			//would exceed the scope of the current project, as we only currently care about a set of static ip addresses and accounts.
			if(org==1) {
				cs.setString(9, "United States House of Representatives");
			} else if(org==2) {
				cs.setString(9, "United States Senate");
			} else {
				cs.setString(9, "Unknown");
			}
			
			
			cs.execute();
			return cs.getInt(1);			
		} catch (SQLException e) {
//			JOptionPane.showMessageDialog(null, "Error");
//			System.out.println("Failed to call procedure; not showing stack trace to prevent spam at the moment");
//			e.printStackTrace();
		}
//		JOptionPane.showMessageDialog(null, "Unsuccessful Edit Import");
		return -1; //unknown error then
	}
	
	public void generateOrganizations() {
//		CallableStatement cs = dbService.getConnection().prepareCall("{? = insert_Organization")
		addQuickOrganization("United States House of Representatives", "LW");
		addQuickOrganization("United States Senate", "RW");
		addQuickOrganization("Unknown", "NO");
	}
	
	public void addQuickOrganization(String name, String lean)
	{ //this is for hardCoded organizations only! Just to not send messages to console
		CallableStatement cs = null;		
		try {
			cs = dbService.getConnection().prepareCall("{? = call insert_Organization(?,?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, name);
			cs.setString(3, lean);
			try {
				cs.executeUpdate();
			}
			catch (SQLServerException e) {
			}
			cs.close();	
		} catch (SQLException e) {
		}				
	}
	
	public int addCategories(GeneralEdit edit) {
		
		boolean problem = false;
		if(edit.getCategories() == null) {
			return 0;
//			//need to autoGenerate the categories.
//			Selenium selenium = new Selenium();
//			selenium.fillInMissingInfo(edit);
//			//if there are still no categories, return null
//			if(edit.getCategories() == null) { return 0;}
		}
		//going to do up to three calls!
//		for(String superCategory: edit.getSup)
		HashMap<String, String> superCats= edit.getSuperCategories();
		for(String category: edit.getCategories()) {
			//check if there is a superCategory, and insert that first
			if(superCats!=null) {
				String superCat = superCats.get(category);
				if(superCat!=null) {
					CallableStatement cs = null;
					try {
						cs = dbService.getConnection().prepareCall("{? = call insert_category(?,?)}");
						cs.registerOutParameter(1, java.sql.Types.INTEGER);
						cs.setString(2, superCat);
						cs.setNull(3, java.sql.Types.VARCHAR); //no super category for a super category
						
						cs.execute();
						if(cs.getInt(1)!=0) { //check error value
							problem = true;
						}
					} catch (SQLException e) {
						problem = true; //any problem marks entire thing as a problem
					}
					//now insert the category with the superCategory
					try {
						cs = dbService.getConnection().prepareCall("{? = call insert_category(?,?)}");
						cs.registerOutParameter(1, java.sql.Types.INTEGER);
						cs.setString(2, category);
						cs.setString(3, superCat);
						
						cs.execute();
						if(cs.getInt(1)!=0) {
							problem = true;
						}
					} catch (SQLException e) {
						problem = true;
					}
				}
			}
			CallableStatement cs = null; //In will auto generate any missing categories if they were not done prior
			try {
				cs = dbService.getConnection().prepareCall("{? = call insert_in(?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				cs.setString(2, edit.getPageUrl());
				cs.setString(3, category);
				
				cs.execute();
				if(cs.getInt(1)!=0) { //any problem marks the entire thing as a problem
					problem = true; //the issues are 1 URL null 
					//2 categoryName null, 3 page not exist, 
					//4 invalid category, 5 already in category
				}
			} catch (SQLException e) { //any problem marks the entire thing as a problem
				problem = true;
//				e.printStackTrace(); not printing right now to reduce spam
			}
		}
		return problem ? 1: 0;
	}
	

	public void updateTable(TableModel model, LinkedHashSet<Integer> rowsEdited, int idx) {
		int returnValue = -1;
		CallableStatement cs = null;
		boolean atLeastOneUpdate = false;
		String errorMsgRow = null;
		String errorMsgText = null;
		if(idx == 0){
			try {
				cs = dbService.getConnection().prepareCall("{? = call update_Account(?,?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				for(int row: rowsEdited) {
					
					Date rowDate = null;
					String creationDate = null;
					errorMsgRow = "At row starting with: " + model.getValueAt(row, 0).toString();
					errorMsgText = null;
					
					cs.setInt(2, (Integer)model.getValueAt(row, 0));
					
					try {
						rowDate = (Date) model.getValueAt(row, 1);
						if(rowDate == null) {
							JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Date has invalid format");
							continue;
						}
						creationDate =  rowDate.toString();
					
					}
					catch(ClassCastException e) {
						creationDate = (String) model.getValueAt(row, 1);
					}
					if (creationDate != null) {
						creationDate = creationDate.strip();//Date format checks
						creationDate = creationDate.replace("-", "");
						creationDate = creationDate.replace("/", "");
						try {
							Integer.parseInt(creationDate);
						}
						catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Date has invalid format");
							continue;
							
						}
						if(creationDate.length() != 8 || Integer.parseInt(creationDate.substring(4, 6)) > 12 || Integer.parseInt(creationDate.substring(6,8)) > 31) {//Incorrect date format check
							JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Date has invalid format");
							continue;
						}
					}
				
					cs.setString(3, creationDate);
					cs.setString(4, (String)model.getValueAt(row, 2));
					System.out.println("ID: " + (Integer)model.getValueAt(row, 0) + "\tCreationDate: " + creationDate + "\tUsername: " + (String)model.getValueAt(row, 2));
					try {
						cs.executeUpdate();
					}
					catch(SQLException e) {
					}
					returnValue = cs.getInt(1);
					if(returnValue != 0) {
						if (returnValue == 3) {
							errorMsgText = "Username already exists";
						}
						else {
							errorMsgText = "Update Failure";
						}
						JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + errorMsgText);
					}
					else {
						atLeastOneUpdate = true;
					}
				}
				
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error");
				System.out.println("fail");
			}
			if(atLeastOneUpdate) JOptionPane.showMessageDialog(null, "Update Applied");
			rowsEdited.clear();
			return;
		}
		else if (idx == 1) {
			return;
		}
		
		else if(idx == 2) {	//update Editor table
			try {
				cs = dbService.getConnection().prepareCall("{? = call update_Editor(?,?,?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
			
			for(int row: rowsEdited) {
				errorMsgRow = "At row starting with: " + model.getValueAt(row, 0).toString();
				errorMsgText = null;

				cs.setInt(2, (Integer)model.getValueAt(row, 0));
				
				String secondObject = (String) model.getValueAt(row, 1);
				if (secondObject == null || secondObject.isBlank()) {
					cs.setString(3, null);
				}
				else {
					cs.setString(3, secondObject);
				}
				
				
				String thirdObject = null;
				if (model.getValueAt(row, 2) == null ) {
					cs.setString(4, null);
				}
				else {
					if (model.getValueAt(row, 2) instanceof Boolean) {
						thirdObject = model.getValueAt(row, 2).toString();
					}
					else {
						thirdObject = (String) model.getValueAt(row, 2);
					}
					if (thirdObject.isBlank()) {
						cs.setString(4, null);
					}
					else if (thirdObject.equalsIgnoreCase("true")){
						cs.setBoolean(4, true);
					}
					else if (thirdObject.equalsIgnoreCase("false")){
						cs.setBoolean(4, false);
					}
					else {
						cs.setString(4, null);
					}
				}
				String fourthObject = (String) model.getValueAt(row, 4);
				if (fourthObject == null || fourthObject.isBlank()) {
					cs.setString(5, null);
				}
				else {
					cs.setString(5, (String) fourthObject);
				}
				try {
					cs.executeUpdate();
				}
				catch(SQLException e) {
				}
				returnValue = cs.getInt(1);
				if(returnValue != 0) {
					if (returnValue == 1) {
						errorMsgText = "Organization doesn't exist";
					}
					else {
						errorMsgText = "Update Failure";
					}
					JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + errorMsgText);
				}
				else {
					atLeastOneUpdate = true;
				}
				}
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error");
				System.out.println("fail");
				e.printStackTrace();
			}
			if (atLeastOneUpdate) JOptionPane.showMessageDialog(null, "Update Applied");
			rowsEdited.clear();
		}
		
		if(idx == 3) {//Edits	
			try {
				cs = dbService.getConnection().prepareCall("{? = call update_Edits(?,?,?,?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
			
			for(int row: rowsEdited) {
				errorMsgRow = "At row starting with: " + model.getValueAt(row, 0).toString();
				errorMsgText = null;

				cs.setString(2, (String) model.getValueAt(row, 0));
				
				String pageURL = (String) model.getValueAt(row, 1);
				if (pageURL == null || pageURL.isBlank()) {
					cs.setString(3, null);
				}
				else {
					cs.setString(3, pageURL);
				}
				
				String editorID;
				if(model.getValueAt(row, 2) instanceof Integer) {
					editorID = ((Integer) model.getValueAt(row, 2)).toString();
				}
				else {
					editorID = (String) model.getValueAt(row, 2);
				}
				if (editorID == null) {
					cs.setString(4, null);
				}
				else {
					try {
						cs.setInt(4,Integer.parseInt(editorID));
					}
					catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "EditorID has invalid format");
						continue;
				
					}
				}
				
				
				String fourthObject = (String) model.getValueAt(row, 3);
				if (fourthObject == null || fourthObject.isBlank()) {
					cs.setString(5, null);
				}
				else {
					cs.setString(5, fourthObject);
				}
//				DateFormatter
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//				DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd [HH:[mm:[ss.[SSS]]]]");
				Timestamp rowDate = null;
				String creationDate = null;
				
				if (model.getValueAt(row, 4) instanceof Timestamp) {
					cs.setObject(6, model.getValueAt(row, 4));
				}
				else if(((String) model.getValueAt(row, 4)).isBlank()) {
					cs.setObject(6, null);
				}
				else {
					try {
					rowDate =  new Timestamp(format.parse((String) model.getValueAt(row, 4)).getTime());
					
					cs.setObject(6,rowDate);
					}
					catch(ParseException e) {
							JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Date has invalid format");
							continue;
				}
				}

				try {
					cs.executeUpdate();
				}
				catch(SQLException e) {
				}
				returnValue = cs.getInt(1);
				if(returnValue != 0) {
					if (returnValue == 1) {
						errorMsgText = "EditorID doesn't exist";
					}
					else if (returnValue ==2) {
						errorMsgText = "Page is invalid or isn't tracked";
					}
					else {
						errorMsgText = "Update Rejected";
					}
					JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + errorMsgText);
				}
				else {
					atLeastOneUpdate = true;
				}
				}
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error");
				System.out.println("fail");
				e.printStackTrace();
			}
			if (atLeastOneUpdate) JOptionPane.showMessageDialog(null, "Update Applied");
			rowsEdited.clear();

	}
		
		if(idx == 6)   //update organization table
		{
			try {
				cs = dbService.getConnection().prepareCall("{? = call update_Organization(?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
			
			for(int row: rowsEdited) {
				errorMsgRow = "At row starting with: " + model.getValueAt(row, 0).toString();
				cs.setString(2, (String)model.getValueAt(row, 0));
				cs.setString(3, (String)model.getValueAt(row, 1));
				try {
					cs.executeUpdate();
				}
				catch(SQLException e) {
				}
				returnValue = cs.getInt(1);
				if(returnValue!=0) JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Update failure");
				else {
					atLeastOneUpdate = true;
				}

			}
			
			if (atLeastOneUpdate) JOptionPane.showMessageDialog(null, "Update Applied");
				
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error");
				System.out.println("fail");
				e.printStackTrace();
			}
		}
		if(idx == 7)   //page
		{
			try {
				cs = dbService.getConnection().prepareCall("{? = call update_Page(?,?)}");
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
			
			for(int row: rowsEdited) {
				errorMsgRow = "At row starting with: " + model.getValueAt(row, 0).toString();
				cs.setString(2, (String)model.getValueAt(row, 0));
				cs.setString(3, (String)model.getValueAt(row, 1));
				try {
					cs.executeUpdate();
				}
				catch(SQLException e) {
				}
				returnValue = cs.getInt(1);
				if(returnValue!=0) JOptionPane.showMessageDialog(null, errorMsgRow + "\n" + "Update failure");
				else {
					atLeastOneUpdate = true;
				}

			}
			
			if (atLeastOneUpdate) JOptionPane.showMessageDialog(null, "Update Applied");
				
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error");
				System.out.println("fail");
				e.printStackTrace();
			}
		}
		rowsEdited.clear();
		return;
		
	}

}
