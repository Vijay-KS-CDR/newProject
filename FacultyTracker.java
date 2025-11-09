import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
class Faculty {
    String name;
    String dept;
    String cabin;
    boolean present;
    Map<Integer, String> periodRoom;

    Faculty(String name, String dept, String cabin, boolean present, Map<Integer, String> periodRoom) {
        this.name = name;
        this.dept = dept;
        this.cabin = cabin;
        this.present = present;
        this.periodRoom = periodRoom;
    }
}

class UserFrame extends Frame implements ActionListener {
    Map<String, Map<String, Faculty>> weeklySchedule;
    Choice dayChoice;
    TextField nameField, periodField;
    TextArea resultArea;
    Button checkButton;

    UserFrame(Map<String, Map<String, Faculty>> weeklySchedule) {
        this.weeklySchedule = weeklySchedule;

        setTitle("Faculty Tracker - User Access");
        setSize(500, 400);
        setLayout(new FlowLayout());
        setBackground(Color.LIGHT_GRAY);

        Label l1 = new Label("Select Day:");
        dayChoice = new Choice();
        for (String day : java.util.List.of("monday", "tuesday", "wednesday", "thursday", "friday")) {
            dayChoice.add(day);
        }

        Label l2 = new Label("Faculty Name:");
        nameField = new TextField(20);

        Label l3 = new Label("Period (1–7):");
        periodField = new TextField(5);

        checkButton = new Button("Check Details");
        checkButton.addActionListener(this);

        resultArea = new TextArea(10, 45);
        resultArea.setEditable(false);

        add(l1);
        add(dayChoice);
        add(l2);
        add(nameField);
        add(l3);
        add(periodField);
        add(checkButton);
        add(resultArea);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String day = dayChoice.getSelectedItem().toLowerCase();
        String name = nameField.getText().trim();
        int period;

        try {
            period = Integer.parseInt(periodField.getText().trim());
            if (period < 1 || period > 7) {
               throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            resultArea.setText("❌ Invalid period! Enter a number (1–7).");
            return;
        }

        Map<String, Faculty> facultyMap = weeklySchedule.get(day);
        if (facultyMap == null) {
            resultArea.setText("❌ Invalid day selected.");
            return;
        }

        Faculty faculty = null;
        for (String key : facultyMap.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                faculty = facultyMap.get(key);
                break;
            }
        }

        if (faculty == null) {
            resultArea.setText("❌ Faculty not found!");
            return;
        }

        LocalDate today = LocalDate.now();
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(today).append("\n\n");
        sb.append("Name: ").append(faculty.name).append("\n");
        sb.append("Dept: ").append(faculty.dept).append("\n");
        sb.append("Cabin: ").append(faculty.cabin).append("\n");

        if (day.equalsIgnoreCase(LocalDate.now().getDayOfWeek().toString())){
            sb.append("Status: ").append(faculty.present ? "✅ Present" : "❌ Absent").append("\n");
        }

        sb.append("Current Room: ").append(faculty.periodRoom.getOrDefault(period, "N/A")).append("\n");

        resultArea.setText(sb.toString());
    }
}
public class FacultyTracker {
    public static void main(String[] args) {
        java.util.List<String> days = java.util.List.of("monday", "tuesday", "wednesday", "thursday", "friday");
        Map<String, Map<String, Faculty>> weeklySchedule = new HashMap<>();
        Scanner sc = new Scanner(System.in);

        for (String day : days) {
            weeklySchedule.put(day, loadFacultyData(day + ".txt"));
        }

        while (true) {
            System.out.println("\n=== Faculty Tracker ===");
            System.out.println("1. Admin Login");
            System.out.println("2. User Access (GUI)");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int type = sc.nextInt();
            sc.nextLine();

            switch (type) {
                case 1 -> {
                    System.out.print("Enter Admin Password: ");
                    String password = sc.nextLine();
                    if (!password.equals("I am Admin1")) {
                        System.out.println("❌ Wrong password!");
                        break;
                    }

                    System.out.println("\n--- Admin Options ---");
                    System.out.println("1. Update Attendance");
                    System.out.println("2. Update Faculty Schedule");
                    System.out.println("3. Add New Faculty");
                    System.out.print("Enter option: ");
                    int option = sc.nextInt();
                    sc.nextLine();

                    switch (option) {
                        case 1 -> {
                            String day = LocalDate.now().getDayOfWeek().toString().toLowerCase();
                            Map<String, Faculty> facultyMap = weeklySchedule.get(day);
                            if (facultyMap == null) {
                                System.out.println("❌ Invalid day!");
                                break;
                            }

                            for (Faculty f : facultyMap.values()) f.present = true;
                            System.out.println("Enter absent faculty names (comma-separated): ");
                            String absents = sc.nextLine().trim();
                            if (!absents.isEmpty()) {
                                String[] names = absents.split(",");
                                for (String n : names) {
                                    Faculty f = facultyMap.get(n.trim());
                                    if (f != null) f.present = false;
                                }
                            }

                            saveFacultyData(day + ".txt", facultyMap);
                            System.out.println("✅ Attendance updated for " + day);
                        }

                        case 2 -> {
                            System.out.print("Enter Day: ");
                            String day = sc.nextLine().trim().toLowerCase();
                            Map<String, Faculty> facultyMap = weeklySchedule.get(day);
                            if (facultyMap == null) {
                                System.out.println("❌ Invalid day!");
                                break;
                            }

                            System.out.print("Enter faculty name to update: ");
                            String name = sc.nextLine().trim();
                            Faculty faculty = facultyMap.get(name);

                            if (faculty == null) {
                                System.out.println("❌ Faculty not found!");
                                break;
                            }

                            System.out.println("Enter 7 new room numbers:");
                            for (int i = 1; i <= 7; i++) {
                                System.out.print("Period " + i + ": ");
                                faculty.periodRoom.put(i, sc.nextLine().trim());
                            }

                            saveFacultyData(day + ".txt", facultyMap);
                            System.out.println("✅ Updated successfully for " + faculty.name);
                        }

                        case 3 -> {
                            System.out.print("Enter Name: ");
                            String name = sc.nextLine().trim();
                            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                            System.out.print("Enter Department: ");
                            String dept = sc.nextLine();
                            System.out.print("Enter Cabin: ");
                            String cabin = sc.nextLine();

                            Map<Integer, String> schedule = new HashMap<>();
                            for (int i = 1; i <= 7; i++) {
                                System.out.print("Room for Period " + i + ": ");
                                schedule.put(i, sc.nextLine());
                            }

                            for (String day : days) {
                                Map<String, Faculty> map = weeklySchedule.get(day);
                                map.put(name, new Faculty(name, dept, cabin, true, new HashMap<>(schedule)));
                                saveFacultyData(day + ".txt", map);
                            }
                            System.out.println("✅ New faculty added for all days");
                        }

                        default -> System.out.println("❌ Wrong input!");
                    }
                }

                case 2 -> {
                    System.out.println("Opening User Access (GUI)...");
                    new UserFrame(weeklySchedule);
                }

                case 3 -> {
                    for (String day : days) {
                        saveFacultyData(day + ".txt", weeklySchedule.get(day));
                    }
                    System.out.println("✅ All data saved. Exiting...");
                    return;
                }

                default -> System.out.println("❌ Wrong input!");
            }
        }
    }

    // LOAD DATA
    static Map<String, Faculty> loadFacultyData(String fileName) {
        Map<String, Faculty> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) continue;

                String name = parts[0];
                String dept = parts[1];
                String cabin = parts[2];
                boolean present = Boolean.parseBoolean(parts[3]);
                String[] rooms = parts[4].split("-");
                Map<Integer, String> periodRoom = new HashMap<>();
                for (int i = 0; i < rooms.length; i++) {
                    periodRoom.put(i + 1, rooms[i]);
                }
                map.put(name, new Faculty(name, dept, cabin, present, periodRoom));
            }
        } catch (IOException e) {
            System.out.println("File Missing ❌ " + fileName);
        }
        return map;
    }

    // SAVE DATA
    static void saveFacultyData(String fileName, Map<String, Faculty> map) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Faculty f : map.values()) {
                bw.write(f.name + "," + f.dept + "," + f.cabin + "," + f.present + ",");
                for (int i = 1; i <= 7; i++) {
                    bw.write(f.periodRoom.get(i));
                    if (i < 7) bw.write("-");
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving " + fileName);
        }
    }
}
