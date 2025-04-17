package main;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Employee;
import model.EmployeeDatabase;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    public MenuItem generateReportMenuItem;
    public MenuItem topPerformersMenuItem;
    public MenuItem topEarnersMenuItem;
    public MenuItem giveRaiseMenuItem;
    public MenuItem averageSalaryMenuItem;

    @FXML private TableView<Employee<UUID>> employeeTableView;
    @FXML private TableColumn<Employee<UUID>, String> idColumn;
    @FXML private TableColumn<Employee<UUID>, String> nameColumn;
    @FXML private TableColumn<Employee<UUID>, String> departmentColumn;
    @FXML private TableColumn<Employee<UUID>, Double> salaryColumn;
    @FXML private TableColumn<Employee<UUID>, Double> ratingColumn;
    @FXML private TableColumn<Employee<UUID>, Integer> experienceColumn;
    @FXML private TableColumn<Employee<UUID>, Boolean> statusColumn;

    @FXML private TextField addNameField;
    @FXML private TextField addDeptField;
    @FXML private TextField addSalaryField;
    @FXML private TextField addRatingField;
    @FXML private TextField addExpField;
    @FXML private CheckBox addIsActiveBox;

    @FXML private TextField minRatingField, minSalaryField, maxSalaryField;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private TextField searchField;

    private Employee<UUID> selectedEmployee = null;
    private final EmployeeDatabase<UUID> database = new EmployeeDatabase<>();
    private final ObservableList<Employee<UUID>> employeeObservableList = FXCollections.observableArrayList();

    public void initialize() {
        List<Employee<UUID>> employees = List.of(new Employee<>(UUID.randomUUID(), "Abdul Baaki", "IT", 85000.0, 4.7, 5, true));

        for (Employee<UUID> employee : employees) {
            database.addEmployee(employee);
            employeeObservableList.add(employee);
        }
        employeeObservableList.setAll(database.getAllEmployees());
        employeeTableView.setItems(employeeObservableList);
        employeeTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedEmployee = newVal;
                        populateFormForUpdate(newVal);
                    }
                });
        
        departmentComboBox.setItems(FXCollections.observableArrayList(
                database.getAllEmployees().stream()
                        .map(Employee::getDepartment)
                        .distinct()
                        .sorted()
                        .toList()
        ));

        // Bind columns
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEmployeeId() != null ? cellData.getValue().getEmployeeId().toString() : "N/A"
        ));

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        departmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));

        salaryColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSalary()));

        ratingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPerformanceRating()));

        experienceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getYearsOfExperience()));

        statusColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().isActive()));
    }

    @FXML
    private void onSearchClicked() {
        String name = searchField
                .getText()
                .trim()
                .toLowerCase();
        List<Employee<UUID>> filtered = database
                .getAllEmployees()
                .stream()
                .filter(emp -> emp.getName().toLowerCase().contains(name))
                .collect(Collectors.toList());
        employeeObservableList.setAll(filtered);
    }

    @FXML
    private void onDepartmentSelected() {
        String dept = departmentComboBox.getValue();
        if (dept == null || dept.isEmpty()) {
            employeeObservableList.setAll(database.getAllEmployees());
        } else {
            List<Employee<UUID>> filtered = database.getEmployeesByDepartment(dept);
            employeeObservableList.setAll(filtered);
        }
    }

    @FXML
    private void onFilterClicked() {
        try {
            double minRating = minRatingField.getText().isEmpty() ? 0.0 : Double.parseDouble(minRatingField.getText());
            double minSalary = minSalaryField.getText().isEmpty() ? 0.0 : Double.parseDouble(minSalaryField.getText());
            double maxSalary = maxSalaryField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxSalaryField.getText());

            List<Employee<UUID>> filtered = database
                    .getAllEmployees()
                    .stream()
                    .filter(emp ->
                            emp.getPerformanceRating() >= minRating &&
                                    emp.getSalary() >= minSalary &&
                                    emp.getSalary() <= maxSalary
                    )
                    .collect(Collectors.toList());

            employeeObservableList.setAll(filtered);
        } catch (NumberFormatException e) {
            showAlert("Filter Error", "Invalid number input in filter fields.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onClearClicked() {
        searchField.clear();
        departmentComboBox
                .getSelectionModel()
                .clearSelection();
        minSalaryField.clear();
        maxSalaryField.clear();
        minRatingField.clear();
        employeeObservableList.setAll(database.getAllEmployees());
    }

    @FXML
    private void onGenerateReportClicked() {
        database.displayEmployeesStreamFormatted();
        database.displayEmployeesFormatted();
    }

    @FXML
    private void onTopPerformersClicked() {
        List<Employee<UUID>> topPerformers = database.getTopPerformers(4.5);
        String message = topPerformers
                .stream()
                .map(e -> e.getName() + " - Rating: " + e.getPerformanceRating())
                .collect(Collectors.joining("\n"));
        showAlert("Top Performers", message, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onTopEarnersClicked() {
        List<Employee<UUID>> topEarners = database.getTop5HighestPaidEmployees();
        String message = topEarners
                .stream()
                .map(e -> e.getName() + " - $" + e.getSalary())
                .collect(Collectors.joining("\n"));
        showAlert("Top Earners", message, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onGiveRaiseClicked() {
        database.giveRaiseToHighPerformers(10);
        employeeObservableList.setAll(database.getAllEmployees());
    }

    @FXML
    private void onAverageSalaryClicked() {
        String dept = departmentComboBox.getValue();
        if (dept == null || dept.isEmpty()) {
            showAlert("Missing Department", "Please select a department.", Alert.AlertType.WARNING);
            return;
        }
        double avg = database.getAverageSalaryByDepartment(dept);
        showAlert("Average Salary", "Average salary in " + dept + ": $" + String.format("%.2f", avg), Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void onAddEmployeeClicked() {
        try {
            String name = addNameField.getText().trim();
            String dept = addDeptField.getText().trim();
            double salary = Double.parseDouble(addSalaryField.getText().trim());
            double rating = Double.parseDouble(addRatingField.getText().trim());
            int experience = Integer.parseInt(addExpField.getText().trim());
            boolean isActive = addIsActiveBox.isSelected();
            
            // Create and add employee
            Employee<UUID> employee = new Employee<>(UUID.randomUUID(), name, dept, salary, rating, experience, isActive);
            database.addEmployee(employee);
            employeeObservableList.add(employee);
            clearAddForm();

            showAlert("Success", "Employee added successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter all fields correctly.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onUpdateEmployeeClicked() {
        if (selectedEmployee == null) {
            showAlert("No Selection", "Please select an employee from the table to update.", Alert.AlertType.WARNING);
            return;
        }

        try {
            selectedEmployee.setName(addNameField.getText().trim());
            selectedEmployee.setDepartment(addDeptField.getText().trim());
            selectedEmployee.setSalary(Double.parseDouble(addSalaryField.getText().trim()));
            selectedEmployee.setPerformanceRating(Double.parseDouble(addRatingField.getText().trim()));
            selectedEmployee.setYearsOfExperience(Integer.parseInt(addExpField.getText().trim()));
            selectedEmployee.setActive(addIsActiveBox.isSelected());

            employeeTableView.refresh();
            clearAddForm();
            selectedEmployee = null;

            showAlert("Updated", "Employee information updated successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Update Failed", "Please enter valid data to update employee.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onRemoveEmployeeClicked() {
        if (selectedEmployee == null) {
            showAlert("No Selection", "Please select an employee from the table to remove.", Alert.AlertType.WARNING);
            return;
        }

        database.removeEmployee(selectedEmployee.getEmployeeId());
        employeeObservableList.remove(selectedEmployee);
        clearAddForm();
        selectedEmployee = null;

        showAlert("Removed", "Employee has been removed.", Alert.AlertType.INFORMATION);
    }

    private void clearAddForm() {
        addNameField.clear();
        addDeptField.clear();
        addSalaryField.clear();
        addRatingField.clear();
        addExpField.clear();
        addIsActiveBox.setSelected(true);
    }

    private void populateFormForUpdate(Employee<UUID> emp) {
        addNameField.setText(emp.getName());
        addDeptField.setText(emp.getDepartment());
        addSalaryField.setText(String.valueOf(emp.getSalary()));
        addRatingField.setText(String.valueOf(emp.getPerformanceRating()));
        addExpField.setText(String.valueOf(emp.getYearsOfExperience()));
        addIsActiveBox.setSelected(emp.isActive());
    }
}
