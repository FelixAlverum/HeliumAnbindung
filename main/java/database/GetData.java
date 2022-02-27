package database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;


public class GetData {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Get Gps Data";
    private static String SPREADSHEET_ID = "1WLacviPS2q-WLK4CnwBxnwGtam2OTV6pZDcnMXNfJCY";

    // https://docs.google.com/spreadsheets/d/1WLacviPS2q-WLK4CnwBxnwGtam2OTV6pZDcnMXNfJCY/edit?usp=sharing

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = GetData.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));

        List <String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                clientSecrets, scopes).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main (String[] args) throws IOException, GeneralSecurityException{
        System.out.println("Starte Programm");

        try{
            sheetsService = getSheetsService();
            String range = "Helium Console data!A2:G30";

            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();

            List<List<Object>> values = response.getValues();

            if(values == null || values.isEmpty()){
                System.out.println("No data found");
            }else{
                for (List row: values
                ) {
                    System.out.printf("Time %s LAT %s LONG %s DEV_EUI %s\n", row.get(0), row.get(2), row.get(3), row.get(6));
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }
}
