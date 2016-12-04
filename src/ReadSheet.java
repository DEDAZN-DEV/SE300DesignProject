import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import javafx.application.Platform;

public class ReadSheet extends ShowGui implements Runnable  {
	
	

	/** Application name. */
    private static final String APPLICATION_NAME =
        "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream("client_secret.json");
            //ShowGui.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
       
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
   
    //check if everyone has confirmed
  	boolean allConfirmed = false;
   	int hasConfirmed = 1;
  
  	
	@Override
	public void run() {
		
		do{
			try{
				
				
				// Build a new authorized API client service.
		        Sheets service = getSheetsService();
				
				//clear the conf. list
				conf.clear();
				
				

		        // Prints the names and majors of students in a sample spreadsheet:
		        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		        String spreadsheetId = "1o3k5Koml7QabFHP1w6ou4-UShjua2XPqjNvSJWhQSs4";
		        String range = "Sheet1!A:B";
		        
		        
		        ValueRange response = service.spreadsheets().values()
		            .get(spreadsheetId, range)
		            .execute();
		        List<List<Object>> values = response.getValues();
		        
		        System.out.printf("MIDN\t\t\tconformation\n");
		        
		        
		        if (values == null || values.size() == 0) {
		            System.out.println("No data found.");
		        } else {
		          for (List<Object> row : values) {
		        	  String status = (String)row.get(1); 
		        	  String confirmed = "1";
		        	  
		        	  for(String name : names){
		        		  if(row.get(0).toString().equals(name)){
		        			  System.out.printf("%s ", name);
		        			  if(status.equals(confirmed)){
		        				  System.out.printf("\thas confirmed\n");
		                          conf.add(1);
		        			  }else{
		        				  System.out.printf("\thas not confirmed\n");
		                      	  conf.add(0); 
		        			  }
		        		  }
		        		  
		        	  }
		          }
		          
		          
		          
			      System.out.println(confirmation());
			     
	          Thread.sleep(3000);
	          
	          
	         
		          
		        }
			}catch(Exception e){
				
			}
			
		}while(!confirmation());
		
		 System.out.println("Everyone has confirmed!!");
		 AllConfGUI show = new AllConfGUI();
		 Platform.runLater(new Runnable(){

			@Override
			public void run() {
				show.showAllConf();
			}
			 
		 });
		 
		 
		
	}
	
	
	public boolean confirmation(){
		
		
		for (int i = 0; i < conf.size(); i++) {
			
			if(!conf.get(i).equals(hasConfirmed)){
				return false;
			}
			
		}
		 return true;
	}

}
