package businessImp;

import Presentation.Admin.EmployeePresentation;
import bussiness.ConsoleColors;
import entity.Employee;
import util.ConnectionDB;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeBusiness implements IBusiness<Employee, String> {
    @Override
    public List<Employee> findAll() {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Employee> employeeList = null;
        try {
            callSt = conn.prepareCall("{call get_all_infor_employee()}");
            ResultSet rs = callSt.executeQuery();
            employeeList = new ArrayList<>();
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmpId(rs.getString("emp_id"));
                employee.setEmpName(rs.getString("emp_name"));
                employee.setBirthOfDate(rs.getDate("birth_of_date"));
                employee.setEmail(rs.getString("email"));
                employee.setPhone(rs.getString("phone"));
                employee.setAddress(rs.getString("address"));
                employee.setEmpStatus(rs.getShort("emp_status"));
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return employeeList;
    }

    @Override
    public boolean create(Employee employee) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call add_infor_employee(?,?,?,?,?,?,?)}");
            callSt.setString(1, employee.getEmpId());
            callSt.setString(2, employee.getEmpName());
            java.util.Date createdDate = employee.getBirthOfDate();
            java.sql.Timestamp createdTimestamp = new java.sql.Timestamp(createdDate.getTime());
            callSt.setTimestamp(3, createdTimestamp);
            callSt.setString(4, employee.getEmail());
            callSt.setString(5, employee.getPhone());
            callSt.setString(6, employee.getAddress());
            callSt.setInt(7, employee.getEmpStatus());
            callSt.executeUpdate();
            conn.commit();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return result;
    }

    public int checkId(String id_check) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call check_employee_by_id(?,?)}");
            callSt.setString(1, id_check);
            callSt.registerOutParameter(2, Types.INTEGER);
            callSt.execute();
            int cnt_Employee = callSt.getInt(2);
            ConnectionDB.closeConnection(conn);
            if (cnt_Employee > 0) {
                return cnt_Employee;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateInfor(Employee employee) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_infor_employee(?,?,?,?,?,?)}");
            callSt.setString(1, employee.getEmpId());
            callSt.setString(2, employee.getEmpName());
            java.util.Date birthOfDate = employee.getBirthOfDate();
            java.sql.Timestamp createdTimestamp = new java.sql.Timestamp(birthOfDate.getTime());
            callSt.setTimestamp(3, createdTimestamp);
            callSt.setString(4, employee.getEmail());
            callSt.setString(5, employee.getPhone());
            callSt.setString(6, employee.getAddress());
            callSt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return true;
    }

    @Override
    public boolean updateStatus(Employee employee) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_status_employee(?,?)}");
            callSt.setString(1, employee.getEmpId());
            callSt.setInt(2, employee.getEmpStatus());
            callSt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return true;
    }

    @Override
    public List<Employee> search(String inputSearch) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        List<Employee> employeeList = null;
        try {

            callSt1 = conn.prepareCall("{call check_employee_by_name_or_id(?,?)}");
            callSt1.setString(1, inputSearch);
            callSt1.registerOutParameter(2, Types.INTEGER);
            callSt1.execute();
            int cnt_Employee = callSt1.getInt(2);
            if (cnt_Employee > 0) {
                callSt2 = conn.prepareCall("{call get_employee_by_name_or_id(?)}");
                callSt2.setString(1, inputSearch);
                ResultSet rs = callSt2.executeQuery();
                employeeList = new ArrayList<>();
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setEmpId(rs.getString("emp_id"));
                    employee.setEmpName(rs.getString("emp_name"));
                    employee.setBirthOfDate(rs.getDate("birth_of_date"));
                    employee.setEmail(rs.getString("email"));
                    employee.setPhone(rs.getString("phone"));
                    employee.setAddress(rs.getString("address"));
                    employee.setEmpStatus(rs.getShort("emp_status"));
                    employeeList.add(employee);
                }
                return employeeList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return null;
    }

    public void displayEmployee(List<Employee> employeeList) {
        employeeList = findAll();
        drawEmployeeTable(employeeList);
    }

    public void inputEmployee(Scanner scanner) {
        System.out.println("Nhập số nhân viên muốn thêm:");
        int n = ExceptionMethod.validateInteger(scanner);
        for (int i = 0; i < n; i++) {
            System.out.printf("Nhân viên thứ %d:\n", i + 1);
            Employee employee = new Employee();
            employee.inputData(scanner);
            boolean resultCreate = create(employee);
            System.out.println(ConsoleColors.makeColor(resultNotification(resultCreate, "Thêm"),ConsoleColors.GREEN));
        }
    }

    public void updateInforEmployee(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        System.out.println("Nhập mã cần cập nhật:");
        String id_update = ExceptionMethod.StringNotEmpty(scanner);
        if (id_update.length() <= 5) {
            try {
                if (checkId(id_update) > 0) {
                    callSt2 = conn.prepareCall("{call get_status_Employee(?)}");
                    callSt2.setString(1,id_update);
                    short empStatus = 0;
                    ResultSet rs2 = callSt2.executeQuery();
                    if (rs2.next()){
                        empStatus = rs2.getShort(1);
                    }
                    if (empStatus == 0){
                        callSt = conn.prepareCall("{call get_infor_to_update_employee(?)}");
                        callSt.setString(1, id_update);
                        ResultSet rs = callSt.executeQuery();
                        Employee employee = new Employee();
                        employee.setEmpId(id_update);
                        do {
                            System.out.println("MENU UPDATE EMPLOYEE");
                            System.out.println("1. Tên nhân viên\n2. Ngày sinh\n3. Email\n4. Số điện thoại\n5. Địa chỉ\n6. Thoát");
                            int choice = ExceptionMethod.validateInteger(scanner);
                            boolean resultUpdate;
                            switch (choice) {
                                case 1:
                                    employee.setEmpName(employee.inputEmployeeName(scanner));
                                    if (rs.next()) {
                                        employee.setBirthOfDate(rs.getDate("birth_of_date"));
                                        employee.setEmail(rs.getString("email"));
                                        employee.setPhone(rs.getString("phone"));
                                        employee.setAddress(rs.getString("address"));
                                    }
                                    resultUpdate = updateInfor(employee);
                                    System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                    break;
                                case 2:
                                    employee.setBirthOfDate(employee.inputEmployeeBirthOfDate(scanner));
                                    if (rs.next()) {
                                        employee.setEmpName(rs.getString("emp_name"));
                                        employee.setEmail(rs.getString("email"));
                                        employee.setPhone(rs.getString("phone"));
                                        employee.setAddress(rs.getString("address"));
                                    }
                                    resultUpdate = updateInfor(employee);
                                    System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                    break;
                                case 3:
                                    employee.setEmail(employee.inputEmail(scanner));
                                    if (rs.next()) {
                                        employee.setEmpName(rs.getString("emp_name"));
                                        employee.setBirthOfDate(rs.getDate("birth_of_date"));
                                        employee.setPhone(rs.getString("phone"));
                                        employee.setAddress(rs.getString("address"));
                                    }
                                    resultUpdate = updateInfor(employee);
                                    System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                    break;
                                case 4:
                                    employee.setPhone(employee.inputEmail(scanner));
                                    if (rs.next()) {
                                        employee.setEmpName(rs.getString("emp_name"));
                                        employee.setBirthOfDate(rs.getDate("birth_of_date"));
                                        employee.setEmail(rs.getString("email"));
                                        employee.setAddress(rs.getString("address"));
                                    }
                                    resultUpdate = updateInfor(employee);
                                    System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                    break;
                                case 5:
                                    employee.setAddress(employee.inputAddress(scanner));
                                    if (rs.next()) {
                                        employee.setEmpName(rs.getString("emp_name"));
                                        employee.setBirthOfDate(rs.getDate("birth_of_date"));
                                        employee.setEmail(rs.getString("email"));
                                        employee.setPhone(rs.getString("phone"));
                                    }
                                    resultUpdate = updateInfor(employee);
                                    System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                    break;
                                case 6:
                                    ConnectionDB.closeConnection(conn);
                                    EmployeePresentation.menuEmployee(scanner);
                                    break;
                                default:
                                    System.err.println("Mời nhập từ 1 đến 6!");
                                    break;
                            }
                        } while (true);
                    }else {
                        System.err.println("Trạng thái nhân viên đang nghỉ chế độ hoặc nghỉ việc");
                    }
                } else {
                    System.err.println("Không tồn tại mã sản phẩm! Ấn 3 để nhập lại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Mã nhân viên chỉ từ 5 ký tự! Ấn 3 để nhập lại");
        }
    }

    public String resultNotification(boolean result, String types) {
        if (result) {
            return types + " thành công!";
        } else {
            return "Có lỗi trong quá trình thực hiện!";
        }
    }

    public void searchEmployee(Scanner scanner, List<Employee> employeeList) {
        System.out.println("Nhập mã hoặc tên nhân viên tìm kiếm:");
        String key_word_Search = ExceptionMethod.StringNotEmpty(scanner);
        employeeList = search(key_word_Search);
        if (employeeList != null) {
            drawEmployeeTable(employeeList);
        } else {
            System.err.println("Nhân viên bạn nhập không tồn tại!");
        }
    }

    public void updateStatusEmployee(Scanner scanner, List<Employee> employeeList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        displayEmployee(employeeList);
        System.out.println("Mã nhân viên cần cập nhật trạng thái:");
        String id_update_status = ExceptionMethod.StringNotEmpty(scanner);
        if (id_update_status.length() <= 5) {
            if (checkId(id_update_status) > 0) {
                boolean resultUpdate;
                Employee employee = new Employee();
                employee.setEmpId(id_update_status);
                try {
                    callSt = conn.prepareCall("{call get_acc_id_by_empId(?)}");
                    callSt.setString(1,id_update_status);
                    ResultSet rs = callSt.executeQuery();
                    int empId = 0;
                    if (rs.next()){
                        empId = rs.getInt(1);
                    }
                    System.out.println("Bạn muốn cập nhật trạng thái nhân viên thế nào?\n0. Hoạt động\n1. Nghỉ chế độ\n2. Nghỉ việc\n3. Thoát");
                    do {
                        int choice = ExceptionMethod.validateInteger(scanner);
                        callSt2 = conn.prepareCall("{call update_status_account(?,?)}");
                        callSt2.setInt(1,empId);
                        switch (choice) {
                            case 0:
                                employee.setEmpStatus((short) 0);
                                resultUpdate = updateStatus(employee);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                EmployeePresentation.menuEmployee(scanner);
                                break;
                            case 1:
                                employee.setEmpStatus((short) 1);
                                resultUpdate = updateStatus(employee);
                                callSt2.setBoolean(2,false);
                                callSt2.executeUpdate();
                                ConnectionDB.closeConnection(conn);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                EmployeePresentation.menuEmployee(scanner);
                                break;
                            case 2:
                                employee.setEmpStatus((short) 2);
                                resultUpdate = updateStatus(employee);
                                callSt2.setBoolean(2,false);
                                callSt2.executeUpdate();
                                ConnectionDB.closeConnection(conn);
                                System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                                EmployeePresentation.menuEmployee(scanner);
                                break;
                            case 3:
                                EmployeePresentation.menuEmployee(scanner);
                                break;
                            default:
                                System.out.println("Mời nhập từ 0 đến 3!");
                                break;
                        }
                    } while (true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Không tồn tại mã nhân viên! Ấn 4 để nhập lại!");
            }
        } else {
            System.err.println("Mã nhân viên chỉ từ 5 ký tự! Mời nhập lại!");
        }
    }

    public static void drawEmployeeTable(List<Employee> employeeList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("                                      Bảng thông tin nhân viên                           ");
        for (int i = 0; i < 143; i++) {
            System.out.print("─");
        }
        System.out.println();
        System.out.printf("|%-15s|%-25s|%-12s|%-30s|%-18s|%-20s|%-15s|%n",
                "Mã nhân viên",
                "Tên nhân viên",
                "Ngày sinh",
                "Email",
                "Số điện thoại",
                "Địa chỉ",
                "Trạng thái");
        for (int i = 0; i < 143; i++) {
            System.out.print("─");
        }
        System.out.println();
        for (Employee employee : employeeList) {
            System.out.printf("|%-15s|%-25s|%-12s|%-30s|%-18s|%-20s|%-15s|%n",
                    employee.getEmpId(),
                    employee.getEmpName(),
                    sdf.format(employee.getBirthOfDate()),
                    employee.getEmail(),
                    employee.getPhone(),
                    employee.getAddress(),
                    (employee.getEmpStatus() == 0 ? "Hoạt động" : employee.getEmpStatus() == 1 ? "Nghỉ chế độ" : "Nghỉ việc"));
            for (int i = 0; i < 143; i++) {
                System.out.print("─");
            }
            System.out.println();
        }
    }

}
