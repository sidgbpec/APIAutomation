package trello;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;
public class SimpleBooks {
	JsonPath jp;
	public static String orderId;
	public static String baseURI="https://simple-books-api.glitch.me";
	public static String accessToken;
	String clientName;
	String clientEmail;
	@Test(priority=0)
	public void Authentication()
	{
		RestAssured.baseURI=baseURI;
		double randomNumber=Math.random();
		clientName="siddharth"+randomNumber;
		clientEmail="sidgbpec"+randomNumber+"@gmail.com";
		
		String requestBody="{\r\n" + "   \"clientName\":\"" + clientName+"\",\r\n"+ "   \"clientEmail\":\""+ clientEmail+"\"\r\n" + "}";
		
		System.out.println(requestBody);
		Response response=given().header("Content-Type", "application/json").
				when().body(requestBody).
				post("/api-clients/").
				then().
				assertThat().
				statusCode(201).
				extract().
				response();
		System.out.println(response.asPrettyString());
		jp=new JsonPath(response.asString());
		accessToken=jp.get("accessToken");
		System.out.println("access token is "+accessToken);
	}
	
	@Test(priority=1)
	public void SubmitAnOrder()
	{
		RestAssured.baseURI=baseURI;
		String requestBodyForSubmittingAnOrder="{\r\n"
				+ "  \"bookId\": 1,\r\n"
				+ "  \"customerName\": \"John\"\r\n"
				+ "}";
		Response responseOfSubmittedOrder=given().
											header("Content-Type", "application/json").
											header("Authorization", "Bearer "+accessToken).
											body(requestBodyForSubmittingAnOrder).
											when().
											post("/orders/").
											then().
											assertThat().
											statusCode(201).
											extract().
											response();
		System.out.println(responseOfSubmittedOrder.asPrettyString());
		jp=new JsonPath(responseOfSubmittedOrder.asString());
		orderId=jp.get("orderId");
		System.out.println(orderId);
	}
	
	@Test(priority=2)
	public void getListOfBooks() {
		RestAssured.baseURI=baseURI;
		Response listOfBooks=given().header("Content-Type", "application/json").when().get("/books").then().assertThat().statusCode(200).extract().response();	
		System.out.println("list of books: "+listOfBooks.asString());
	}
	
	@Test(priority=3)
	public void getABookById()
	{
		RestAssured.baseURI=baseURI;
		int bookId=1;
		Response responseOfASingleBook=given().header("Content-Type", "application/json").when().get("/books/"+bookId).then().assertThat().statusCode(200).extract().response();
		System.out.println("single book with bookid "+bookId +" is"+responseOfASingleBook.asPrettyString());
	}
	
	@Test(priority=4)
	public void getAllOrders()
	{
		RestAssured.baseURI=baseURI;
		Response responeOfAllOrders=given().header("Content-Type", "application/json").header("Authorization", "Bearer "+accessToken).get("/orders").then().assertThat().statusCode(200).extract().response();
		System.out.println("all orders: "+responeOfAllOrders.asPrettyString());
	}
	
	@Test(priority=5)
	public void getAnOrder()
	{
		RestAssured.baseURI=baseURI;
		Response responseOfSingleOrder=given().header("Content-Type", "application/json").header("Authorization", "Bearer "+accessToken).when().get("/orders/"+orderId).then().assertThat().statusCode(200).extract().response();
		System.out.println("single order: "+responseOfSingleOrder.asPrettyString());
	}
	
	@Test(priority=6)
	public void updateAnOrder()
	{
		RestAssured.baseURI=baseURI;
		String bodyForUpdation="{\r\n"+ "   \"customerName\":\""+ "Updated_"+clientName+"\"\r\n" + "}";
		Response updateAnOrder=given().header("Content-Type", "application/json").header("Authorization", "Bearer "+accessToken).when().body(bodyForUpdation).patch("/orders/"+orderId).then().assertThat().statusCode(204).extract().response();
		System.out.println("updated order response "+updateAnOrder.asPrettyString());
		getAnOrder();
	}

}
