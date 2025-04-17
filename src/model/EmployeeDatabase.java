package model;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> {
    private Map<T, Employee<T>> employeeMap = new HashMap<>();

    public void addEmployee(Employee<T> employee) {
        employeeMap.put(employee.getEmployeeId(), employee);
    }

    public void removeEmployee(T employeeId) {
        employeeMap.remove(employeeId);
    }

    public List<Employee<UUID>> getAllEmployees() {
        return employeeMap
                .values()
                .stream()
                .map(emp -> (Employee<UUID>) emp)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> getEmployeesByDepartment(String department) {
        return employeeMap
                .values()
                .stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> getTopPerformers(double minRating) {
        return employeeMap
                .values()
                .stream()
                .filter(e -> e.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    public void giveRaiseToHighPerformers(double percentage) {
        for (Employee<T> emp : employeeMap.values()) {
            if (emp.getPerformanceRating() >= 4.5) {
                double raise = emp.getSalary() * (percentage / 100);
                emp.setSalary(emp.getSalary() + raise);
            }
        }
    }

    public List<Employee<T>> getTop5HighestPaidEmployees() {
        return employeeMap
                .values()
                .stream()
                .sorted(Comparator.comparingDouble((Employee<T> e) -> e.getSalary()).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public double getAverageSalaryByDepartment(String department) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getAdjustedSalary )
                .average()
                .orElse(0.0);
    }

    public void displayEmployeesFormatted() {
        System.out.printf("%-25s %-15s %-10s %-10s %-10s%n", "Name", "Department", "Salary", "Rating", "Experience");
        System.out.println("------------------------------------------------------------------------------");
        for (Employee<T> emp : employeeMap.values()) {
            System.out.printf("%-25s %-15s $%-9.2f %-10.1f %-10d%n",
                    emp.getName(), emp.getDepartment(), emp.getSalary(),
                    emp.getPerformanceRating(), emp.getYearsOfExperience());
        }
    }

    public void displayEmployeesStreamFormatted() {
        ArrayList<Employee<T>> allEmployees = new ArrayList<>(employeeMap.values());
        System.out.println("\n\n\n================== EMPLOYEE LIST ==================");
        System.out.printf("%-25s %-15s %-10s %-10s %-10s%n", "Name", "Department", "Salary", "Rating", "Experience");
        System.out.println("------------------------------------------------------------------------------");

        allEmployees.stream()
                .sorted(Comparator.comparing(Employee::getName))
                .forEach(emp -> System.out.printf("%-25s %-15s $%-9.2f %-10.1f %-10d%n",
                        emp.getName(), emp.getDepartment(), emp.getSalary(),
                        emp.getPerformanceRating(), emp.getYearsOfExperience()));

        // Summary
        System.out.println("\n================== SUMMARY ==================");
        System.out.printf("%-30s %d%n", "Total Employees:", allEmployees.size());
        System.out.printf("%-30s %s%n", "Departments:",
                allEmployees.stream()
                        .map(Employee::getDepartment)
                        .distinct()
                        .sorted()
                        .collect(Collectors.joining(", ")));

        double averageSalary = allEmployees.stream()
                .mapToDouble(Employee::getAdjustedSalary
                )
                .average()
                .orElse(0.0);
        System.out.printf("%-30s $%.2f%n", "Average Salary:", averageSalary);

        // Top 5 Performers
        System.out.println("\n=============== TOP 5 PERFORMERS ===============");
        System.out.printf("%-25s %-15s %-10s%n", "Name", "Department", "Rating");
        System.out.println("-------------------------------------------------------------");

        allEmployees.stream()
                .sorted(Comparator.comparingDouble(Employee::getPerformanceRating).reversed())
                .limit(5)
                .forEach(e -> System.out.printf("%-25s %-15s %-10.1f%n",
                        e.getName(), e.getDepartment(), e.getPerformanceRating()));

        // Top 5 Earners
        System.out.println("\n=============== TOP 5 EARNERS ===============");
        System.out.printf("%-25s %-15s %-10s%n", "Name", "Department", "Salary");
        System.out.println("-------------------------------------------------------------");

        allEmployees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(5)
                .forEach(e -> System.out.printf("%-25s %-15s $%-9.2f%n",
                        e.getName(), e.getDepartment(), e.getSalary()));

        System.out.println("=============================================================\n");

    }

}
