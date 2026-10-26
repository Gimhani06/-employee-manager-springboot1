
package com.example.employeemanager.controller;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Display the employee form and employee table
    @GetMapping("/")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());

        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Employee());
        }

        return "employees";
    }

    // Create a new employee
    @PostMapping("/save")
    public String saveEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "employees";
        }

        if (employeeService.emailExists(employee.getEmail())) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("errorMessage",
                    "This email is already registered");
            return "employees";
        }

        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Employee saved successfully!");

        return "redirect:/";
    }

    // Display the edit form
    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id, Model model) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);

        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            return "edit-employee";
        }

        return "redirect:/";
    }

    // Update an existing employee
    @PostMapping("/update/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Employee> existing = employeeService.getEmployeeById(id);

        if (existing.isEmpty()) {
            return "redirect:/";
        }

        employee.setId(id);

        if (result.hasErrors()) {
            return "edit-employee";
        }

        if (!existing.get().getEmail().equals(employee.getEmail())
                && employeeService.emailExists(employee.getEmail())) {
            model.addAttribute("errorMessage",
                    "This email is already registered");
            return "edit-employee";
        }

        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Employee updated successfully!");

        return "redirect:/";
    }

    // Delete an employee
    @GetMapping("/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Employee deleted successfully!");

        return "redirect:/";
    }
}
