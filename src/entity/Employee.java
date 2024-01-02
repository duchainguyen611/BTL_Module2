package entity;

import businessImp.ExceptionMethod;
import util.ConnectionDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Scanner;

public class Employee {
    private String empId;
    private String empName;
    private Date birthOfDate;
    private String email;
    private String phone;
    private String address;
    private short empStatus;

    public Employee() {
    }

    public Employee(String empId, String empName, Date birthOfDate, String email, String phone, String address, short empStatus) {
        this.empId = empId;
        this.empName = empName;
        this.birthOfDate = birthOfDate;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.empStatus = empStatus;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Date getBirthOfDate() {
        return birthOfDate;
    }

    public void setBirthOfDate(Date birthOfDate) {
        this.birthOfDate = birthOfDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(short empStatus) {
        this.empStatus = empStatus;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId='" + empId + '\'' +
                ", empName='" + empName + '\'' +
                ", birthOfDate=" + birthOfDate +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", empStatus=" + empStatus +
                '}';
    }

    public void inputData(Scanner scanner){
        this.empId = inputEmployeeId(scanner);
        this.empName = inputEmployeeName(scanner);
        this.birthOfDate = inputEmployeeBirthOfDate(scanner);
        this.email = inputEmail(scanner);
        this.phone = inputPhone(scanner);
        this.address = inputAddress(scanner);
        this.empStatus = inputEmpStatus(scanner);
    }

    public String inputEmployeeId(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập mã nhân viên:");
        boolean isDuplicate;
        String id_employee = null;
        try {
            do {
                isDuplicate = false;
                id_employee = ExceptionMethod.StringNotEmpty(scanner);
                if (id_employee.length() <= 5){
                    callSt = conn.prepareCall("{call check_employee_by_id(?,?)}");
                    callSt.setString(1, id_employee);
                    callSt.registerOutParameter(2, Types.INTEGER);
                    callSt.execute();
                    int cnt_id_employee = callSt.getInt(2);
                    if (cnt_id_employee > 0) {
                        System.err.println("Mã nhân viên bị trùng! Mời nhập lại!");
                        isDuplicate = true;
                    }
                }else {
                    isDuplicate = true;
                    System.err.println("Mã nhân viên chỉ từ 5 ký tự! Mời nhập lại!");
                }
            } while (isDuplicate);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return id_employee;
    }

    public String inputEmployeeName(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập tên nhân viên:");
        boolean isDuplicate;
        String name_Employee = null;
        try {
            do {
                isDuplicate = false;
                name_Employee = ExceptionMethod.StringNotEmpty(scanner);
                callSt = conn.prepareCall("{call check_employee_by_name(?,?)}");
                callSt.setString(1, name_Employee);
                callSt.registerOutParameter(2, Types.INTEGER);
                callSt.execute();
                int cnt_employee = callSt.getInt(2);
                if (cnt_employee > 0) {
                    System.err.println("Tên nhân viên bị trùng! Mời nhập lại!");
                    isDuplicate = true;
                }
            } while (isDuplicate);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return name_Employee;
    }

    public Date inputEmployeeBirthOfDate(Scanner scanner){
        System.out.println("Nhập ngày sinh nhân viên:");
        return ExceptionMethod.validateDate(scanner);
    }

    public String inputEmail(Scanner scanner){
        System.out.println("Nhập email nhân viên:");
        do {
            String email = ExceptionMethod.StringNotEmpty(scanner);
            String emailPattern = "^[A-Za-z0-9]+[A-Za-z0-9]*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)$";
            if (email.matches(emailPattern)) {
                return email;
            } else {
                System.out.println("Email phải có định dạng email! Mời nhập lại");
            }
        } while (true);
    }

    public String inputPhone(Scanner scanner){
        System.out.println("Nhập số điện thoại nhân viên:");
        do {
            String phone = ExceptionMethod.StringNotEmpty(scanner);
            String phonePattern = "(\\d{3}-)?\\d{2}-\\d{7}";
            if (phone.matches(phonePattern)) {
                return phone;
            } else {
                System.out.println("Số điện thoại phải có định dạng theo số điện thoại di động tại Việt Nam! Mời nhập lại");
            }
        } while (true);
    }

    public String inputAddress(Scanner scanner){
        System.out.println("Nhập địa chỉ nhân viên:");
        return ExceptionMethod.StringNotEmpty(scanner);
    }

    public short inputEmpStatus(Scanner scanner){
        System.out.println("Nhập trạng thái nhân viên (0- Hoạt động | 1- Nghỉ chế độ | 2-Nghỉ việc):");
        do {
            short status = ExceptionMethod.validateShort(scanner);
            switch (status){
                case 0: case 1: case 2:
                    return status;
                default:
                    System.err.println("Chỉ nhận giá trị 0,1 và 2!");
                    break;
            }
        }while (true);
    }
}
