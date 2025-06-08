package org.example;

import java.sql.*;
import java.util.Scanner;

public class DiseaseDatabaseManager {

    static final String URL = ""; //url
    static final String USER = "";  //username
    static final String PASSWORD = "";//password

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the database successfully!");

            while (true) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Manage Disease_Info");
                System.out.println("2. Manage Disease_Outbreaks");
                System.out.println("3. Manage Treatment_Table");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        manageDiseaseInfo(conn);
                        break;
                    case 2:
                        manageDiseaseOutbreaks(conn);
                        break;
                    case 3:
                        manageTreatmentTable(conn);
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }

    private static void manageDiseaseInfo(Connection conn) throws SQLException {
        while (true) {
            System.out.println("\nManage Disease_Info Table:");
            System.out.println("1. View all diseases");
            System.out.println("2. Add new disease");
            System.out.println("3. Edit existing disease");
            System.out.println("4. Delete disease");
            System.out.println("5. Go back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewTable(conn, "Disease_Info");
                    break;
                case 2:
                    insertDisease(conn);
                    break;
                case 3:
                    editDisease(conn);
                    break;
                case 4:
                    deleteFromTable(conn, "Disease_Info");
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void manageDiseaseOutbreaks(Connection conn) throws SQLException {
        while (true) {
            System.out.println("\nManage Disease_Outbreaks Table:");
            System.out.println("1. View all outbreaks");
            System.out.println("2. Add new outbreak");
            System.out.println("3. Edit existing outbreak");
            System.out.println("4. Delete outbreak");
            System.out.println("5. Go back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewTable(conn, "Disease_Outbreaks");
                    break;
                case 2:
                    insertOutbreak(conn);
                    break;
                case 3:
                    editOutbreak(conn);
                    break;
                case 4:
                    deleteFromTable(conn, "Disease_Outbreaks");
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void manageTreatmentTable(Connection conn) throws SQLException {
        while (true) {
            System.out.println("\nManage Treatment_Table:");
            System.out.println("1. View all treatments");
            System.out.println("2. Add new treatment");
            System.out.println("3. Edit existing treatment");
            System.out.println("4. Delete treatment");
            System.out.println("5. Go back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewTable(conn, "Treatment_Table");
                    break;
                case 2:
                    insertTreatment(conn);
                    break;
                case 3:
                    editTreatment(conn);
                    break;
                case 4:
                    deleteFromTable(conn, "Treatment_Table");
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewTable(Connection conn, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }


    private static void insertDisease(Connection conn) throws SQLException {
        int id = getNextDiseaseId(conn);
        System.out.println("New disease_id auto-assigned: " + id);

        String name = readNonEmptyString("Enter disease_name: ");
        String transmission = readNonEmptyString("Enter transmission: ");
        String symptoms = readNonEmptyString("Enter symptoms: ");
        int incubation = readInt("Enter incubation_period_days (INTEGER): ");
        String origin = readNonEmptyString("Enter origin: ");

        String sql = "INSERT INTO Disease_Info (disease_id, disease_name, transmission, symptoms, incubation_period_days, origin) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, transmission);
            pstmt.setString(4, symptoms);
            pstmt.setInt(5, incubation);
            pstmt.setString(6, origin);

            pstmt.executeUpdate();
            System.out.println("Disease added successfully.");
        }
    }



    private static void insertOutbreak(Connection conn) throws SQLException {
        int diseaseId;
        while (true) {
            diseaseId = readInt("Enter existing disease_id (INT): ");
            if (recordExists(conn, "Disease_Info", "disease_id", diseaseId)) {
                break;
            } else {
                System.out.println("Disease ID does not exist. Please enter a valid existing disease_id.");
            }
        }

        String region = readNonEmptyString("Enter region: ");
        String country = readNonEmptyString("Enter country: ");
        int totalCases = readInt("Enter total_cases (INTEGER): ");
        int estDeaths = readInt("Enter est_deaths (INTEGER): ");

        String sql = "INSERT INTO Disease_Outbreaks (disease_id, region, country, total_cases, est_deaths) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diseaseId);
            pstmt.setString(2, region);
            pstmt.setString(3, country);
            pstmt.setInt(4, totalCases);
            pstmt.setInt(5, estDeaths);

            pstmt.executeUpdate();
            System.out.println("Outbreak added successfully.");
        }
    }



    private static void insertTreatment(Connection conn) throws SQLException {
        int diseaseId;
        while (true) {
            diseaseId = readInt("Enter existing disease_id (INTEGER): ");
            if (recordExists(conn, "Disease_Info", "disease_id", diseaseId)) {
                break;
            } else {
                System.out.println("Disease ID does not exist. Please enter a valid existing disease_id.");
            }
        }

        int incubation = readInt("Enter incubation_period_days (INTEGER): ");
        String treatment = readNonEmptyString("Enter treatment_type: ");
        String vaccine = readNonEmptyString("Enter vaccine (Yes/No): ");
        float vaccRate = readFloat("Enter vaccination_rate_pct (FLOAT 0-100): ");
        float longTerm = readFloat("Enter long_term_effects_pct (FLOAT 0-100): ");

        String sql = "INSERT INTO Treatment_Table (disease_id, incubation_period_days, treatment_type, vaccine, vaccination_rate_pct, long_term_effects_pct) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diseaseId);
            pstmt.setInt(2, incubation);
            pstmt.setString(3, treatment);
            pstmt.setString(4, vaccine);
            pstmt.setFloat(5, vaccRate);
            pstmt.setFloat(6, longTerm);

            pstmt.executeUpdate();
            System.out.println("Treatment added successfully.");
        }
    }



    private static void deleteFromTable(Connection conn, String tableName) throws SQLException {
        System.out.println("Enter the ID of the row to delete:");
        int id = scanner.nextInt();
        scanner.nextLine();

        String idColumn = "disease_id"; // default
        if (tableName.equalsIgnoreCase("Disease_Outbreaks")) {
            idColumn = "outbreak_id";
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Row deleted successfully.");
            } else {
                System.out.println("No row found with that ID.");
            }
        }
    }

    private static void editDisease(Connection conn) throws SQLException {
        int id = readInt("Enter disease_id to edit: ");
        if (!recordExists(conn, "Disease_Info", "disease_id", id)) {
            System.out.println("No disease found with that ID.");
            return;
        }

        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. disease_name");
        System.out.println("2. transmission");
        System.out.println("3. symptoms");
        System.out.println("4. incubation_period_days (INTEGER)");
        System.out.println("5. origin");

        int choice = readInt("Enter choice: ");

        String column = "";
        switch (choice) {
            case 1: column = "disease_name"; break;
            case 2: column = "transmission"; break;
            case 3: column = "symptoms"; break;
            case 4: column = "incubation_period_days"; break;
            case 5: column = "origin"; break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        String newValue;
        if (choice == 4) {
            newValue = Integer.toString(readInt("Enter new incubation_period_days (INTEGER): "));
        } else {
            newValue = readNonEmptyString("Enter new value: ");
        }

        String sql = "UPDATE Disease_Info SET " + column + " = ? WHERE disease_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newValue);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Disease updated successfully.");
            } else {
                System.out.println("No disease found with that ID.");
            }
        }
    }



    private static void editOutbreak(Connection conn) throws SQLException {
        int id = readInt("Enter outbreak_id to edit: ");
        if (!recordExists(conn, "Disease_Outbreaks", "outbreak_id", id)) {
            System.out.println("No outbreak found with that ID.");
            return;
        }

        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. disease_id (INTEGER)");
        System.out.println("2. region (STRING)");
        System.out.println("3. country (STRING)");
        System.out.println("4. total_cases (INTEGER)");
        System.out.println("5. est_deaths (INTEGER)");

        int choice = readInt("Enter choice: ");

        String column = "";
        switch (choice) {
            case 1: column = "disease_id"; break;
            case 2: column = "region"; break;
            case 3: column = "country"; break;
            case 4: column = "total_cases"; break;
            case 5: column = "est_deaths"; break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        String sql = "UPDATE Disease_Outbreaks SET " + column + " = ? WHERE outbreak_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (choice == 1 || choice == 4 || choice == 5) {
                pstmt.setInt(1, readInt("Enter new value (INTEGER): "));
            } else {
                pstmt.setString(1, readNonEmptyString("Enter new value (STRING): "));
            }
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Outbreak updated successfully.");
            } else {
                System.out.println("No outbreak found with that ID.");
            }
        }
    }


    private static void editTreatment(Connection conn) throws SQLException {
        int id = readInt("Enter disease_id to edit treatment: ");
        if (!recordExists(conn, "Treatment_Table", "disease_id", id)) {
            System.out.println("No treatment found with that disease_id.");
            return;
        }

        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. incubation_period_days (INTEGER)");
        System.out.println("2. treatment_type");
        System.out.println("3. vaccine (Yes/No)");
        System.out.println("4. vaccination_rate_pct (FLOAT)");
        System.out.println("5. long_term_effects_pct (FLOAT)");

        int choice = readInt("Enter choice: ");

        String column = "";
        switch (choice) {
            case 1: column = "incubation_period_days"; break;
            case 2: column = "treatment_type"; break;
            case 3: column = "vaccine"; break;
            case 4: column = "vaccination_rate_pct"; break;
            case 5: column = "long_term_effects_pct"; break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        String sql = "UPDATE Treatment_Table SET " + column + " = ? WHERE disease_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (choice == 1) {
                pstmt.setInt(1, readInt("Enter new incubation_period_days (INTEGER): "));
            } else if (choice == 4 || choice == 5) {
                pstmt.setFloat(1, readFloat("Enter new value (FLOAT): "));
            } else {
                pstmt.setString(1, readNonEmptyString("Enter new value: "));
            }
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Treatment updated successfully.");
            } else {
                System.out.println("No treatment found with that ID.");
            }
        }
    }

    // Read and validate a positive INT (e.g., INT columns)
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Please enter a positive integer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }
    // Read and validate a positive BIGINT (e.g., BIGINT columns)
    private static long readBigInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Please enter a positive whole number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
    // Read and validate non-empty string (e.g., VARCHAR, TEXT columns)
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            } else {
                System.out.println("Input cannot be empty. Please enter a valid text.");
            }
        }
    }
    // Read and validate percentage (0-100, e.g., DECIMAL columns)
    private static double readPercentage(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0 && value <= 100) {
                    return value;
                } else {
                    System.out.println("Please enter a number between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a decimal number.");
            }
        }
    }
    // Get the maximum disease_id and add 1
    private static int getNextDiseaseId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(disease_id) FROM Disease_Info";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt(1); // If no rows, returns 0
                return maxId + 1;
            } else {
                return 1; // Starting ID if table is empty
            }
        }
    }

    private static boolean recordExists(Connection conn, String tableName, String idColumn, int id) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + idColumn + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static float readFloat(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                float value = Float.parseFloat(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid float number.");
            }
        }
    }


}
