package com.company;

import java.sql.*;
import java.util.*;

public class Datasource {
    public static final String DB_NAME = "fiszki.db";
    public static final String CONNECTION = "jdbc:sqlite:C:\\Users\\Patryk\\Desktop\\Fiszki\\" + DB_NAME;

    public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS categories " + "(_id INTEGER, name TEXT)";
    public static final String CREATE_TABLE_WORD = "CREATE TABLE IF NOT EXISTS word " + "(id_cat INTEGER, login TEXT, polishWord TEXT, englishWord)";
    public static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE IF NOT EXISTS account " + "(login TEXT, password INTEGER)";


    public static final String TABLE_CATEGORY_NAME = "categories";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "name";

    public static final String TABLE_WORD_NAME = "word";
    public static final String COLUMN_WORD_ID = "id_cat";
    public static final String COLUMN_WORD_LOGIN = "login";
    public static final String COLUMN_WORD_POLISH_WORD = "polishWord";
    public static final String COLUMN_WORD_ENGLISH_WORD = "englishWord";

    public static final String TABLE_ACCOUNT_NAME = "account";
    public static final String COLUMN_ACCOUNT_LOGIN = "login";
    public static final String COLUMN_ACCOUNT_PASSWORD = "password";


    public static final String INSERT_WORD = "INSERT INTO word(id_cat,login ,polishWord, englishWord) VALUES(?, ?, ?, ?)";
    public static final String INSERT_CATEGORIES = "INSERT INTO " + TABLE_CATEGORY_NAME +
            "(" + COLUMN_CATEGORY_ID + ", " + COLUMN_CATEGORY_NAME + ") " + " VALUES(?, ?)";
    public static final String INSERT_ACCOUNT = "INSERT INTO " + TABLE_ACCOUNT_NAME + "(" + COLUMN_ACCOUNT_LOGIN + ", " + COLUMN_ACCOUNT_PASSWORD + ") "
            + "VALUES(?, ?)";


    public static final String QUERY_WORD = "SELECT " + COLUMN_WORD_POLISH_WORD + ", " + COLUMN_WORD_ENGLISH_WORD + " FROM " + TABLE_WORD_NAME +
            " WHERE " + COLUMN_WORD_ID + " = ";
    public static final String QUERY_LOGIN = "SELECT " + COLUMN_ACCOUNT_LOGIN + " FROM " + TABLE_ACCOUNT_NAME;
    public static final String QUERY_ACCOUNTS = "SELECT * " + " FROM " + TABLE_ACCOUNT_NAME;
    public static final String QUERY_CATEGORIES = "SELECT * " + " FROM " + TABLE_CATEGORY_NAME;

    private PreparedStatement insertIntoWord;
    private PreparedStatement insertCategories;
    private PreparedStatement insertAccount;

    private Connection conn;

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION);
            insertIntoWord = conn.prepareStatement(INSERT_WORD);
            insertCategories = conn.prepareStatement(INSERT_CATEGORIES);
            insertAccount = conn.prepareStatement(INSERT_ACCOUNT);

            return true;
        } catch (SQLException e) {
            System.out.println("Open Fail: " + e.getMessage());
        }
        return false;
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (insertIntoWord != null) {
                insertIntoWord.close();
            }
            if (insertCategories != null) {
                insertCategories.close();
            }
            if (insertAccount != null) {
                insertAccount.close();
            }
        } catch (SQLException e) {
            System.out.println("Close failed: " + e.getMessage());
        }
    }

    public boolean createTable() {
        try {
            Statement st = conn.createStatement();
            st.execute(CREATE_TABLE_CATEGORY);
            st.execute(CREATE_TABLE_WORD);
            st.execute(CREATE_TABLE_ACCOUNT);
            return true;
        } catch (SQLException e) {
            System.out.println("Table not create");
        }
        return false;
    }

    private List<String> getExistingLogin() {
        try {
            Statement st = conn.createStatement();
            ResultSet results = st.executeQuery(QUERY_LOGIN);
            List<String> logins = new ArrayList<>();
            while (results.next()) {
                logins.add(results.getString(1));
            }
            return logins;
        } catch (SQLException e) {
            e.getMessage();
            return null;
        }
    }

    public boolean createAccount() {
        List<String> log = getExistingLogin();
        Scanner scann = new Scanner(System.in);
        while (true) {
            System.out.println("Enter login");
            String login = scann.nextLine();
            if (log == null || !log.contains(login)) {
                System.out.println("Enter password");
                String pass = scann.nextLine();
                System.out.println("Enter second time password");
                String pass2 = scann.nextLine();
                if (pass.equals(pass2)) {
                    int passHash = pass.hashCode();
                    try {
                        insertAccount.setString(1, login);
                        insertAccount.setInt(2, passHash);
                        insertAccount.execute();
                        return true;
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                } else {
                    System.out.println("Password do not match");
                }
            } else {
                System.out.println("Login is exist, Enter different");
            }
        }
    }

    private String logIn() {

        List<Account> accounts = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter login: ");
        String login = scan.nextLine();
        System.out.println("Enter password: ");
        String pass = scan.nextLine();


        try {
            Statement st = conn.createStatement();
            ResultSet results = st.executeQuery(QUERY_ACCOUNTS);
            while (results.next()) {
                Account ac = new Account();
                ac.setLogin(results.getString(1));
                ac.setPassword(results.getInt(2));
                accounts.add(ac);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        int hash = pass.hashCode();
        if(checkPassword(hash,login)) {
            System.out.println("Log in succesfully");
            return login;
        } else {
            System.out.println("Login or password is wrong");
        }
        return null;
    }

    private boolean checkPassword(int hash, String login) {
        StringBuilder sb = new StringBuilder(QUERY_ACCOUNTS);
        sb.append(" WHERE ");
        sb.append(COLUMN_ACCOUNT_LOGIN);
        sb.append(" = ");
        sb.append("\"");
        sb.append(login);
        sb.append("\"");
        sb.append(" AND ");
        sb.append(COLUMN_ACCOUNT_PASSWORD);
        sb.append(" = ");
        sb.append(hash);

        try {
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery(sb.toString());
            while(result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    private boolean insertCategories() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj nazwe kategorii: ");
        String name = scanner.nextLine();
        try {
            insertCategories.setInt(1, (getCount() + 1));
            insertCategories.setString(2, name);
            insertCategories.execute();
            return true;
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    private int getCount() {
        String sql = "SELECT COUNT(*) AS count FROM " + TABLE_CATEGORY_NAME;
        try (Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            int count = result.getInt("count");


            return count;
        } catch (SQLException e) {
            System.out.println("QUERY DIDN'T WORK " + e.getMessage());
            return -1;
        }
    }

    private void insertWords(String login) {

        Scanner scanner = new Scanner(System.in);
        String anwser = "y";
        boolean gate = true;
        while (gate) {
            System.out.println("Czy chcesz dodać fishke?");
            anwser = scanner.nextLine();
            if (anwser.equals("y")) {

                try {
                    System.out.println("Podaj kategorie: ");
                    printCategories();
                    int category = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Podaj słówko: ");
                    String polishWord = scanner.nextLine();
                    System.out.println("Podaj jego tłumaczenie: ");
                    String englishWord = scanner.nextLine();

                    insertIntoWord.setInt(1, category);
                    insertIntoWord.setString(2, login);
                    insertIntoWord.setString(3, polishWord);
                    insertIntoWord.setString(4, englishWord);
                    insertIntoWord.execute();
                    System.out.println();
                } catch (SQLException e) {
                    System.out.println("Added failed:  " + e.getMessage());
                }
            } else {
                gate = false;
            }
        }
    }

    private List<Map<String, String>> fillTheMap(int category, String login) {
        Words wordWithTranslate = new Words();
        StringBuilder sb = new StringBuilder(QUERY_WORD);

        sb.append(category);
        sb.append(" AND ");
        sb.append(COLUMN_WORD_LOGIN);
        sb.append(" = ");
        sb.append("\"");
        sb.append(login);
        sb.append("\"");
        try {
            Statement st = conn.createStatement();
            ResultSet results = st.executeQuery(sb.toString());
            List<Map<String, String>> words = new ArrayList<>();
            while (results.next()) {
                Map<String, String> word = new HashMap<>();
                word.put(results.getString(1), results.getString(2));
                if (!words.contains(word)) {
                    words.add(word);
                }
            }
            wordWithTranslate.setWordsWithTranslate(words);
            if (wordWithTranslate == null) {
                System.out.println("The is array is empty : ");
            }
            return wordWithTranslate.getWordsWithTranslate();

        } catch (SQLException e) {
            System.out.println("Some went wrong" + e.getMessage());
        }
        return null;
    }

    private int randomWord(List<Map<String, String>> words) {
        Random rand = new Random();
        int number;
        if (words.size() > 1) {
            number = rand.nextInt(words.size());
        } else if (words.size() == 1) {
            number = 0;
        } else {

            return -1;
        }
        return number;

    }

    private void test(String login) {
        Scanner scan = new Scanner(System.in);
        boolean gate = false;
        System.out.println("Podaj kategorie(liczbe): ");
        System.out.println("Dostepne: ");
        printCategories();
        int count = 0;
        while (!gate) {
            int category = scan.nextInt();
            if (category <= getCount()) {
                List<Map<String, String>> words = fillTheMap(category, login);
                System.out.println("Podaj liczbe słówek: ");
                int limit = scan.nextInt();
                scan.nextLine();
                while (count < limit) {
                    int numberOfWord = randomWord(words);
                    if (numberOfWord == -1) {
                        System.out.println("Koniec słówek");
                        return;
                    }
                    Map<String, String> word = words.get(numberOfWord);
                    System.out.println("Podaj tłumaczenie dla słowa: " + word.keySet().toString());
                    StringBuilder anwser = new StringBuilder();
                    anwser.append("[");
                    anwser.append(scan.nextLine());
                    anwser.append("]");
                    if (checkIfCorrect(word, anwser)) {
                        words.remove(numberOfWord);
                    } else {
                        continue;
                    }
                    count++;
                }

                gate = true;
            } else {
                System.out.println("Wrong number category \n" +
                        "Enter new number: ");
            }
        }

    }

    private boolean checkIfCorrect(Map<String, String> word, StringBuilder anwser) {
        if (anwser.toString().equalsIgnoreCase(word.values().toString())) {
            System.out.println("Congratulation, your translation is correct ! ");
            for (Map.Entry<String, String> w : word.entrySet()) {
                System.out.println(w);
            }
        } else {
            System.out.println("Something went wrong");
            return false;
        }
        return true;
    }


    private void printCategories() {
        try {
            Statement st = conn.createStatement();
            ResultSet results = st.executeQuery(QUERY_CATEGORIES);
            while (results.next()) {
                System.out.println(results.getInt(1) + ": " + results.getString(2));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String checkIfAccountExist(Scanner scan) {
        String anwser = "n";
        String login;
        while (anwser.equals("n")) {
            System.out.println("Do you have account? y/n ");
            anwser = scan.nextLine();
            if (anwser.equals("y")) {
                login = logIn();
                return login;
            } else {
                System.out.println("Create new Account");
                createAccount();
            }
        }
        return null;
    }

    public void fishApp() {


        Scanner scan = new Scanner(System.in);
        String login = checkIfAccountExist(scan);
        if (login == null)
            return;

        boolean gate = true;

        while (gate) {
            System.out.println("Choose option: ");

            System.out.println("1: Add new category");
            System.out.println("2: Add word to category");
            System.out.println("3: Start test");
            System.out.println("4: Quit");
            int choise = scan.nextInt();
            scan.nextLine();

            switch (choise) {
                case 1:
                    insertCategories();
                    break;
                case 2:
                    insertWords(login);
                    break;
                case 3:
                    test(login);
                    break;
                case 4:
                    gate = false;
            }
        }
    }
}

