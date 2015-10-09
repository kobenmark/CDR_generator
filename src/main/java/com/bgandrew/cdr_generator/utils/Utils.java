package com.bgandrew.cdr_generator.utils;

import com.bgandrew.cdr_generator.model.Customer;
import com.bgandrew.cdr_generator.model.Location;
import static com.bgandrew.cdr_generator.model.LocationSet.CITY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Utility class to generate different Customer attributes and other stuff
 *
 */
public class Utils {
    
    public static byte b = 127;
    
    public static String USA_MCC = "310";
    
    public static Random random = new Random();
    
    
    public static String USA_CC = "1";
    // for IMSI generating 
    // taken from https://en.wikipedia.org/wiki/List_of_mobile_network_codes_in_the_United_States
    // TODO turn it to a map with provider name as a value; add more functioning MNSs
    public static List<String> USA_MNSs = Collections.unmodifiableList(Arrays.asList(new String[] {
        "012","015","016","020",
        "026","030","032","034",
        "040","060","070","080",
        "090","100","110","120",
        "130","140","150","170",
        "180","190","260","311",
        "320","340","370","380",
        "390","400","410","420",
        "430","440","450","460",
        "470","480","490","500",
        "510","520","530","540",
        "560","570","590","610",
        "620","630","640","650",
        "680","690","710","740",
        "760","770","790","800",
        "830","840","850","870",
        "880","890","900","910",
        "930","940","950","960",
        "970","980","990","999",}));
    
    // for IMEI generating
    // taken from https://en.wikipedia.org/wiki/Type_Allocation_Code
    // TODO turn it to a map with vendor+model name as a value; add more functioning TACs
    public static List<String> TACs = Collections.unmodifiableList(Arrays.asList(new String[] 
    {"01124500", "01130000", "01136400", "01154600", "01161200",
     "01174400","01180800","01181200","01193400",
     "01194800","01215800","01215900","01216100",
     "01226800","01233600","01233700","01233800",
     "01241700","01243000","01253600","01254200",
     "01300600","01326300","01332700","01388300",
     "35089080","35099480","35148420","35148820",
     "35151304","35154900","35171005","86723902",
     "35174605","35191405","35226005","35238402",
     "35274901","35291402","35316004","35316605",
     "35332705","35328504","35351200","35357800",
     "35376800","35391805","35405600","35421803",
     "35433004","35450502","35511405","35524803",
     "35566600","35569500","35679404","35685702",
     "35693803","35694603","35699601","35700804",
     "35714904","35733104","35739804","35744105",
     "35788104","35803106","35824005","35828103",
     "35836800","35837501","35837800","35850000",
     "35851004","35853704","35869205","35876105",
     "35896704","35902803","35909205","35918804",
     "35920605","35929005","35933005","35935003",
     "35972100","35974101","35979504","86107402",
     "86217001"}));    
    
    
    // Los Angeles coordinates
    
    
    
    private static final double CITY_RADIUS = 0.1; // about 11 kilometers
    public static final Map<CITY, Location> CITY_LOCATIONS;
    
        // Los Angeles codes
    public static Map<CITY,List<String>> USA_NDCs;
    
    static {
        Map<CITY, Location> locations = new HashMap<>();
        locations.put(CITY.LA, new Location(33.938234, -118.106140));
        locations.put(CITY.SD, new Location(32.799612, -117.140673));
        locations.put(CITY.SJ, new Location(37.317485, -121.893148));
        CITY_LOCATIONS = Collections.unmodifiableMap(locations);
        
        Map<CITY,List<String>> ndcs = new HashMap<>();
        ndcs.put(CITY.LA, Collections.unmodifiableList(Arrays.asList(
                            "213", "310", "323",
                            "424", "626", "818",
                            "661","747")));
        ndcs.put(CITY.SD,  Collections.unmodifiableList(Arrays.asList("619","858")));
        ndcs.put(CITY.SJ,  Collections.unmodifiableList(Arrays.asList("408")));
        USA_NDCs = Collections.unmodifiableMap(ndcs);
    }

    
    private static String randomNDigitsNumber (int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    public static <T> T pickRandomElement (List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int index = random.nextInt(list.size());
        return list.get(index);
    }
    
    
    /**
     * @see http://stackoverflow.com/a/5969748
     * @param <T>
     * @param list
     * @return 
     */ 
    public static <T> T pickElementWithTriangularDestribution(List<T> list) {
        final int size = list.size();
        final int y = random.nextInt(size);
        final int x = random.nextInt(size);
        return list.get((y < x ) ? x : (size - x - 1));
       
        
    }
    
    
    public static long generateIMSI() {
        return Long.decode(USA_MCC + pickRandomElement(USA_MNSs) + randomNDigitsNumber(10));
        
    }
    
    
    // TODO do we need check sum?
    public static long generateIMEI() {
        return Long.decode(pickRandomElement(TACs) + randomNDigitsNumber(6) + "0");
    }
    
    public static long generateMSISDN(CITY city) {
        return Long.decode(USA_CC + pickRandomElement(USA_NDCs.get(city)) + randomNDigitsNumber(7));
        
    }
    
    public static Customer.CallType generateCallType() {
        return random.nextInt(3) == 2 ? Customer.CallType.SMS : Customer.CallType.Voice;
    }
    
    public static int generateDuration() {
        return 90 + (random.nextInt(160) -80);
    } 
    
    public static int generateLength() {
        return 40 + (random.nextInt(30) -15);
    }
    
    private static Location randomLocationInArea(Location center, double radius) {
        //using polar coordinates to generate random point in a circle

        double r = radius * Math.sqrt(random.nextDouble());
        double f = 2 * Math.PI * random.nextDouble();
        
        double x = r*Math.cos(f);
        double y = r*Math.sin(f);
        
        return new Location(center.latitude + x, center.longitude + y);
    } 
    
    public static Location randomLocationInCity(CITY city){
        return randomLocationInArea(CITY_LOCATIONS.get(city), CITY_RADIUS);
    } 
    
    public static CITY randomCity() {
        return pickRandomElement(Arrays.asList(CITY.values()));
    }
    
        
    public static Map<CITY,List<Location>> generateLocationMap(int numberOfLocations) {
        
        if (numberOfLocations < 1)
            numberOfLocations = 1;
                
        Map<CITY,List<Location>> map = new HashMap<>();
        // this is workaround
        // we need to be sure that at least one location in each city exists
        // otherwise the program can crash with exception 
        for (CITY city : CITY.values()) {

            ArrayList<Location> list = new ArrayList<>();
            list.add(randomLocationInCity(city));
            map.put(city, list);
        }
        // fill each arraylist with random places in corresponding city
        for (int i = 0; i < numberOfLocations; i++) {
            CITY city = Utils.randomCity();
            map.get(city).add(Utils.randomLocationInCity(city));    
        }
        
        return map;
    }
    
    // return true with probapability p (if p > 1 always true, if p < 0 always false)
    public static boolean bernoulliTrial(float p) {
        return random.nextFloat() < p;
    }
    
    public static void main(String[] args) {
        
        System.out.println(random.nextInt(0));
        //System.out.println("IMEI " +  generateIMEI());
        //System.out.println("IMSI " + generateIMSI());
        //System.out.println("MSISDN " + generateMSISDN());
    }
}
