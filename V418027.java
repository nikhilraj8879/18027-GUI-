import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import java.util.regex.Pattern;


class Students {
    private int id;
    private String name;
    private int age;
    private String course;

    public Students(int id, String name, int age, String course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age + ", Course: " + course;
    }
}


public class V418027 {
    private Vector<Student> students = new Vector<>();
    private Connection connection;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public V418027() {
        // Initialize database connection
        String url = "jdbc:mysql://localhost:3307/studentdb";
        String user = "root";
        String password = "";
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Initialize GUI
        frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Age", "Course"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);


        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        JButton viewButton = new JButton("View Students");
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewStudents();
            }
        });

        JButton searchButton = new JButton("Search Student by ID");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchStudentById();
            }
        });

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        JButton updateButton = new JButton("Update Student");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        // Add buttons to a panel
        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(searchButton);
        panel.add(deleteButton);
        panel.add(updateButton);

        // Add panel and scroll pane to the frame
        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.setVisible(true);
    }

    private void addStudent() {
        // Create form
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(10);
        JTextField ageField = new JTextField(3);
        JTextField courseField = new JTextField(10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("ID:"));
        myPanel.add(idField);
        myPanel.add(new JLabel("Name:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("Age:"));
        myPanel.add(ageField);
        myPanel.add(new JLabel("Course:"));
        myPanel.add(courseField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Please Enter Student Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String course = courseField.getText();
            addStudent(new Student(id, name, age, course));
        }
    }

    // Add a student
    public void addStudent(Student student) {
        String sql = "INSERT INTO students (id, name, age, course) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getCourse());
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all students
    public void viewStudents() {
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String course = rs.getString("course");
                tableModel.addRow(new Object[]{id, name, age, course});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search for a student by ID
    public void searchStudentById() {
        String id = JOptionPane.showInputDialog("Enter Student ID:");
        if (id != null) {
            Student student = searchStudentById(Integer.parseInt(id));
            if (student != null) {
                JOptionPane.showMessageDialog(frame, student);
            } else {
                JOptionPane.showMessageDialog(frame, "Student not found.");
            }
        }
    }

    public Student searchStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String course = rs.getString("course");
                return new Student(id, name, age, course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Delete a student by ID
    public void deleteStudent() {
        String id = JOptionPane.showInputDialog("Enter Student ID to delete:");
        if (id != null) {
            deleteStudent(Integer.parseInt(id));
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a student's details
    public void updateStudent() {
        // Create form
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(10);
        JTextField ageField = new JTextField(3);
        JTextField courseField = new JTextField(10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("ID:"));
        myPanel.add(idField);
        myPanel.add(new JLabel("New Name:"));
        myPanel.add(nameField);
        myPanel.add(new JLabel("New Age:"));
        myPanel.add(ageField);
        myPanel.add(new JLabel("New Course:"));
        myPanel.add(courseField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Please Enter New Student Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String course = courseField.getText();
            updateStudent(id, name, age, course);
        }
    }

    public void updateStudent(int id, String newName, int newAge, String newCourse) {
        String sql = "UPDATE students SET name = ?, age = ?, course = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, newCourse);
            pstmt.setInt(4, id);
            int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Student not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Main method to run the application
public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            new V418027();
        }
    });
}
}

