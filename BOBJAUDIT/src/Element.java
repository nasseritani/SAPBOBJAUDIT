
public class Element {
	private int id;
	private String name;
	private int document;

	public Element(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Element(int documentID,int id, String name) {
		super();
		this.document=documentID;
		this.id = id;
		this.name = name;
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
