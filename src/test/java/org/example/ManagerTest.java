package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    static ArrayList<Manager.Purchase> spendingReference = new ArrayList<>();
    Manager manager;

    @BeforeAll
    static void beforeAll(){
        SimpleDateFormat parser = new SimpleDateFormat("yyyy.MM.dd");
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        Calendar date3 = Calendar.getInstance();
        Calendar date4 = Calendar.getInstance();
        Calendar date5 = Calendar.getInstance();
        Calendar date6 = Calendar.getInstance();
        Calendar date7 = Calendar.getInstance();
        Calendar date8 = Calendar.getInstance();
        Calendar date9 = Calendar.getInstance();
        try {
            date1.setTime(parser.parse("2022.08.02"));
            date2.setTime(parser.parse("2023.01.15"));
            date3.setTime(parser.parse("2021.03.31"));
            date4.setTime(parser.parse("2023.02.24"));
            date5.setTime(parser.parse("2022.08.02"));
            date6.setTime(parser.parse("2022.09.17"));
            date7.setTime(parser.parse("2022.12.12"));
            date8.setTime(parser.parse("2022.02.24"));
            date9.setTime(parser.parse("2023.04.24"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Manager.Purchase purchase1 = new Manager.Purchase("булка", "еда", 200, date1);
        Manager.Purchase purchase2 = new Manager.Purchase("колбаса", "еда", 300, date2);
        Manager.Purchase purchase3 = new Manager.Purchase("сухарики", "еда", 20, date3);
        Manager.Purchase purchase4 = new Manager.Purchase("курица", "еда", 500, date4);
        Manager.Purchase purchase5 = new Manager.Purchase("тапки", "одежда", 1000, date5);
        Manager.Purchase purchase6 = new Manager.Purchase("шапка", "одежда", 3500, date6);
        Manager.Purchase purchase7 = new Manager.Purchase("мыло", "быт", 600, date7);
        Manager.Purchase purchase8 = new Manager.Purchase("акции", "финансы", 12000, date8);
        Manager.Purchase purchase9 = new Manager.Purchase("бипки", "другое", 800, date9);
        spendingReference.add(purchase1);
        spendingReference.add(purchase2);
        spendingReference.add(purchase3);
        spendingReference.add(purchase4);
        spendingReference.add(purchase5);
        spendingReference.add(purchase6);
        spendingReference.add(purchase7);
        spendingReference.add(purchase8);
        spendingReference.add(purchase9);
    }



    @Test
    void addPurchase() {
        manager = new Manager();
        String[] args = {"булка:2022.08.02:200", "колбаса:2023.01.15:300", "сухарики:2021.03.31:20", "курица:2023.02.24:500", "тапки:2022.08.02:1000", "шапка:2022.09.17:3500", "мыло:2022.12.12:600", "акции:2022.02.24:12000", "бипки:2023.04.24:800"};
        for (String arg : args){
            String[] splitArgs= arg.split(":");
            manager.addPurchase(splitArgs[0],splitArgs[1], Long.parseLong(splitArgs[2]));
        }
               for (int i = 0; i < spendingReference.size(); i++){
            Assertions.assertEquals(manager.spending.get(i).getTitle(),spendingReference.get(i).getTitle());
            Assertions.assertEquals(manager.spending.get(i).getCategory(),spendingReference.get(i).getCategory());
            Assertions.assertEquals(manager.spending.get(i).getSum(),spendingReference.get(i).getSum());
            Assertions.assertTrue(manager.spending.get(i).getDate().equals(spendingReference.get(i).getDate()));
        }
        File file = new File ("categories.tsv");
        assertTrue(file.canRead());
    }

    @ParameterizedTest
    @CsvSource(value = {"булка,еда", "колбаса,еда", "сухарики,еда", "курица,еда", "тапки,одежда", "шапка,одежда", "мыло,быт", "акции,финансы", "бипки,другое"}, delimiter = ',')
    void getCatFromCSV(String input, String expected) {
        File file = new File ("categories.tsv");
        assertTrue(file.canRead());
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("булка","еда");
        testMap.put("колбаса","еда");
        testMap.put("сухарики","еда");
        testMap.put("курица","еда");
        testMap.put("тапки","одежда");
        testMap.put("шапка","одежда");
        testMap.put("мыло","быт");
        testMap.put("акции","финансы");
        if (!input.equals("бипки")){
            assertTrue(testMap.containsKey(input));
        } else {
            assertFalse(testMap.containsKey(input));
        }
        String output = manager.getCatFromCSV(input);
        assertEquals(expected, output);
    }

    @Test
    void getMaxCategory() {
        manager = new Manager();
        String[] args = {"булка:2022.08.02:200", "колбаса:2023.01.15:300", "сухарики:2021.03.31:20", "курица:2023.02.24:500", "тапки:2022.08.02:1000", "шапка:2022.09.17:3500", "мыло:2022.12.12:600", "акции:2022.02.24:12000", "бипки:2023.04.24:800"};
        for (String arg : args){
            String[] splitArgs= arg.split(":");
            manager.addPurchase(splitArgs[0],splitArgs[1], Long.parseLong(splitArgs[2]));
        }
        Assertions.assertEquals(12000, manager.getMaxCategory().get().getValue());
        Assertions.assertEquals("финансы", manager.getMaxCategory().get().getKey());
        File file = new File ("categories.tsv");
        assertTrue(file.canRead());
        }

    }
