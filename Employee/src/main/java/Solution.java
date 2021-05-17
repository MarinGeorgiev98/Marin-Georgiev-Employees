import model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;
import java.util.List;

public class Solution {
    public static void main(String[] args) throws IOException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("employee");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Scanner scan = new Scanner(System.in);
        List<Employee> employees = new ArrayList<>();
        HashMap<Long, List<Employee>> longestWorkedEmployees = new HashMap<>();

        //Please provide your file path here
        String path = "C:\\Users\\Марин\\Desktop\\Employee\\src\\Data.csv";
        Path filePath = Paths.get(path);
        Charset charset = StandardCharsets.UTF_8;
        String[] fileLines = Files.readAllLines(filePath, charset).toArray(String[]::new);

        for (String fileLine : fileLines) {
            String[] formats = {
                    "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd",
                    "yyyy-dd-MM", "yyyy/dd/MM", "yyyy.dd.MM",
                    "MM-dd-yyyy", "MM/dd/yyyy", "MM.dd.yyyy",
                    "MMM-dd-yyyy", "MMM/dd/yyyy", "MMM.dd.yyyy",
                    "MMMM-dd-yyyy", "MMMM/dd/yyyy", "MMMM.dd.yyyy"};
            String[] dataLine = fileLine.split(", ");

            int employeeId = Integer.parseInt(dataLine[0]);
            int projectId = Integer.parseInt(dataLine[1]);
            LocalDate dateFrom = LocalDate.parse(dataLine[2]);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formats[0]);
            LocalDate dateTo;
            if (!dataLine[3].equals("NULL")) {
                dateTo = LocalDate.parse(dataLine[3], formatter);
            } else {
                dateTo = LocalDate.now();
            }
            long daysWorked = ChronoUnit.DAYS.between(dateFrom, dateTo);
            Employee currentEmployee = new Employee(employeeId, projectId, dateFrom, dateTo, daysWorked);
            entityManager.getTransaction().begin();
            entityManager.persist(currentEmployee);
            entityManager.getTransaction().commit();
            employees.add(currentEmployee);
        }
        long daysSum = 0;

        for (int i = 0; i < employees.size() - 1; i++) {
            for (int j = i + 1; j <= employees.size() - 1; j++) {
                int firstEmployeeId = employees.get(i).getEmpID();
                int secondEmployeeId = employees.get(j).getEmpID();
                long maxDaysOnSameProject = 0;

                if (firstEmployeeId != secondEmployeeId && i < j) {
                    int firstEmployeeProject = employees.get(i).getProjectID();
                    int secondEmployeeProject = employees.get(j).getProjectID();

                    int dateFrom = employees.get(i).getDateFrom().compareTo(employees.get(j).getDateFrom());

                    if (firstEmployeeProject == secondEmployeeProject) {
                        if (dateFrom > 0) {
                            if (employees.get(j).getDateTo().compareTo(employees.get(i).getDateFrom()) > 0) {
                                if (employees.get(i).getDateTo().compareTo(employees.get(j).getDateTo()) > 0) {
                                    long diff = ChronoUnit.DAYS.between(employees.get(i).getDateFrom(), (employees.get(j).getDateTo()));
                                    maxDaysOnSameProject += diff + 1;
                                    addEmployeesToHashmap(employees, longestWorkedEmployees, i, j, maxDaysOnSameProject);
                                } else {
                                    long diff = ChronoUnit.DAYS.between(employees.get(i).getDateFrom(), (employees.get(i).getDateTo()));
                                    maxDaysOnSameProject += diff + 1;
                                    addEmployeesToHashmap(employees, longestWorkedEmployees, i, j, maxDaysOnSameProject);
                                }
                            }
                        } else if (dateFrom < 0) {
                            if (employees.get(i).getDateTo().compareTo(employees.get(j).getDateFrom()) > 0) {
                                if (employees.get(j).getDateTo().compareTo(employees.get(i).getDateTo()) > 0) {
                                    long diff = ChronoUnit.DAYS.between(employees.get(j).getDateFrom(), (employees.get(i).getDateTo()));
                                    maxDaysOnSameProject += diff + 1;
                                    addEmployeesToHashmap(employees, longestWorkedEmployees, i, j, maxDaysOnSameProject);
                                } else {
                                    long diff = ChronoUnit.DAYS.between(employees.get(j).getDateFrom(), (employees.get(j).getDateTo()));
                                    maxDaysOnSameProject += diff + 1;
                                    addEmployeesToHashmap(employees, longestWorkedEmployees, i, j, maxDaysOnSameProject);
                                }
                            }
                        }
                    } else if (dateFrom == 0) {
                        long diff = ChronoUnit.DAYS.between(employees.get(i).getDateFrom(), (employees.get(i).getDateTo()));
                        maxDaysOnSameProject += diff + 1;
                        addEmployeesToHashmap(employees, longestWorkedEmployees, i, j, maxDaysOnSameProject);
                    }
                    if (maxDaysOnSameProject > daysSum) {
                        daysSum = maxDaysOnSameProject;
                    }
                }
            }
        }
        long longestPeriod = 0;
        for (Long days : longestWorkedEmployees.keySet()) {
            if (longestPeriod < days) {
                longestPeriod = days;
            }
        }
        Employee firstEmployee = longestWorkedEmployees.get(longestPeriod).get(0);
        Employee secondEmployee = longestWorkedEmployees.get(longestPeriod).get(1);
        System.out.printf("The longest period in which two employees have worked together on a project is %d days.%n", daysSum);
        System.out.printf("The first employee has id = %d. The second employee has id = %d",
                firstEmployee.getEmpID(), secondEmployee.getEmpID());
    }

    private static void addEmployeesToHashmap(List<Employee> employees, HashMap<Long, List<Employee>> longestWorkedEmployees, int i, int j, long daysWorked) {
        longestWorkedEmployees.putIfAbsent(daysWorked, new ArrayList<>());
        longestWorkedEmployees.get(daysWorked).add(employees.get(i));
        longestWorkedEmployees.get(daysWorked).add(employees.get(j));
    }
}
