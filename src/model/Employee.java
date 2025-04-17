package model;

public class Employee<T> implements Comparable<Employee<T>> {
    private final T employeeId;
    private String name;
    private String department;
    private static double salary;
    private static double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;

    public Employee(T employeeId, String name, String department, double salary, double performanceRating, int yearsOfExperience, boolean isActive) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.performanceRating = performanceRating;
        this.yearsOfExperience = yearsOfExperience;
        this.isActive = isActive;
    }

    // Getters
    public T getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return this.salary; }
    public double getPerformanceRating() { return this.performanceRating; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setPerformanceRating(double rating) { this.performanceRating = rating; }
    public void setYearsOfExperience(int years) { this.yearsOfExperience = years; }
    public void setActive(boolean active) { this.isActive = active; }

    @Override
    public int compareTo(Employee<T> other) {
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }

    @Override
    public String toString() {
        return name + " | " + department + " | " + yearsOfExperience + " yrs";
    }

    public static double getPerformanceRating(Object o) {
        if (o instanceof Employee<?> employee) {
            return employee.performanceRating;
        }
        throw new IllegalArgumentException("Invalid object type");
    }

    public static double getSalary(Object o) {
        if (o instanceof Employee<?> employee) {
            return employee.salary;
        }
        throw new IllegalArgumentException("Invalid object type");
    }

    public double getAdjustedSalary() {
        return this.salary * (1 + (this.performanceRating / 100));
    }
}
