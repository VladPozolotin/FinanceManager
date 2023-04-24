package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Manager {
    ArrayList<Purchase> spending = new ArrayList<>();

    class Purchase {
        private final String title;
        private final String category;
        private final long sum;
        private final Calendar date;

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
            BufferedReader in = new BufferedReader(csv);
            String inline;
            while ((inline = in.readLine()) != null) {
                String[] read = inline.split("\t");
                String purchase = read[0].toLowerCase();
                String category = read[1].toLowerCase();
                if (!purchase.equals("") && !category.equals(""))
                    purchases.put(purchase, category);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        String cat;
        cat = purchases.get(name.toLowerCase());
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
}
