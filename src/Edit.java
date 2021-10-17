import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Edit extends GeneralEdit {

	private String channel;
	private String page;
	private String File;
	private String pageUrl;
	private String url;
	private int delta;
	private String comment;
	private String wikipedia;
	private String wikipediaUrl;
	private String wikipediaShort;
	private String wikipediaLong;
	private String user;
	private String userUrl;
	private String unpatrolled;
	private boolean newPage;
	private boolean robot;
	private boolean anonymous;
	private String namespace;
	private ArrayList<String> categories;
	private String timeStamp;
	private String creationDate;
	private String status;
	
	public Edit() {
		super();
		type = type.NEW;
	}
	
	@Override
	public String getUserUrl() {
		return userUrl;
	}
	@Override
	public String getUser() {
		return user;
	}
	@Override
	public String getPageName() {
		return page;
	}
	@Override
	public String getUrl() {
		return url;
	}
	public int getAnonymouse() {
		if(anonymous) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public String getPageUrl() {
		return pageUrl;
	}

	@Override
	public ArrayList<String> getCategories() {
		return categories;
	}
	
	@Override
	public String toString() {
		return "Edit [channel=" + channel + ", page=" + page + ", File=" + File + ", pageUrl=" + pageUrl + ", url="
				+ url + ", delta=" + delta + ", comment=" + comment + ", wikipedia=" + wikipedia + ", wikipediaUrl="
				+ wikipediaUrl + ", wikipediaShort=" + wikipediaShort + ", wikipediaLong=" + wikipediaLong + ", user="
				+ user + ", userUrl=" + userUrl + ", unpatrolled=" + unpatrolled + ", newPage=" + newPage + ", robot="
				+ robot + ", anonymous=" + anonymous + ", namespace=" + namespace + ", categories=" + categories
				+ ", timeStamp=" + this.getTimeStamp() + ", creationDate=" + this.getCreationDate() + ", status = " 
				+ status + ", type=" + type + "]";
	}

	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public String getTimeStamp() {
		if(timeStamp == null) return null;
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
		builder.parseCaseInsensitive();
		builder.appendPattern("HH:mm d[d] MMMM yyyy");
		DateTimeFormatter formatter = builder.toFormatter();
		LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
		return date.toString() + ":00";
	}
	
	public String getCreationDate() {
		if(creationDate == null) return null;
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
		builder.parseCaseInsensitive();
		builder.appendPattern("HH:mm d[d] MMMM yyyy");
		DateTimeFormatter formatter = builder.toFormatter();
		LocalDateTime date = LocalDateTime.parse(creationDate, formatter);
		return date.toString() + ":00";
	}
	
	public int isRobot() {
		return robot ? 1: 0;
	}
	
	public String getStatus() {
		return status;
	}
	
	@Override
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	
	@Override
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}