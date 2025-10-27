import java.time.LocalDate;
import java.util.*;
class Faculty {
    String name;
    String dept;
    String cabin;
    boolean present;
    Map<Integer, String> periodRoom;  // period -> room number

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
        Scanner sc = new Scanner(System.in);

        // --- Faculty Data ---
        Map<Integer, String> VishalSchedule = new HashMap<>();
        VishalSchedule.put(1, "201");
        VishalSchedule.put(2, "202");
        VishalSchedule.put(3, "203");
        VishalSchedule.put(4, "204");
        VishalSchedule.put(5, "205");
        VishalSchedule.put(6, "206");
        VishalSchedule.put(7, "207");

        Map<Integer, String> anithaSchedule = new HashMap<>();
        anithaSchedule.put(1, "108");
        anithaSchedule.put(2, "110");
        anithaSchedule.put(3, "112");
        anithaSchedule.put(4, "114");
        anithaSchedule.put(5, "116");
        anithaSchedule.put(6, "118");
        anithaSchedule.put(7, "120");

        Map<Integer, String> rajkumarSchedule = new HashMap<>();
        rajkumarSchedule.put(1, "101");
        rajkumarSchedule.put(2, "102");
        rajkumarSchedule.put(3, "103");
        rajkumarSchedule.put(4, "104");
        rajkumarSchedule.put(5, "105");
        rajkumarSchedule.put(6, "106");
        rajkumarSchedule.put(7, "107");

        Map<String, Faculty> facultyMap = new HashMap<>();
        facultyMap.put("Vishal", new Faculty("Vishal", "CSE", "226", true, VishalSchedule));
        facultyMap.put("Anitha", new Faculty("Anitha", "IT", "215", false, anithaSchedule));
        facultyMap.put("Rajkumar", new Faculty("Rajkumar", "ECE", "234", true, rajkumarSchedule));
        while (true) {
            System.out.println("Login type:\n1.Admin\n2.user\n3.Exit");
            int type = sc.nextInt();
            sc.nextLine();
            switch (type) {
                case 1:
                    System.out.println("Enter password:");
                    String s = sc.nextLine();
                    if (!s.equals("I am Admin1")) {
                        System.out.println("Wrong password❌.");
                        break;
                    }
                    System.out.println("1.Update Attendance.");
                    System.out.println("2.Update Details.");
                    System.out.println("3.new Faculty");
                    int option = sc.nextInt();
                    sc.nextLine();
                    switch (option) {
                        case 1:
                            for (Faculty atn : facultyMap.values()) {
                                atn.present = true;
                            }
                            System.out.println("Enter absent facultys's names with comma seperated names:");
                            String absentFaculty = sc.nextLine().trim();
                            if (absentFaculty.isEmpty()) {
                                break;
                            }
                            String[] saf /*(Seperated absent faculty)*/ = absentFaculty.split(",");
                            for (int i = 0; i < saf.length; i++) {
                                for (Map.Entry<String, Faculty> entries : facultyMap.entrySet()) {
                                    if (saf[i].equalsIgnoreCase(entries.getKey())) {
                                        Faculty f = entries.getValue();
                                        f.present = false;
                                    }
                                }
                            }
                            break;
                        case 2:

                            break;
                        case 3:
                            System.out.println("Welcome Mr Admin----");
                            String []DetName={"Name:","Department","Cabin"};
                            String []details =new String[3];
                            for(int i=0;i<details.length;i++) {
                                System.out.println(DetName[i]);
                                details[i] = sc.nextLine();
                            }
                            HashMap<Integer,String>a=new HashMap<>();
                            for(int i=0;i<7;i++){
                                System.out.print("period "+(i+1)+" Enter room number: ");
                                String num= sc.nextLine();
                                a.put(i+1,num);
                                System.out.println();
                            }
                            System.out.println();
                            facultyMap.put(details[0],new Faculty(details[0],details[1],details[2],true,a));
                            break;
                        default:
                            System.out.println("Wrong input❌.");
                    }
                    break;
                case 2:
                    // --- User Input --
                    System.out.print("Enter Faculty Name: ");
                    String inputName = sc.nextLine().trim();

                    System.out.print("Enter Period (1-7): ");
                    int period = sc.nextInt();

                    // --- Lookup ---
                    Faculty f = null;
                    for (String key : facultyMap.keySet()) {
                        if (key.equalsIgnoreCase(inputName)) {
                            f = facultyMap.get(key);
                            break;
                        }
                    }
                    LocalDate today = LocalDate.now(); // current date

                    System.out.println("\nDate: " + today);
                    if (f != null) {
                        System.out.println(f.name + " " + f.dept);
                        System.out.println("Cabin: " + f.cabin);
                        System.out.println("Status: " + (f.present ? "✅Present" : "❌Absent"));

                        String room = f.periodRoom.get(period);
                        if (room != null) {
                            System.out.println("Current in: " + room);
                        } else {
                            System.out.println("No Wrong period number❌.");
                        }
                    } else {
                        System.out.println("Faculty not found❌");
                    }
                    break;
                case 3:
                    return;

                default:
                    System.out.println("Wrong input.");
            }
        }
    }
}