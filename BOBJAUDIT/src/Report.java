import java.util.ArrayList;
import java.util.List;

public class Report {
private int id;
private int document;
private String name;
private List<Element> element;
public Report(int id, String reportName) {
	super();
	this.id = id;
	this.name = reportName;
	// TODO Auto-generated constructor stub
}

	public Report(int documentID, int reportID, String reportName) {
		super();
		this.document=documentID;
		this.id = reportID;
		this.name = reportName;
		this.element=new ArrayList<Element>();
	}

	public List<Element> getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element.add(element);
	}

	public int getDocument() {
		return document;
	}

	public void setDocument(int document) {
		this.document = document;
	}

	public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
	


}
