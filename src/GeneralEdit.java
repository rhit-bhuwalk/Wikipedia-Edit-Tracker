import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GeneralEdit {
	EditType type;
	HashMap<String, String> superCategories = null; //really would need an efficiency boost if doing every single category. So much more
	public abstract String toString();
	public abstract String getUser();
	public abstract String getUserUrl();
	public abstract String getComment();
	public abstract String getPageUrl();
	public abstract String getPageName();
	public abstract String getUrl();
	public abstract String getTimeStamp();
	public abstract ArrayList<String> getCategories();
	public abstract void setCategories(ArrayList<String> categories);
	public abstract void setTimeStamp(String timeStamp);
	public EditType getType() {
		return type;
	}
	public int inRange() {
//		if(lowerBound==null||upperBound==null) return false; //can't input null
		//somehow need to know it is ip, but this is probably good enough
//		String[] myArray = this.getUser().split("."); //split ip at periods.
//		return (this.getUser().compareTo(upperBound) <= 0)
//		for(int i = 0; i < myArray.length; i++) {
//			if()
//		}
		boolean house = false;
		boolean senate = false;
		String[] myArray = this.getUser().split("\\."); //may have to do \\.
		if(myArray.length==4) { //know it is an ip if it matches this thingy
//			System.out.println("Parsing the ip");
			int part1 = Integer.parseInt(myArray[0]);
			int part2 = Integer.parseInt(myArray[1]);
			int part3 = Integer.parseInt(myArray[2]);
			int part4 = Integer.parseInt(myArray[3]);
			if(part1==143&&part2==231) {
				house = true;
			} else if(part1==137&&part2==18) {
				house = true;
			} else if(part1==143&&part2==228) {
				house = true;
			} else if(part1==12) { //there is 74.119.128.0/16 which I do not know how to interpret
				if(part2==147&&part3==170&&part4>=144&&part4<=159) {
					house = true;
				} else if(part2==185&&part3==56&&part4>=0&&part4<=7) {
					house = true;
				}
			} else if(part1==156&&part2==33) {
				senate = true;
			}
		}
		
//		Pattern pattern = Pattern.compile("[143.231.*][137.18.*][143.228.*][12.185.56.[0-7]][12.147.170.1[4-5][4-9]]"); //144-159
//		Pattern pattern = Pattern.compile("(143.231.*)(137.18.*)(143.228.*)(12.185.56.[0-7])(12.147.170.1[4-5][4-9])"); //144-159
		//brackets are union, parenthesis are groups
		//could also split and check the individual four numbers, but pattern matching may be easier
//		Matcher m = pattern.matcher(this.getUser());
		
//		Pattern p2 = Pattern.compile("143\\.231\\.*"); //period is any character, so need to escape, but have to escape again
//		Matcher m2 = p2.matcher(this.getUser());
//		System.out.println("Simple test it is " + (house ? "house": "other"));
//		if(m.find())
//		m.
//		m.group(); not sure what this does
		return (house ? 1: (senate ? 2: 0)); //possibly what I want and not matches
	}
	
	public HashMap<String,String> getSuperCategories() {
		return superCategories;
	}
	
	public void setSuperCategories(HashMap<String,String> superCategories) {
		this.superCategories = superCategories;
	}
}
