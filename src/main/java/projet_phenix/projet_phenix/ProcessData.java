package projet_phenix.projet_phenix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.stream.Collectors;
import static java.lang.Float.*;


public class ProcessData {

	public static void main (String[] args) throws FileNotFoundException{
		// files path
		String path = args[0];

		//process date
		String date = args[1];

		//number of day
		int nbOfDay = Integer.parseInt(args[2]);

		String endOfName="";

		if (nbOfDay != 0) endOfName = "-J"+nbOfDay;

		//*********************************************************************************
		//********************* process data by id_magasin ********************************
		//*********************************************************************************

		Stream<String[]> txRows  = getData(path, date, nbOfDay);

		treatDataByMagasin(txRows , date , path, endOfName);

		//*********************************************************************************
		//****************************** Golbal process ***********************************
		//*********************************************************************************

		Stream<String[]> txRows1  = getData(path, date, nbOfDay);

		globalTreatData(txRows1 , date , path, endOfName);


	}

	/**
	 * this function read file and return a stream fo data
	 * input : path
	 * output : stream of data
	 */

	public static Stream<String[]> readFile(String path) throws FileNotFoundException  {


		try {
			InputStream inputFS = new FileInputStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			Stream<String[]> stream = br.lines().map(line -> line.split("\\|"));
			return stream;

		} catch (Exception e){
			System.out.println("File Not Found : "+path);
			return Stream.empty();

		}


	}

	/**
	 * this function concatenate all transactions files in one stream
	 * input : (path of files, process date, nbre of day to process)
	 * output : stream of all transactions details 
	 */
	public static Stream<String[]> getData(String path, String dateS, int nbOfDay) throws FileNotFoundException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate runDay = LocalDate.parse(dateS, formatter);
		Stream<String[]> global_stream = Stream.empty();
		for(int i = 0; i < nbOfDay; i++)
		{
			String date = runDay.minusDays(i).toString().replace("-", "");
			Stream<String[]> stream = readFile(path + "/transactions_"+date+".data");
			global_stream = Stream.concat(global_stream, stream);
		}

		return global_stream;

	}

	/**
	 * this function process data to generate results in files By id_magasin
	 * input : (transactions , process date, path of files , end of files name example -J7)
	 */


	public static void treatDataByMagasin(Stream<String[]> txRows, String dateS, String path, String endOfName) {

		Map<String, List<String[]>> groupedTxRows = txRows.collect(Collectors.groupingBy(row -> row[2]));

		// process data grouped by id_magasin
		groupedTxRows.forEach((String idMagasin, List<String[]> listByMagasin) -> {

			HashMap<String, Integer> product_qte = new HashMap<>();
			HashMap<String, Float> product_ca = new HashMap<>();

			//process data for specific id_magasin grouped by id_product 
			listByMagasin.stream().filter(row -> row[3] != "0").collect(Collectors.groupingBy(row -> row[3])).forEach((String id_product, List<String[]> listByProduct) -> {

				//quantity of product 
				AtomicInteger qteByProduct = new AtomicInteger(0);
				//date of referencial data
				AtomicReference<String> date = new AtomicReference<>();

				AtomicInteger bits = new AtomicInteger(0);

				float price = 0f;

				listByProduct.forEach(row -> {
					qteByProduct.addAndGet(Integer.parseInt(row[4]));
					date.set(row[1].substring(0, 8));

				});

				try {
					// get price of specefic product
					Stream<String[]> refRows = readFile(path+"/reference_prod-"+idMagasin+"_"+date.get()+".data");
					refRows.filter(row -> row[0].equals(id_product)).forEach(row -> bits.set(floatToIntBits(Float.parseFloat(row[1]))));
					price = intBitsToFloat(bits.get());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				float cAByProduit = price * qteByProduct.get();
				product_qte.put(id_product, qteByProduct.get());
				product_ca.put(id_product, cAByProduit);

			});

			//sort a map
			Map<String, String> sorted_product_qte = new LinkedHashMap<>();
			product_qte.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
			.forEachOrdered(x -> sorted_product_qte.put(x.getKey(), Integer.toString(x.getValue())));


			try {
				//create top_100_ventes_<ID_MAGASIN>_YYYYMMDD.data files 
				saveData(sorted_product_qte , path+"top_100_ventes_"+idMagasin+"_"+dateS+endOfName+".data");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Map<String, String> sorted_product_ca = new LinkedHashMap<>();
			product_ca.entrySet().stream()
			.sorted(Map.Entry.<String, Float>comparingByValue().reversed())
			.forEachOrdered(x -> sorted_product_ca.put(x.getKey(), Float.toString(x.getValue())));

			try {
				//create top_100_ca_<ID_MAGASIN>_YYYYMMDD.data files
				saveData(sorted_product_ca , path+"top_100_ca_"+idMagasin+"_"+dateS+endOfName+".data");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}



		});

	}

	/**
	 * this function process golbal data to generate results in files 
	 * input : (transactions , process date, path of files , end of files name example -J7)
	 */

	public static void globalTreatData(Stream<String[]> txRows, String dateS, String path, String endOfName) {


		HashMap<String, Integer> global_product_qte = new HashMap<>();
		HashMap<String, Float> global_product_ca = new HashMap<>();

		Map<String, List<String[]>> groupedTxRows = txRows.filter(row -> row[3] != "0").collect(Collectors.groupingBy(row -> row[3]));

		//process data for specific id_magasin grouped by id_product 
		groupedTxRows.forEach((String idProduct, List<String[]> listByProduct) -> {

			//quantity of product 
			AtomicInteger qteByProduct = new AtomicInteger(0);

			AtomicInteger bitsCa = new AtomicInteger(0);

			//process data for specific id_magasin grouped by date 
			listByProduct.stream().collect(Collectors.groupingBy(row -> row[1])).forEach((String dateP, List<String[]> listByDate) ->{

				listByDate.forEach(row -> {
					float price = 0f;
					AtomicInteger bitsPrice = new AtomicInteger(0);

					qteByProduct.addAndGet(Integer.parseInt(row[4]));

					try {
						// get price of specefic product
						Stream<String[]> refRows = readFile(path+"/reference_prod-"+row[2]+"_"+dateP.substring(0, 8)+".data");
						refRows.filter(row1 -> row1[0].equals(idProduct)).forEach(row1 -> bitsPrice.set(floatToIntBits(Float.parseFloat(row1[1]))));
						price = intBitsToFloat(bitsPrice.get());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}	

					bitsCa.set(floatToIntBits(intBitsToFloat(bitsCa.get())+Integer.parseInt(row[4])*price));

				});


			});

			float caByProduct = intBitsToFloat(bitsCa.get());
			global_product_qte.put(idProduct, qteByProduct.get());
			global_product_ca.put(idProduct, caByProduct);


		});

		Map<String, String> sorted_product_qte = new LinkedHashMap<>();
		global_product_qte.entrySet().stream()
		.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
		.forEachOrdered(x -> sorted_product_qte.put(x.getKey(), Integer.toString(x.getValue())));


		try {
			//create top_100_ventes_GLOBAL_YYYYMMDD.data files 
			saveData(sorted_product_qte , path+"top_100_ventes_GLOBAL_"+dateS+endOfName+".data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Map<String, String> sorted_product_ca = new LinkedHashMap<>();
		global_product_ca.entrySet().stream()
		.sorted(Map.Entry.<String, Float>comparingByValue().reversed())
		.forEachOrdered(x -> sorted_product_ca.put(x.getKey(), Float.toString(x.getValue())));

		try {
			//create top_100_ca_GLOBAL_YYYYMMDD.data files
			saveData(sorted_product_ca , path+"top_100_ca_GLOBAL_"+dateS+endOfName+".data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}



	}

	/**
	 * this function save data to file
	 * input : (map of data , path)
	 */
	public static void saveData(Map<String, String> map , String path) throws FileNotFoundException {

		PrintWriter writer = new PrintWriter(new File(path));

		// save top 100 product to file
		map.entrySet().stream().limit(100).forEach(product -> writer.write(product.getKey()+"|"+product.getValue()+"\r\n"));
		writer.close();


	}



}
