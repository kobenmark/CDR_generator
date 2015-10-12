package com.bgandrew.cdr_generator;


import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken; 
import com.google.gson.Gson;
import com.bgandrew.cdr_generator.model.Constants;
import com.bgandrew.cdr_generator.model.Customer;
import com.bgandrew.cdr_generator.model.Location;
import com.bgandrew.cdr_generator.model.LocationSet;
import com.bgandrew.cdr_generator.model.LocationSet.CITY;
import com.bgandrew.cdr_generator.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/** Generator class.
 *
 */
public class Generator {

	public static final long starttime = System.nanoTime();
	public static final String runtimeId = "kalinin_" + String.valueOf(starttime);		
	private final static String DEVICES_LIST = runtimeId + "_devices.json";
	private final static String OUTPUT_FILE = runtimeId + "_data.csv";
	private final static String CELLID_LOCATIONS_TABLE = runtimeId + "_cellId_locations_table.csv";

	private final static String[] CSV_HEADER = {
		"runtimeId",
		"timestamp",
		"caller MSISDN", "caller IMEI", "caller IMSI",
		"caller cell ID",
		"recipient MSISDN", "recipient IMEI", "recipient IMSI",
		"recipient cell ID",
		"type", "duration", "length" 
	};

	private static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	public static HashMap<Location, Integer> generateCellIdMappingFile(Map<CITY, List<Location>>... locationPools){
		int expectedSize = 0; 
		for (Map<CITY, List<Location>> locationPool: locationPools){
			for(CITY city: locationPool.keySet()){
				expectedSize += locationPool.get(city).size();
			}					
		}

		HashMap<Location, Integer> location_to_cellId =
				new HashMap<Location, Integer>(expectedSize);

		for(Map<CITY, List<Location>> locationPool: locationPools){
			for(CITY city: locationPool.keySet()){				
				for(Location location: locationPool.get(city)){
					if (!location_to_cellId.containsKey(location)){
						int cellId = location_to_cellId.size();
						location_to_cellId.put(location, cellId);
					}
				}
			}
		}

		BiMap<Location, Integer> location_to_cellId_bimap = 
				HashBiMap.create(location_to_cellId);
		BiMap<Integer, Location> cellId_to_location_bimap = location_to_cellId_bimap.inverse();

		String NEW_LINE_SEPARATOR = "\n";
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);		
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		try {

			//initialize FileWriter object
			fileWriter = new FileWriter(CELLID_LOCATIONS_TABLE);

			//initialize CSVPrinter object 
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			//Create CSV file header
			csvFilePrinter.printRecord("runtimeId", "cellId", "latitude", "longitude");
			SortedSet<Integer> sortedCellIds = new TreeSet<Integer>(cellId_to_location_bimap.keySet());
			for(int cellId: sortedCellIds){
				Location location = cellId_to_location_bimap.get(cellId);
				csvFilePrinter.printRecord(
						runtimeId,
						cellId,
						location.latitude,
						location.longitude);
			}

			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}

		return location_to_cellId;
	}

	public static long foo = System.nanoTime();

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {    	

		int numberOfCalls = (int)1e7;
		int numberOfDevices = 1000;
		boolean doExport = false;
		boolean doImport = false;
		if (args.length > 0) {
			try {
				ArrayList<String> options = new ArrayList<>(Arrays.asList(args));
				if (options.indexOf("-import") != -1) {
					doImport = true;
				}
				if (options.indexOf("-export") != -1) {
					doExport = true;
				}
				if (options.indexOf("-d") != -1) {
					numberOfDevices = Integer.parseInt(options.get(options.indexOf("-d") + 1));
				}
				if (options.indexOf("-c") != -1) {
					numberOfCalls = Integer.parseInt(options.get(options.indexOf("-c") + 1));
				}

			} catch (Throwable t) {
				System.out.println("Error parsing command line arguments!");
				System.exit(1);
			} 
		}

		ArrayList<Customer> customers = new ArrayList<>();

		Type listOfCustomers = new TypeToken<List<Customer>>(){}.getType();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		LocalDateTime start_time = LocalDateTime.now().minusMonths(3);

		HashMap<Location, Integer> location_to_cellId = null;
		
		if (doImport) {
			try {
				customers = gson.fromJson(readFile(DEVICES_LIST), listOfCustomers);
			} catch (IOException | JsonSyntaxException e)  {
				System.out.println("Error reading devices.json : " + e );
			}
		} else {
			// generate locations for homes, works and other places among all cities
			Map<CITY,List<Location>> works = Utils.generateLocationMap(numberOfDevices/10);
			Map<CITY,List<Location>> others = Utils.generateLocationMap(numberOfDevices);
			Map<CITY,List<Location>> homes = Utils.generateLocationMap(numberOfDevices/2);

			List<Map<CITY, List<Location>>> locationPools =
					new ArrayList<Map<CITY,List<Location>>>();
			locationPools.add(works);
			locationPools.add(others);
			locationPools.add(homes);

			location_to_cellId = generateCellIdMappingFile(works, others, homes);

			for (int i = 0; i < numberOfDevices; i++) {
				// pick random set of home, work and other from one city
				CITY city = Utils.randomCity();
				LocationSet locationSet = new LocationSet(city,
						Utils.pickRandomElement(homes.get(city)),
						Utils.pickRandomElement(works.get(city)),
						Utils.pickRandomElement(others.get(city)));

				customers.add(Customer.generatePhone(start_time, locationSet));
			}
		}

		// sorting based on activity value. 
		customers.sort(null);

		try (PrintWriter writer = new PrintWriter(OUTPUT_FILE, "UTF-8")) {

			String csv_header_line = Joiner.on(Constants.DELIMITER).join(CSV_HEADER);
			writer.println(csv_header_line);

			Customer customer1;
			Customer customer2;

			for (int j = 0; j < numberOfCalls; j++) {

				// since customers are sorted based on activity of each customer
				// we'll pick customers with more activity more frequently 
				customer1 = Utils.pickElementWithTriangularDestribution(customers);
				customer2 = Utils.pickElementWithTriangularDestribution(customers);

				if (customer1.equals(customer2)) {
					continue; // don't want to make customers to call themselves
				} 

				Customer.CallType type = Utils.generateCallType();
				int duration;
				int length;
				if (type == Customer.CallType.call) {
					length = 0;
					duration = Utils.generateDuration();
				} else {
					duration = 0;
					length = Utils.generateLength();
				}
				writer.println(customer1.call(customer2, type, duration, length)
						.toString(location_to_cellId));                
			}


		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not create the file");
		} catch (UnsupportedEncodingException uee) {
			System.out.println("Unsupported encoding exception");
		} 

		if (doExport) {
			try (PrintWriter writer = new PrintWriter(DEVICES_LIST, "UTF-8")) {
				writer.println(gson.toJson(customers, listOfCustomers));
			} catch (FileNotFoundException fnfe) {
				System.out.println("Could not create the file");
			} catch (UnsupportedEncodingException uee) {
				System.out.println("Unsupported encoding exception");
			}
		}

		System.out.println("Done in " + (System.nanoTime() - starttime)/1e6 + " ms");


	}

}
