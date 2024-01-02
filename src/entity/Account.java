package entity;

import Presentation.Admin.EmployeePresentation;
import businessImp.EmployeeBusiness;
import businessImp.ExceptionMethod;
import util.ConnectionDB;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Scanner;

public class Account implements Serializable {
    private int accId;
    private String userName;
    private String passWord;
    private boolean permission;
    private String empId;
    private boolean accStatus;

    public Account() {
    }

    public Account(int accId, String userName, String passWord, boolean permission, String empId, boolean accStatus) {
        this.accId = accId;
        this.userName = userName;
        this.passWord = passWord;
        this.permission = permission;
        this.empId = empId;
        this.accStatus = accStatus;
    }

    public int getAccId() {
        return accId;
    }

    public void setAccId(int accId) {
        this.accId = accId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public boolean isAccStatus() {
        return accStatus;
    }

    public void setAccStatus(boolean accStatus) {
        this.accStatus = accStatus;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accId=" + accId +
                ", userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", permission=" + permission +
                ", empId='" + empId + '\'' +
                ", accStatus=" + accStatus +
                '}';
    }

    public void inputData(Scanner scanner) {
        this.userName = inputUserName(scanner);
        this.passWord = inputPassword(scanner);
        this.permission = inputPermission(scanner);
        this.empId = inputEmpId(scanner);
    }

    public String inputUserName(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập tên tài khoản:");
        boolean isDuplicate;
        String userName = null;
        try {
            do {
                isDuplicate = false;
                userName = ExceptionMethod.StringNotEmpty(scanner);
                callSt = conn.prepareCall("{call check_account_by_username(?,?)}");
                callSt.setString(1, userName);
                callSt.registerOutParameter(2, Types.INTEGER);
                callSt.execute();
                int cnt_account = callSt.getInt(2);
                if (cnt_account > 0) {
                    System.err.println("Tên sản phẩm bị trùng! Mời nhập lại!");
                    isDuplicate = true;
                }
            } while (isDuplicate);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return userName;
    }

    public String inputPassword(Scanner scanner) {
        System.out.println("Nhập mật khẩu:");
        return ExceptionMethod.StringNotEmpty(scanner);
    }

    public boolean inputPermission(Scanner scanner) {
        System.out.println("Nhập quyền tài khoản (0-admin 1-user):");
        do {
            int permission = ExceptionMethod.validateInteger(scanner);
            if (permission == 0) {
                return false;
            } else if (permission == 1) {
                return true;
            } else {
                System.err.println("Mời nhập lại quyền tài khoản có giá trị 0 hoặc 1!");
                ;
            }
        } while (true);
    }

    public String inputEmpId(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        System.out.println("Danh sách nhân viên:");
        EmployeeBusiness employeeBusiness = new EmployeeBusiness();
        employeeBusiness.displayEmployee(EmployeePresentation.employeeList);
        System.out.println("Nhập mã nhân viên:");
        do {
            String empId = ExceptionMethod.StringNotEmpty(scanner);
            if (empId.length() <= 5) {
                try {
                    callSt = conn.prepareCall("{call check_employee_by_id(?,?)}");
                    callSt.setString(1, empId);
                    callSt.registerOutParameter(2, Types.INTEGER);
                    callSt.execute();
                    int cnt_employee = callSt.getInt(2);
                    if (cnt_employee > 0) {
                        callSt2 = conn.prepareCall("{call check_emp_id_account(?,?)}");
                        callSt2.setString(1, empId);
                        callSt2.registerOutParameter(2, Types.INTEGER);
                        callSt2.execute();
                        int cnt_acc = callSt2.getInt(2);
                        if (cnt_acc > 0) {
                            System.err.println("Mã nhân viên đã có tài khoản! Mời nhập lại!");
                        } else {
                            ConnectionDB.closeConnection(conn);
                            return empId;
                        }
                    } else {
                        System.err.println("Mã nhân viên không tồn tại! Mời nhập lại!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Mã nhân viên chỉ từ 5 ký tự!");
            }
        } while (true);
    }
}
