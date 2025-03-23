package ru.dz.netology.phone_book_tdd;

import java.util.*;

public class PhoneBook {
    private Map<String, String> nameToNumber;
    private Map<String, String> numberToName;
    private Set<String> sortedNames;

    public PhoneBook() {
        this.nameToNumber = new HashMap<>();
        this.numberToName = new HashMap<>();
        this.sortedNames = new TreeSet<>();
    }

    public int add(String name, String number) {
        if(nameToNumber.containsKey(name)){
            System.out.println("Такое имя уже есть в справочнике");
            return nameToNumber.size();
        }

        nameToNumber.put(name,number);
        numberToName.put(number,name);
        sortedNames.add(name);

        return nameToNumber.size();
    }

    public String findByNumber(String number){
        return numberToName.get(number);
    }

    public String findByName(String name){
        return nameToNumber.get(name);
    }

    public String printAllNames(){
        StringBuilder sb = new StringBuilder();
        sortedNames.forEach(name -> sb.append(name).append(";\n"));
        return sb.toString();
    }
}
