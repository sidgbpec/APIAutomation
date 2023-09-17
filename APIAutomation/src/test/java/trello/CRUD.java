package trello;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class CRUD {
	public static String baseURI = "https://api.trello.com";
	public static String key="454f7620b708583b379ba72570a7f79c";
	//public static String oldkey = "d7e20f94bbeea711ec6fcfd051d6a6df";
	public static String token = "ATTAcb614adc383c30efc23959c9e6f73bdaca27440e82289c0bdeb688b74470f47685E00A1C";
	public static String id;
	public static int rowcount = 0;
	public static int columncount = 0;
	public static String name;
	public static String colour;
	public static String description;
	
	
	@Test(priority=0)
	public void dataDrivenSetup() throws IOException
	{
		File excel = new File("C:\\Users\\Siddharth Barthwal\\eclipse-workspace\\APIAutomation\\TrelloTestData.xlsx");
		FileInputStream fis = new FileInputStream(excel);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("Sheet1");
		rowcount = sheet.getLastRowNum();
		columncount=sheet.getRow(0).getLastCellNum();
		System.out.println(rowcount);

		for (int i = 1; i <=rowcount; i++) 
		{
			name = sheet.getRow(i).getCell(0).getStringCellValue();
			System.out.println(name);
			colour = sheet.getRow(i).getCell(1).getStringCellValue();
			System.out.println(colour);
			description = sheet.getRow(i).getCell(2).getStringCellValue();
			create_a_Board();
			read_a_Board();
			update_a_Board();
			read_a_Board();
			delete_a_Board();
		}
		workbook.close();
	}
	
	public void create_a_Board() {
		
		RestAssured.baseURI = baseURI;
		Response response = given().
							queryParam("key", key).
							queryParam("token", token).
							queryParam("name", "defaultname").
							header("Content-Type", "application/json").
							when().
							post("/1/boards/").
							then().assertThat().statusCode(200).extract().response();
		
		JsonPath jp = new JsonPath(response.asString());
		id=jp.get("id");
		System.out.println("id of newly created trello board is "+id);
		System.out.println("name at the time of creation "+jp.get("name"));
	}

	
	public void read_a_Board() {
		// {{BaseURL}}/1/boards/{{id}}?key={{Key}}&token={{Token}}
		RestAssured.baseURI = baseURI;
		Response response = given().
							queryParam("key", key).
							queryParam("token", token).
							header("Content-Type", "application/json").
							when().
							get("/1/boards/" + id).
							then().assertThat().statusCode(200).extract().response();
		JsonPath jsonpath = new JsonPath(response.asString());
		System.out.println("name while reading the board "+jsonpath.get("name"));
		System.out.println("Siddharth updated something from eclipse "+jsonpath.get("name"));
		System.out.println("description while reading the board "+jsonpath.get("desc"));
		System.out.println("color while reading the board "+jsonpath.get("prefs.background"));
	}

	
	public void update_a_Board() 
	{ 
		RestAssured.baseURI=baseURI; 
		Response response=
						given().
						queryParam("key", key).
						queryParam("token",token).
						queryParam("name", name).
						queryParam("prefs/background", "orange").
						queryParam("desc", "updated"+description).
						header("Content-Type", "application/json").
						when().
						put("/1/boards/"+id).
						then().
						assertThat().statusCode(200).extract().response();
		JsonPath jsonpath = new JsonPath(response.asString());
		System.out.println("name while reading the board "+jsonpath.get("name"));
		System.out.println("description while reading the board "+jsonpath.get("desc"));
		System.out.println("color while reading the board "+jsonpath.get("prefs.background"));
	}

	
	public void delete_a_Board() 
	{
		System.out.println(given().queryParam("key", key).queryParam("token", token).when().delete("/1/boards/" + id)
				.then().extract().statusCode());
	}
	

}
