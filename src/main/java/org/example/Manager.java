package org.example;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Manager implements Serializable{
    ArrayList<Purchase> spending = new ArrayList<>();

    static class Purchase implements Serializable {
        private final String title;
        private final String category;
        private final long sum;
        private final Calendar date;

        public Calendar getDate() {
            return date;
        }

        Purchase(String name, String cat, long price, Calendar parsedDate) {
            title = name;
            category = cat;
            sum = price;
            date = parsedDate;
        }

        protected String getCategory() {
            return category;
        }

        protected long getSum() {
            return sum;
        }

        protected String getTitle() {
            return title;
        }
    }

    public void addPurchase(String name, String date, long sum) {
        String category = getCatFromCSV(name);
        Calendar parsedDate = Calendar.getInstance();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy.MM.dd");
        try {
            parsedDate.setTime(parser.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Purchase purchase = new Purchase(name, category, sum, parsedDate);
        spending.add(purchase);
    }

    public static String getCatFromCSV(String name) {
        HashMap<String, String> purchases = new HashMap<>();
        try (FileReader csv = new FileReader("categories.tsv")) {
            CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
            CSVReader csvReader = new CSVReaderBuilder(csv).withCSVParser(parser).build();
            List<String[]> allData = csvReader.readAll();
            for (String[] row : allData) {
                String purchase = row[0];
                String category = row[1];
                if (!purchase.equals("") && !category.equals(""))
                    purchases.put(purchase, category);
            }
        } catch (IOException | CsvException ex) {
            System.out.println(ex.getMessage());
        }
        String cat;
        cat = purchases.get(name);
        if (cat == null) {
            cat = "другое";
        }
        return cat;
    }

    public Optional<Map.Entry<String, Long>> getMaxCategory() {
        HashMap<String, Long> catSums = new HashMap<>();
        for (Purchase purchase : spending) {
            String cat = purchase.getCategory();
            if (!catSums.containsKey(cat)) {
                catSums.put(cat, purchase.getSum());
            } else {
                Long old = catSums.get(cat);
                catSums.put(cat, (old + purchase.getSum()));
            }
        }
        Optional<Map.Entry<String, Long>> maxCat = catSums.entrySet().stream()
                .max((a, b) -> a.getValue().compareTo(b.getValue()));
        return maxCat;
    }
    public void saveBin(File file, Manager manager) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(manager);
            stream.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static Manager loadFromBinFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            ObjectInputStream stream = new ObjectInputStream(in);
            Manager manager = (Manager) stream.readObject();
            return manager;
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
