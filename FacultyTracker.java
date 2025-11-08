import java.io.*;
import java.time.LocalDate;
import java.util.*;

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

public class FacultyTracker {
    public static void main(String[] args) {
    List<String> days = List.of("monday", "tuesday", "wednesday", "thursday", "friday");
    Map<String, Map<String, Faculty>> weeklySchedule = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        for (String day : days) {
            weeklySchedule.put(day, loadFacultyData(day.toLowerCase()+ ".txt"));
        }
        while (true) {
            System.out.println("\n=== Faculty Tracker ===");
            System.out.println("1. Admin Login");
            System.out.println("2. User Access");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int type = sc.nextInt();
            sc.nextLine();

            switch (type) {

                // Admin;
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

                        // UPDATE ATTENDANCE
                        case 1 -> {
                            System.out.print("Operating on today:");
                            String day = LocalDate.now().getDayOfWeek().toString().toLowerCase();
                            Map<String, Faculty> facultyMap = weeklySchedule.get(day);
                            if (facultyMap == null) {
                                System.out.println("❌ Invalid day!");
                                break;
                            }

                            for (Faculty f : facultyMap.values()){
                                f.present = true;
                            }

                            System.out.println("Enter absent faculty names (comma-separated): ");
                            String absents = sc.nextLine().trim();

                            if (!absents.isEmpty()) {
                                String[] names = absents.split(",");
                                for (Map.Entry<String, Faculty> entry : facultyMap.entrySet()) {
                                    for (String name : names) {
                                        if (entry.getKey().equalsIgnoreCase(name.trim())) {
                                            entry.getValue().present = false;
                                        }
                                    }
                                }
                            }

                            saveFacultyData(day.toLowerCase() + ".txt", facultyMap);
                            System.out.println("✅ Attendance updated for " + LocalDate.now().getDayOfWeek());
                        }

                        // update
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
                                String prom=sc.nextLine().trim();
                                faculty.periodRoom.put(i, prom);
                            }

                            saveFacultyData(day.toLowerCase() + ".txt", facultyMap);
                            System.out.println("✅ Updated successfully for " + faculty.name);
                        }

                        //new faculty
                        case 3 -> {

                            System.out.print("Enter Name: ");

                            String name = sc.nextLine().trim();
                            name=name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
                            System.out.print("Enter Department: ");
                            String dept = sc.nextLine();
                            System.out.print("Enter Cabin: ");
                            String cabin = sc.nextLine();

                            Map<Integer, String> schedule = new HashMap<>();
                            for (int i = 1; i <= 7; i++) {
                                System.out.print("Room for Period " + i + ": ");
                                schedule.put(i, sc.nextLine());
                            }

                            for(String day:days) {
                                Map<String,Faculty> map1=weeklySchedule.get(day);
                                map1.put(name,new Faculty(name,dept,cabin,true,new HashMap<>(schedule)));
                                saveFacultyData(day.toLowerCase() + ".txt", map1);
                            }
                            System.out.println("✅ New faculty added for all days");

                        }

                        default -> System.out.println("❌ Wrong input!");
                    }
                }

                //user
                case 2 -> {
                    System.out.print("Enter Day (Monday–Friday): ");
                    String day = sc.nextLine().trim().toLowerCase();
                    Map<String, Faculty> facultyMap = weeklySchedule.get(day);

                    if (facultyMap == null) {
                        System.out.println("❌ Invalid day!");
                        break;
                    }

                    System.out.print("Enter Faculty Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Enter Period (1–7): ");
                    int period = sc.nextInt();
                    sc.nextLine();

                    Faculty faculty = null;
                    for (String key : facultyMap.keySet()) {
                        if (key.equalsIgnoreCase(name)) {
                            faculty = facultyMap.get(key);
                            break;
                        }
                    }

                    LocalDate today = LocalDate.now();
                    System.out.println("\nDate: " + today);

                    if (faculty != null) {
                        System.out.println("Name: " + faculty.name + " | Dept: " + faculty.dept);
                        System.out.println("Cabin: " + faculty.cabin);
                        if(day.equalsIgnoreCase(LocalDate.now().getDayOfWeek().toString())){
                            System.out.println("Status: " + (faculty.present ? "✅ Present" : "❌ Absent"));
                        }
                        System.out.println("Current Room: " + faculty.periodRoom.getOrDefault(period, "N/A"));
                    } else {
                        System.out.println("❌ Faculty not found!");
                    }
                }
                //exit;
                case 3 -> {
                    for (String day : days) {
                        saveFacultyData(day.toLowerCase() + ".txt", weeklySchedule.get(day));
                    }
                    System.out.println("All data saved ✅ Exiting...");
                    return;
                }

                default -> System.out.println("❌ Wrong input!");
            }
        }
    }

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
            System.out.println(" File Missing❌" + fileName);
        }

        return map;
    }

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
