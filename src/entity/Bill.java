package entity;

import businessImp.EmployeeBusiness;
import businessImp.ExceptionMethod;
import util.ConnectionDB;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Bill {
    private int billId;
    private String billCode;
    private Boolean billType;
    private String empIdCreated;
    private Date created;
    private String empIdAuth;
    private Date authDate;
    private short billStatus;

    public Bill() {
    }

    public Bill(int billId, String billCode, Boolean billType, String empIdCreated, Date created, String empIdAuth, Date authDate, short billStatus) {
        this.billId = billId;
        this.billCode = billCode;
        this.billType = billType;
        this.empIdCreated = empIdCreated;
        this.created = created;
        this.empIdAuth = empIdAuth;
        this.authDate = authDate;
        this.billStatus = billStatus;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Boolean getBillType() {
        return billType;
    }

    public void setBillType(Boolean billType) {
        this.billType = billType;
    }

    public String getEmpIdCreated() {
        return empIdCreated;
    }

    public void setEmpIdCreated(String empIdCreated) {
        this.empIdCreated = empIdCreated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getEmpIdAuth() {
        return empIdAuth;
    }

    public void setEmpIdAuth(String empIdAuth) {
        this.empIdAuth = empIdAuth;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public short getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(short billStatus) {
        this.billStatus = billStatus;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", billCode='" + billCode + '\'' +
                ", billType=" + billType +
                ", empIdCreated='" + empIdCreated + '\'' +
                ", created=" + created +
                ", empIdAuth='" + empIdAuth + '\'' +
                ", authDate=" + authDate +
                ", billStatus=" + billStatus +
                '}';
    }

    public String inputBillCode(Scanner scanner){
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập mã code:");
        do {
            try {
                String code = ExceptionMethod.StringNotEmpty(scanner);
                callSt = conn.prepareCall("{call check_bill_code_by_bill_code(?,?)}");
                callSt.setString(1,code);
                callSt.registerOutParameter(2,Types.INTEGER);
                callSt.execute();
                int check_bill = callSt.getInt(2);
                if (code.length() <= 10){
                    if (check_bill > 0){
                        System.err.println("Mã code bị trùng! Mời nhập lại");
                    }else {
                        return code;
                    }
                }else {
                    System.err.println("Mã code chỉ từ 10 ký tự!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }while (true);
    }

    public Short inputBillStatus(Scanner scanner){
        System.out.println("Nhập trạng thái phiếu:");
        do {
            short status = ExceptionMethod.validateShort(scanner);
            if (status == 0 || status == 1){
                return status;
            }else {
                System.err.println("Trạng thái phiếu chỉ nhận giá trị 0 hoặc 1!");
            }
        }while (true);
    }

    public String inputEmpIdCreated(Scanner scanner, List<Employee> employeeList){
        System.out.println("Danh sách nhân viên:");
        EmployeeBusiness employeeBusiness = new EmployeeBusiness();
        employeeBusiness.displayEmployee(employeeList);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        System.out.println("Nhập mã nhân viên:");
        boolean isCheck;
        String id_employee = null;
        try {
            do {
                isCheck = false;
                id_employee = ExceptionMethod.StringNotEmpty(scanner);
                if (id_employee.length() <= 5){
                    callSt = conn.prepareCall("{call check_employee_by_id(?,?)}");
                    callSt.setString(1, id_employee);
                    callSt.registerOutParameter(2, Types.INTEGER);
                    callSt.execute();
                    int cnt_id_employee = callSt.getInt(2);
                    if (cnt_id_employee <= 0) {
                        System.err.println("Mã nhân viên không tồn tại! Mời nhập lại!");
                        isCheck = true;
                    }else{
                        callSt2 = conn.prepareCall("{call get_status_Employee(?)}");
                        callSt2.setString(1,id_employee);
                        ResultSet rs = callSt2.executeQuery();
                        short status = 0;
                        if (rs.next()){
                            status = rs.getShort(1);
                        }
                        if (status != 0){
                            System.err.println("Nhân viên đang nghỉ chế độ hoặc đã nghỉ việc!");
                            isCheck = true;
                        }
                    }
                }else {
                    isCheck = true;
                    System.err.println("Mã nhân viên chỉ từ 5 ký tự! Mời nhập lại!");
                }
            } while (isCheck);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
        return id_employee;
    }


}
