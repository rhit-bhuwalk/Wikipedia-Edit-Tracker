import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Selenium {
	private WebDriver driver; //don't want other stuff calling this
	private final int SLEEP_TIME = 3000; //only use if you don't want Wikipedia to get mad at you and have limited CPU power
	private HashSet<String> checkedSuperCategoryPages = new HashSet<String>();
	private int wikipediaQueries = 0;
	
	public Selenium() {
		System.setProperty("webdriver.chrome.driver", "src/lib/chromedriver.exe");
		System.setProperty("webdriver.chrome.whitelistedIps", "");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless --disable-gpu"); //currently not working?
		driver = new ChromeDriver(options);
	}
	
	public void quiteSelenium() {
		this.driver.quit();
	}
	
	public int getNumberOfWikipediaCalls() {
		return wikipediaQueries;
	}
	
	public void fillInMissingInfo(GeneralEdit edit) {
		if(edit.getCategories()==null||edit.getTimeStamp()==null) { //only bother if something is missing
//			if(!edit.getUrl().startsWith("https://en.wikipedia.org")) {return;} //can't handle non-English Wikipedia
			driver.get(edit.getUrl());
			wikipediaQueries++;
			ArrayList<WebElement> categoryElements = null;
			WebElement timeStampElement = null;
			String timeStamp = null;
			ArrayList<String> categories = null;
			if(edit.getCategories()==null) {
				try {
					categoryElements = (ArrayList<WebElement>) driver.findElements(By.xpath("//*[@id=\"mw-normal-catlinks\"]/ul/li/a"));			
					categories = new ArrayList<String>(categoryElements.size());
					for(WebElement e: categoryElements) {
						categories.add(e.getText());
					}
					edit.setCategories(categories);
				} catch (NoSuchElementException e) {
					//nothing happens, just don't error out
				}
			}
			if(edit.getTimeStamp()==null) {
				try {
					timeStampElement = driver.findElement(By.xpath("//*[@id='mw-diff-ntitle1']/strong/a"));
					//Various languages but always looks something like blah blah dateTime
					Pattern pattern = Pattern.compile("\\d\\d:\\d\\d(.*)");
					Matcher matcher = pattern.matcher(timeStampElement.getText());

					while(matcher.find()) {
//						System.out.println(matcher.group());
						timeStamp=matcher.group();
						timeStamp = timeStamp.replace(",", ""); //get rid of comma
					}

					System.out.println("Grabbed a timeStamp of " + timeStamp);
					edit.setTimeStamp(timeStamp);
				} catch (NoSuchElementException e) {
					//nothing happens, just don't error out
				}
			}
			System.out.println("Revised " + edit.toString());
//			this.quiteSelenium();
		}
		//get account. Selenium with multiple tabs or threads is probably going to crash computer and Wikipedia would be mad, so doing second one
		if(edit.getType()==EditType.NEW) {
			Edit editNew = (Edit) edit;
			if(editNew.getAnonymouse()==0) {
				//only need to query if either of these are missing
				if(editNew.getCreationDate()==null||editNew.getStatus()==null) {
					wikipediaQueries++;
					//then, finally we want to get creationDate
					String queryUrl = "https://meta.wikimedia.org/wiki/Special:CentralAuth?target="; //NOT a SQL Query this is a URL Query!!!!!!
					queryUrl+=editNew.getUser().replace(' ', '+'); //convert to query
					System.out.println("Searching for a user, querying: " + queryUrl);
					driver.get(queryUrl);
					
					if(editNew.getCreationDate()==null) {
						WebElement creationElement;
						try {
							creationElement = driver.findElement(By.xpath("//*[@id=\"mw-centralauth-info\"]/ul/li[2]"));
//					System.out.println("The element is " + creationElement.getText());
							//now need to parse the text will be of formate Registered: dateTime (time since registered)
							Pattern pattern = Pattern.compile("\\d\\d:\\d\\d(.*)");
							Matcher matcher = pattern.matcher(creationElement.getText());
							
							String creationDate = null;
							while(matcher.find()) {
								creationDate= matcher.group();
								creationDate = creationDate.replace(",", ""); //eliminate the comma
								creationDate = creationDate.replaceAll(" \\(.*", ""); //eliminate the " (time since registered)"
							}
//							System.out.println("The creation date is " + creationDate);
							editNew.setCreationDate(creationDate);
						} catch (NoSuchElementException e) {
//							System.out.println("Did not find the creationDate");
							//nothing to do, just ignore
						}
					}
					if(editNew.getStatus()==null) {
						ArrayList<WebElement> statusElements;
						try {
							statusElements = (ArrayList<WebElement>) driver.findElements(By.xpath("//*[@id=\"mw-centralauth-merged\"]/table/tbody/tr[*]/td[4]/a"));
							String status = null;
							ArrayList<String> statuses = new ArrayList<String>(statusElements.size());
							
							//populate the staatuses array
							for(WebElement e: statusElements) {
								statuses.add(e.getText());
							}
							
							for(String s: statuses) {
								if(!s.equals("—")) { //that is Wikipedia's way of saying null.
									status = s; //look for status not being null, and take the first not null.
									break;
									//being banned on one wiki probably means going to be banned on all
								}
							}
							if(status==null) {status="Good";} //no status just means good
							
							editNew.setStatus(status); //could just be setting to null if does not exist
//							System.out.println("Located a status of " + status);
//							System.out.println("The full set of status was " + statuses.toString());
							
						} catch (NoSuchElementException e) {
//							System.out.println("Could not find user status");
							//nothing to do, just ignore
						}
					}
				}
			}
		}
	}
	
	public void findSuperCategories(GeneralEdit edit) {
		if(edit.superCategories == null&&!checkedSuperCategoryPages.contains(edit.getPageUrl())) {
			//do not need to re-check if the page was already checked in the import process earlier
			//while can reduce checks on others, this is probably the most intensive query.
			checkedSuperCategoryPages.add(edit.getPageUrl());
			HashMap<String,String> superCategories = new HashMap<String,String>();
			WebElement parentElement = null;
			for(String s: edit.getCategories()) {
				String queryUrl = "https://en.wikipedia.org/wiki/Special:CategoryTree?target="; //NOT A SQL Query!!!
				wikipediaQueries++;
				queryUrl += s.replace(' ', '+'); //to build the query
				driver.get(queryUrl);
				
				try {
					parentElement = driver.findElement(By.xpath("//*[@id=\"mw-content-text\"]/div[2]"));
					String parents = parentElement.getText().replace("Parents: ", "");
					//only care about the first one
					String[] parentArray = parents.split("\\|", 2); //yay regex! so much regex! Split n - 1 to only grab first as that is all we care
					String trueParent = parentArray[0].substring(0, parentArray[0].length()-1); //cut off the space at the end. end minus 1
//					System.out.println("Found a super category of " + s + " that is  " + trueParent);
					superCategories.put(s, trueParent);
				} catch (NoSuchElementException e) {
					//Nothing to do; or possibly superCategories.put(s, null);
				}
			}
			edit.setSuperCategories(superCategories);
		} else {
			checkedSuperCategoryPages.add(edit.getPageUrl()); //not null, so add. Does not matter if already in list, just doesn't change
		}
	}
}
