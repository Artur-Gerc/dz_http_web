import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.dz.netology.phone_book_tdd.PhoneBook;

public class PhoneBookTest {
    private PhoneBook phoneBook;

    @BeforeEach
    public void setUp(){
        phoneBook = new PhoneBook();
    }

    @Test
    public void addTest(){
        phoneBook.add("Алексей", "12345");
        int count = phoneBook.add("Алексей", "54321");
        Assertions.assertEquals(1, count);
    }

    @Test
    public void findByNumberTest(){
        String number = "12345";
        String expected = "Виталий";
        phoneBook.add(expected, number);
        String actual = phoneBook.findByNumber(number);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void findByNameTest(){
        String expectedNumber = "12345";
        String name = "Семен";
        phoneBook.add(name, expectedNumber);
        String actual = phoneBook.findByName(name);
        Assertions.assertEquals(expectedNumber,actual);
    }

    @Test
    public void printAllNamesTest(){
        phoneBook.add("Алексей", "12345");
        phoneBook.add("Василий", "54321");
        phoneBook.add("Иван", "11111");
        phoneBook.add("Анна", "77777");

        String expected = "Алексей;\n" +
                "Анна;\n" +
                "Василий;\n" +
                "Иван;\n";

        String actual = phoneBook.printAllNames();

        Assertions.assertEquals(expected, actual);
    }
}
