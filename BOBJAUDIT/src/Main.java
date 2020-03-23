import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	static String cmcDocuments=null;
	static String reportsCon=null;
	static String reportsIDCon=null;
	static String elementCon=null;
	static String elementIDCon=null;
	static Bo4Connection connection=null;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main mn=new Main();
		//Do the connection to the CMC
		connection = new Bo4Connection("http://WH-AN-01.tyconz.com:6405/biprws");
		connection.connect("Administrator", "Password", "secEnterprise");
		System.out.println("Connected to CMC");
		//createHeaderRow(sheet);
		//get the list of documents 
		try {
			cmcDocuments = connection.query("GET", "/documents", "application/json");
			JSONArray documents = new JSONObject(cmcDocuments).getJSONObject("documents").getJSONArray("document");
			//get list of documents and fetch into array
			List<Document> listBook = mn.getListDocumenst();
			//List<Document> listReport = mn.getListReport(listBook);
			//List<Report> reports=new ArrayList<Report>();
			//			System.out.println("Size:"+" Document"+listBook.size());
			//			for(int i=0;i<listReport.size();i++) {
			//				reports=listReport.get(i).getReport();
			////				for(int j=0;j<reports.size();j++ ) {
			////					System.out.println("Reports: "+reports.get(j).getName());
			////				}
			//			}
			//List<Document> listReports = mn.getListElement(listBook);
			System.out.println("Elements array "+listBook);
			//write to excel 
			String excelFilePath = "FormattedJavaBooks.xls";
			mn.writeExcel(listBook, excelFilePath);
		}
		finally {
			System.out.println("") ;  
			//connection.disconnect();
		}
	}
//	private List<Document> getListDocument() throws Exception {
//		List<Document> arrDocument = new ArrayList<Document>(); 
//		JSONArray documents = new JSONObject(cmcDocuments).getJSONObject("documents").getJSONArray("document");
//		//move through the document all 
//		for (int i = 0; i < documents.length(); i++) {
//			//get the detail of document and set them 
//			JSONObject document = documents.getJSONObject(i);
//			int id=document.getInt("id");
//			String name=document.getString("name");
//			reportsCon = connection.query("GET", "/documents/" + id + "/reports", "application/json");
//			//List<Report> listReport = getListReport(); 			
//			Document book1 = new Document(id,name);
//			arrDocument.add(book1);
//			//List<Report> listReport = getListReport();
//		}
//		return arrDocument;
//	}
	private List<Document> getListDocumenst() throws Exception {
		List<Document> arrDocument = new ArrayList<Document>(); 
		String reportName;
		int reportID;
		JSONArray documents = new JSONObject(cmcDocuments).getJSONObject("documents").getJSONArray("document");
		//move through the document all 
		for (int i = 0; i < documents.length(); i++) {
			//get the detail of document and set them 
			JSONObject document = documents.getJSONObject(i);
			int documentID=document.getInt("id");
			String name=document.getString("name");
			Document book2 = new Document(document.getInt("id"),document.getString("name"));
			reportsCon = connection.query("GET", "/documents/" + documentID + "/reports", "application/json");
			JSONArray reportJs = new JSONObject(reportsCon).getJSONObject("reports").getJSONArray("report");
			for (int j = 0; j < reportJs.length(); j++) {
				// Print Report information
				JSONObject report = reportJs.getJSONObject(j);
				reportName = report.getString("name");
				reportID = report.getInt("id");
				Report book1 = new Report(documentID,reportID,reportName);
				reportsIDCon = connection.query("GET", "/documents/" + documentID+ "/reports/"+reportID, "application/json");
				JSONObject reportNam = new JSONObject(reportsIDCon).getJSONObject("report");
				//System.out.println("report name with details:  "+reportNam);
				elementCon = connection.query("GET", "/documents/" + documentID + "/reports/"+reportID+"/elements", "application/json");
				JSONObject element = new JSONObject(elementCon);
				JSONObject elementID = element.getJSONObject("elements");
				JSONArray elementArr = elementID.getJSONArray("element");
				Element element1 = null;
				for(int k=0;k<elementArr.length();k++) {
					int idElement = elementArr.getJSONObject(k).getInt("id");
					String elementType =  elementArr.getJSONObject(k).getString("@type");
					elementIDCon = connection.query("GET", "/documents/" + documentID + "/reports/"+reportID+"/elements/"+idElement, "application/json");
					//System.out.println("element name with details:  "+ elementArr.getJSONObject(k));
					if( elementType.equalsIgnoreCase("Cell")) {
						int parentID =  elementArr.getJSONObject(k).getInt("parentId");
						if(parentID==9) {
							JSONObject elementName = new JSONObject(elementIDCon).getJSONObject("element");
							if(elementName.has("content")){
								JSONObject elementContent=elementName.getJSONObject("content");
								//System.out.println("element content:  "+elementContent);
								JSONObject elementExpression=elementContent.getJSONObject("expression");
								JSONObject elementFormula=elementExpression.getJSONObject("formula");
								String content =  elementFormula.get("$").toString();
								System.out.println("element content:  "+content);
								 element1 = new Element(documentID,idElement,content);
								 book1.setElement(element1);
							}
							else {
								 book1.setElement(element1);
								//arrReports.get(i).setElement(book1);
								//System.out.println("No Content");
							}
						}
						else {
							 book1.setElement(element1);

							//arrReports.get(i).setElement(book1);
							//System.out.println("Not in the Vertical Table)");
						}
					}
					else
					{
						 book1.setElement(element1);

					//arrReports.get(i).setElement(book1);
					System.out.println("element ignnore");
					}
				}
				book2.setReport(book1);
			}
			arrDocument.add(book2);
		}
		return arrDocument;
	}
	//	private List<Document> getListReport(List<Document>arrDocument) throws Exception {
	//		List<Report> arrReport = new ArrayList<Report>(); 
	//		List<Document> arrDoc = new ArrayList<Document>(); 
	//		int reportID=-1; //List<Document>
	//		//listBook = getListDocument(); 
	//		for(int i=0;i<arrDocument.size();i++) {
	//			//for(int i=0;i<listBook.size();i++) { 
	//			int documentIDs=arrDocument.get(i).getId();
	//			reportsCon = connection.query("GET","/documents/" + documentIDs + "/reports", "application/json"); 
	//			JSONArray		reportJs = new JSONObject(reportsCon).getJSONObject("reports").getJSONArray("report"); 
	//			for(int j = 0; j < reportJs.length(); j++) { // Print Report information
	//						JSONObject report = reportJs.getJSONObject(j); 
	//						reportName =report.getString("name"); 
	//						reportID = report.getInt("id"); 
	//						reportsIDCon =		connection.query("GET", "/documents/" + documentIDs + "/reports/"+reportID,"application/json"); 
	//						JSONObject reportNam = new JSONObject(reportsIDCon).getJSONObject("report");
	//										//System.out.println("report name with details:  "+reportNam); Report book1 =
	//										new Report(arrDocument.get(i),reportID,reportName); arrReport.add(book1);
	//										//arrDocument.get(i).setReport(arrReport); } Document book1 = new
	//										Document(documentIDs,arrDocument.get(i).getName(),arrReport);
	//										arrDoc.add(book1); } 
	//			return arrDoc; }

//	private List<Document> getListElement(List<Document>arrReports) throws Exception {
//		List<Element> arrReport = new ArrayList<Element>();
//		Set<Element> e=new HashSet<Element>();
//		//List<Document> listBook = getListDocument();
//		//List<Report> listReport = getListReport();
//		for(int i=0;i<arrReports.size();i++) {
//			int documentIDs=arrReports.get(i).getId();
//			//List<Report>rpt=arrReports.get(i).getReport();
//			int reportID=arrReports.get(i).getReport()..getId();
//			//System.out.println(""+i+arrReports.get(i).getReport());			
//			//for(int s=0;s<arrReports.get(i).getReport().size();s++) {
//			//int reportID=arrReports.get(i).getReport().get(s).getId();
//			elementCon = connection.query("GET", "/documents/" + documentIDs + "/reports/"+reportID+"/elements", "application/json");
//			JSONObject element = new JSONObject(elementCon);
//			JSONObject elementID = element.getJSONObject("elements");
//			JSONArray elementArr = elementID.getJSONArray("element");
//			Element book1 = null;
//			for(int k=0;k<elementArr.length();k++) {
//				int idElement = elementArr.getJSONObject(k).getInt("id");
//				String elementType =  elementArr.getJSONObject(k).getString("@type");
//				elementIDCon = connection.query("GET", "/documents/" + documentIDs + "/reports/"+reportID+"/elements/"+idElement, "application/json");
//				//System.out.println("element name with details:  "+ elementArr.getJSONObject(k));
//				if( elementType.equalsIgnoreCase("Cell")) {
//					int parentID =  elementArr.getJSONObject(k).getInt("parentId");
//					if(parentID==9) {
//						JSONObject elementName = new JSONObject(elementIDCon).getJSONObject("element");
//						if(elementName.has("content")){
//							JSONObject elementContent=elementName.getJSONObject("content");
//							//System.out.println("element content:  "+elementContent);
//							JSONObject elementExpression=elementContent.getJSONObject("expression");
//							JSONObject elementFormula=elementExpression.getJSONObject("formula");
//							String content =  elementFormula.get("$").toString();
//							System.out.println("element content:  "+content);
//							 book1 = new Element(documentIDs,idElement,content);
//							 arrReport.add(book1);
//							e.add(book1);
//							arrReports.get(i).setElement(book1);
//						}
//						else {
//							//arrReports.get(i).setElement(book1);
//
//							//System.out.println("No Content");
//						}
//					}
//					else {
//						//arrReports.get(i).setElement(book1);
//						//System.out.println("Not in the Vertical Table)");
//					}
//				}
//				else
//				{
//					//arrReports.get(i).setElement(book1);
//				System.out.println("element ignnore");
//				}
//			}
//		}
//		return arrReports;
//	}

	private static void createHeaderRow(org.apache.poi.ss.usermodel.Sheet sheet) {
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		Font font =  sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		cellStyle.setFont(font);

		Row row =  sheet.createRow(0);
		Cell cellTitle = row.createCell(0);

		cellTitle.setCellStyle(cellStyle);
		cellTitle.setCellValue("Document ID");

		Cell cellAuthor = row.createCell(1);
		cellAuthor.setCellStyle(cellStyle);
		cellAuthor.setCellValue("Document Name");

		Cell cellTest = row.createCell(2);
		cellTest.setCellStyle(cellStyle);
		cellTest.setCellValue("Report Name");

		Cell cellTest1 = row.createCell(3);
		cellTest.setCellStyle(cellStyle);
		cellTest.setCellValue("Element Name");
		
		Cell cellEl = row.createCell(4);
		cellEl.setCellStyle(cellStyle);
		cellEl.setCellValue("Element Name");

	}
	public void writeExcel(List<Document> listDocument, String excelFilePath) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		org.apache.poi.ss.usermodel.Sheet sheet =  workbook.createSheet();
		createHeaderRow(sheet);
		Row row = null;
		int rowCount = 1;
		for(int i=0;i<listDocument.size();i++)
		{
			for(int j=0;j<listDocument.get(i).getReport().size();j++) {
				for(int k=0;k<listDocument.get(i).getReport().get(j).getElement().size();k++)
				{
					row = sheet.createRow(rowCount++);
					writeBook(listDocument.get(i),listDocument.get(i).getReport().get(j),listDocument.get(i).getReport().get(j).getElement().get(k), row,sheet);
				}
			}
		}
//				row = sheet.createRow(j);
//				writeBook(listDocument.get(i), row,sheet);
//			}
		//}
		//			for (Document document : listDocument) {
		//				//System.out.println("dd"+document.getReport());
		////				if(document.getReport().size()>1)
		////				{
		////					row = sheet.createRow(++rowCount);
		////					writeBook(document, row);
		////				}
		////				else {
		//					
		try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
			workbook.write(outputStream);
		}		
	}
	
	private void writeBook(Document document,Report report,Element element, Row row,org.apache.poi.ss.usermodel.Sheet sheet) {
		// TODO Auto-generated method stub

		Cell cell = row.createCell(0);
		cell.setCellValue(document.getId());
		cell = row.createCell(1);
		cell.setCellValue(document.getName());
		cell = row.createCell(2);
		cell.setCellValue(report.getName());
		cell = row.createCell(3);
		if(element==null) {
			cell.setCellValue("");
		}
		else {
		cell.setCellValue(element.getName());
		}
}
}

