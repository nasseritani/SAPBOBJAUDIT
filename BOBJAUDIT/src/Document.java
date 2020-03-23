import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Document {
public int id;
public int folderID;
public String name;
public String cuid;
public List<Report> report;
public List<Element> element;
public List<Element> getElement() {
	return element;
}
public void setElement(Element element) {
	this.element.add(new Element(element.getDocument(),element.getId(),element.getName()));
}
public List<Report> getReport() {
	return report;
}
public void setReport(Report listReport) {
	this.report.add(listReport);
}
public Document(int id, String name, Report listReport) {
	super();
	this.id = id;
	this.name = name;
	this.element=new ArrayList<Element>();
}
public Document() {
	
}
public Document(int id, String name) {
	super();
	this.id = id;
	this.name = name;
	this.report = new ArrayList<Report>();
	this.element=new ArrayList<Element>();


}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getFolderID() {
	return folderID;
}
public void setFolderID(int folderID) {
	this.folderID = folderID;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getCuid() {
	return cuid;
}
public void setCuid(String cuid) {
	this.cuid = cuid;
}

}
