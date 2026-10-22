import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    static ArrayList<String> assignments = new ArrayList<>();

    // Save assignments
    public static void saveAssignments() {
        try (FileWriter writer = new FileWriter("assignments.txt")) {
            for (String assignment : assignments) {
                writer.write(assignment + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving assignments.");
        }
    }

    // Load assignments
    public static void loadAssignments() {
        File file = new File("assignments.txt");

        if (file.exists()) {
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    assignments.add(reader.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Error loading assignments.");
            }
        }
    }

    // View assignments
    public static void viewAssignments(String studentName) {
        System.out.println("\n========== ASSIGNMENT LIST ==========");
        System.out.println("Student: " + studentName);

        if (assignments.isEmpty()) {
            System.out.println("No assignments found.");
            return;
        }

        for (int i = 0; i < assignments.size(); i++) {
            System.out.println((i + 1) + ". " + assignments.get(i));
        }
    }

    // Assignment dashboard
    public static void showDashboard() {
        int total = assignments.size();
        int completed = 0;
        int pending = 0;

        for (String assignment : assignments) {
            if (assignment.endsWith("Status: Completed")) {
                completed++;
            } else if (assignment.endsWith("Status: Pending")) {
                pending++;
            }
        }

        double percentage = 0;

        if (total > 0) {
            percentage = completed * 100.0 / total;
        }

        System.out.println("\n======================================");
        System.out.println("         ASSIGNMENT DASHBOARD");
        System.out.println("======================================");
        System.out.println("Total Assignments     : " + total);
        System.out.println("Completed Assignments : " + completed);
        System.out.println("Pending Assignments   : " + pending);
        System.out.printf("Completion Percentage : %.1f%%%n", percentage);
        System.out.println("======================================");
    }

    // Search assignment by name
    public static void searchAssignment(Scanner sc) {
        System.out.print("Enter assignment name to search: ");
        String keyword = sc.nextLine().trim();

        boolean found = false;

        for (int i = 0; i < assignments.size(); i++) {
            String assignment = assignments.get(i);

            if (assignment.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println((i + 1) + ". " + assignment);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching assignment found.");
        }
    }

    // Delete assignment
    public static void deleteAssignment(Scanner sc) {
        if (assignments.isEmpty()) {
            System.out.println("No assignments available to delete.");
            return;
        }

        System.out.println("\n===== SELECT ASSIGNMENT TO DELETE =====");

        for (int i = 0; i < assignments.size(); i++) {
            System.out.println((i + 1) + ". " + assignments.get(i));
        }

        System.out.print("Enter assignment number to delete: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            sc.nextLine();
            return;
        }

        int number = sc.nextInt();
        sc.nextLine();

        if (number < 1 || number > assignments.size()) {
            System.out.println("Invalid assignment number!");
            return;
        }

        String removed = assignments.remove(number - 1);
        saveAssignments();

        System.out.println("Assignment deleted successfully!");
        System.out.println(removed);
    }

    // Main method
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        loadAssignments();

        System.out.println("======================================");
        System.out.println("      STUDENT ASSIGNMENT TRACKER");
        System.out.println("======================================");

        System.out.print("Enter your name: ");
        String studentName = sc.nextLine();

        int choice = 0;

        do {
            System.out.println("\n============== MENU ==============");
            System.out.println("1. Add Assignment");
            System.out.println("2. View Assignments");
            System.out.println("3. Update Assignment Status");
            System.out.println("4. View Dashboard");
            System.out.println("5. Search Assignment");
            System.out.println("6. Delete Assignment");
            System.out.println("7. Exit");
            System.out.println("==================================");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Enter a number from 1 to 7.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {
                    System.out.print("Enter subject name: ");
                    String subject = sc.nextLine().trim();

                    System.out.print("Enter assignment name: ");
                    String name = sc.nextLine().trim();

                    if (subject.isEmpty() || name.isEmpty()) {
                        System.out.println("Subject and assignment name cannot be empty.");
                        break;
                    }

                    System.out.print("Is it completed? (yes/no): ");
                    String answer = sc.nextLine().trim();

                    String status;

                    if (answer.equalsIgnoreCase("yes")) {
                        status = "Completed";
                    } else if (answer.equalsIgnoreCase("no")) {
                        status = "Pending";
                    } else {
                        System.out.println("Please enter yes or no.");
                        break;
                    }

                    String details = "Subject: " + subject
                            + " | Assignment: " + name
                            + " | Status: " + status;

                    assignments.add(details);
                    saveAssignments();

                    System.out.println("Assignment added successfully!");
                    break;
                }

                case 2:
                    viewAssignments(studentName);
                    break;

                case 3: {
                    if (assignments.isEmpty()) {
                        System.out.println("No assignments available to update.");
                        break;
                    }

                    viewAssignments(studentName);

                    System.out.print("Enter assignment number to update: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Please enter a valid number.");
                        sc.nextLine();
                        break;
                    }

                    int number = sc.nextInt();
                    sc.nextLine();

                    if (number < 1 || number > assignments.size()) {
                        System.out.println("Invalid assignment number!");
                        break;
                    }

                    String oldDetails = assignments.get(number - 1);
                    String updatedDetails;

                    if (oldDetails.endsWith("Status: Pending")) {
                        updatedDetails = oldDetails.replace(
                                "Status: Pending", "Status: Completed");
                    } else if (oldDetails.endsWith("Status: Completed")) {
                        updatedDetails = oldDetails.replace(
                                "Status: Completed", "Status: Pending");
                    } else {
                        System.out.println("Unable to update this assignment.");
                        break;
                    }

                    assignments.set(number - 1, updatedDetails);
                    saveAssignments();

                    System.out.println("Assignment status updated successfully!");
                    break;
                }

                case 4:
                    showDashboard();
                    break;

                case 5:
                    searchAssignment(sc);
                    break;

                case 6:
                    deleteAssignment(sc);
                    break;

                case 7:
                    saveAssignments();
                    System.out.println("Thank you, " + studentName + "!");
                    System.out.println("Exiting Assignment Tracker...");
                    break;

                default:
                    System.out.println("Invalid choice! Please select 1 to 7.");
            }

        } while (choice != 7);

        sc.close();
    }
}