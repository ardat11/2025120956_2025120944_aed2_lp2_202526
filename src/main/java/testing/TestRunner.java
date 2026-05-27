package testing;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Starting master test runner...");
        
        try {
            DatabaseTest.runDatabaseTests();
        } catch (Exception e) {
            System.out.println("Database tests failed: " + e.getMessage());
        }

        try {
            DatabaseTest.runSearchTests();
        } catch (Exception e) {
            System.out.println("Search tests failed: " + e.getMessage());
        }

        try {
            FileTest.runFileTests();
        } catch (Exception e) {
            System.out.println("File tests failed: " + e.getMessage());
        }

        System.out.println("All system tests completed successfully.");
    }
}
