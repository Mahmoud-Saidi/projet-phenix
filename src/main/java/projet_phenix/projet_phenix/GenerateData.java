package projet_phenix.projet_phenix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GenerateData {
	
	public static void main (String[] args) throws FileNotFoundException{
		
		String path = args[0];
		
		String dateS = args[1];
		
		int nbOfDay = Integer.parseInt(args[2]);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate runDay = LocalDate.parse(dateS, formatter);
		for(int i = 0; i < nbOfDay; i++)
		{
			 String date = runDay.minusDays(i).toString().replace("-", "");
			 writeTxFile(path+"/transactions_"+date+".data");
			 writeRefFile(path+"/reference_prod-"+getRandomMagasinId()+"_"+date+".data");

		}	
		
	}
	
	/**
	 * Generate random String
	 * input : string length
	 * output : string
	 */
	
	public static String getRandomString(int targetStringLength) {
		int leftLimit = 97; 
	    int rightLimit = 122; 
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();
	 
	    return generatedString;
	}
	
	/**
	 * Generate random id_magasin
	 * output : string
	 */
	public static String getRandomMagasinId() {
		
		return getRandomString(8)+"-"+getRandomString(4)+"-"+getRandomString(4)+"-"+getRandomString(4)+"-"+getRandomString(12);
	}
	
	/**
	 * Generate random date
	 * input : random int
	 * output : date
	 */
	public static String getRandomDate(int rand) {
		

        DateFormat simple = new SimpleDateFormat("yyyyMMdd HHmmssSSSZ"); 
  
        Date result = new Date(System.currentTimeMillis()+rand*10); 
  
       return simple.format(result);
	}
	
	/**
	 * Create a transaction file
	 * @param path
	 * @throws FileNotFoundException
	 */
	public static void writeTxFile(String path) throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter(new File(path));
		
		for(int i = 0; i < 100000; i++) {
		Random r = new Random();
		int txId = Math.abs(r.nextInt(100));
		String datetime = getRandomDate(txId);
		String magasin = getRandomMagasinId();
		int produit =  Math.abs(r.nextInt(100));
		int qte =  Math.abs(r.nextInt(100));
		writer.write(txId+"|"+datetime+"|"+magasin+"|"+produit+"|"+qte+"\r\n");
		}
		writer.close();
	}
	
	/**
	 * Create a referential file
	 * @param path
	 * @throws FileNotFoundException
	 */
	public static void writeRefFile(String path) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File(path));
		
		for(int i = 0; i < 100000; i++) {
			Random r = new Random();
			int produit = Math.abs(r.nextInt(100));
			String prix = r.nextFloat()*100+"";
			writer.write(produit+"|"+prix.substring(0, 5)+"\r\n");
		}
		
		writer.close();
		
	}

}
