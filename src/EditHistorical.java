import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EditHistorical extends GeneralEdit{
	private String url;
	private String editorName;
	private String editorPage;
	private String comment;
	private String pageUrl;
	private String pageName;
	private String timeStamp; //Currently not reading timeStamp
	private ArrayList<String> categories;
	
	public EditHistorical() {
		super();
		type = EditType.HISTORICAL;
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public String getUser() {
		return editorName;
	}

	public String getUserUrl() {
		return editorPage;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public String getPageUrl() {
		return pageUrl;
	}
	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public String getTimeStamp() {
		if(timeStamp == null) return null;
		//Needed to format the date properly MMMM is long form of months as far as I know
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd MMMM yyyy");
		LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
//		System.out.println("The formatted time is " + date.toString());
		return date.toString() + ":00";
	}

	@Override
	public ArrayList<String> getCategories() {
		return categories;
	}
	
	public String concatonateCategories() {
		String seperator = "^";
		String allCategories = "";
		for(int i = 0; i < categories.size(); i++) {
			allCategories+=categories.get(i);
			if(i!=categories.size()-1) {
				allCategories+=seperator;
			}
		}
		return allCategories;
	}
	
	@Override
	public void setCategories(ArrayList<String> categories) {
		//do nothing, should not be adjusting these
	}
	@Override
	public void setTimeStamp(String timeStamp) {
		//do nothing, should not be adjusting these
	}

	@Override
	public String toString() {
		return "EditHistorical [url=" + url + ", editorName=" + editorName + ", editorPage=" + editorPage + ", comment="
				+ comment + ", pageUrl=" + pageUrl + ", pageName=" + pageName + ", timeStamp=" + this.getTimeStamp()
				+ ", categories=" + categories + ", type=" + type + "]";
	}

}
