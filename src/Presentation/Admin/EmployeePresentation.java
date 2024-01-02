package Presentation.Admin;

import businessImp.EmployeeBusiness;
import businessImp.ExceptionMethod;
import entity.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePresentation {

    public static EmployeeBusiness employeeBusiness = new EmployeeBusiness();

    public static List<Employee> employeeList = new ArrayList<>();

    public static void menuEmployee(Scanner scanner) {
        do {
            System.out.println("******************EMPLOYEE MANAGEMENT****************\n" +
                    "1. Danh sách nhân viên\n" +
                    "2. Thêm mới nhân viên\n" +
                    "3. Cập nhật thông tin nhân viên\n" +
                    "4. Cập nhật trạng thái nhân viên\n" +
                    "5. Tìm kiếm nhân viên\n" +
                    "6. Thoát");
            System.out.println("Nhập lựa chọn:");
            int choice = ExceptionMethod.validateInteger(scanner);
            switch (choice) {
                case 1:
                    employeeBusiness.displayEmployee(employeeList);
                    break;
                case 2:
                    employeeBusiness.inputEmployee(scanner);
                    break;
                case 3:
                    employeeBusiness.updateInforEmployee(scanner);
                    break;
                case 4:
                    employeeBusiness.updateStatusEmployee(scanner,employeeList);
                    break;
                case 5:
                    employeeBusiness.searchEmployee(scanner,employeeList);
                    break;
                case 6:
                    AdminPresentation.menuAdmin(scanner);
                    break;
                default:
                    System.err.println("Mời nhập từ 1 đến 6!");
                    break;
            }
        } while (true);
    }
}
