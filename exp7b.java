import java.sql.*;
import java.util.*;

// ----------------------- Model -----------------------
class Student {
    private int studentID;
    private String name;
    private String department;
    private double marks;

    public Student(int studentID, String name, String department, double marks) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public int getStudentID() { return studentID; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getMarks() { return marks; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setMarks(double marks) { this.marks = marks; }
}

// ----------------------- Controller -----------------------
class StudentController {
    private final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    private final String USER = "root";       // <-- change if needed
    private final String PASS = "password";   // <-- change if needed

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void addStudent(Student s) {
        String sql = "INSERT INTO Students (StudentID, Name, Department, Marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getStudentID());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getDepartment());
            stmt.setDouble(4, s.getMarks());
            stmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("StudentID"),
                    rs.getString("Name"),
                    rs.getString("Department"),
                    rs.getDouble("Marks")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    public void updateStudent(Student s) {
        String sql = "UPDATE Students SET Name=?, Department=?, Marks=? WHERE StudentID=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getDepartment());
            stmt.setDouble(3, s.getMarks());
            stmt.setInt(4, s.getStudentID());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM Students WHERE StudentID=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }
}

// ----------------------- View (Main) -----------------------
public class StudentManagementApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentController controller = new StudentController();

        int choice;
        do {
            System.out.println("\n===== Student Management Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Department: ");
                    String dept = scanner.nextLine();
                    System.out.print("Enter Marks: ");
                    double marks = scanner.nextDouble();
                    Student s = new Student(id, name, dept, marks);
                    controller.addStudent(s);
                }
                case 2 -> {
                    List<Student> students = controller.getAllStudents();
                    System.out.println("\n-- Student List --");
                    for (Student s : students) {
                        System.out.printf("ID: %d | Name: %s | Dept: %s | Marks: %.2f\n",
                                s.getStudentID(), s.getName(), s.getDepartment(), s.getMarks());
                    }
                }
                case 3 -> {
                    System.out.print("Enter Student ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter New Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter New Department: ");
                    String dept = scanner.nextLine();
                    System.out.print("Enter New Marks: ");
                    double marks = scanner.nextDouble();
                    Student s = new Student(id, name, dept, marks);
                    controller.updateStudent(s);
                }
                case 4 -> {
                    System.out.print("Enter Student ID to delete: ");
                    int id = scanner.nextInt();
                    controller.deleteStudent(id);
                }
                case 5 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 5);

        scanner.close();
    }
}
