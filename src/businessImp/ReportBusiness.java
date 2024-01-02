package businessImp;

import util.ConnectionDB;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class ReportBusiness {
    public void statisticReceiptByDay(Scanner scanner) {
        System.out.println("Nhập ngày tháng năm để thống kê:");
        Date inputDate = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call get_sum_money_by_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp = new java.sql.Timestamp(inputDate.getTime());
            callSt.setTimestamp(1, createdTimestamp);
            callSt.setBoolean(2, true);
            callSt.setShort(3, (short) 2);
            callSt.registerOutParameter(4, Types.INTEGER);
            callSt.execute();
            int sum = callSt.getInt(4);
            System.out.println("Chi phi = " + sum);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticReceiptFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call get_sum_money_from_day_to_day(?,?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, true);
            callSt.setShort(4, (short) 2);
            callSt.registerOutParameter(5, Types.INTEGER);
            callSt.execute();
            int sum = callSt.getInt(5);
            System.out.println("Chi phi = " + sum);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticBillByDay(Scanner scanner) {
        System.out.println("Nhập ngày tháng năm để thống kê:");
        Date inputDate = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call get_sum_money_by_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp = new java.sql.Timestamp(inputDate.getTime());
            callSt.setTimestamp(1, createdTimestamp);
            callSt.setBoolean(2, false);
            callSt.setShort(3, (short) 2);
            callSt.registerOutParameter(4, Types.INTEGER);
            callSt.execute();
            int sum = callSt.getInt(4);
            System.out.println("Doanh thu = " + sum);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticBillFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call get_sum_money_from_day_to_day(?,?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, false);
            callSt.setShort(4, (short) 2);
            callSt.registerOutParameter(5, Types.INTEGER);
            callSt.execute();
            int sum = callSt.getInt(5);
            System.out.println("Doanh thu = " + sum);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticEmpById(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call count_employee_by_status()}");
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                short empStatus = rs.getShort(1);
                int empCount = rs.getInt(2);
                System.out.println((empStatus == 0 ? "Nhân viên đang hoạt động: " : empStatus == 1 ? "Nhân viên nghỉ chế độ: " : "Nhân viên nghỉ việc: ") + empCount + " nhân viên");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticMaxReceiptFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call statistic_max_from_day_to_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, true);
            callSt.setShort(4, (short) 2);
            ResultSet rs = callSt.executeQuery();
            if (rs.next()) {
                String nameProduct = rs.getString(1);
                int sumQuantity = rs.getInt(2);
                System.out.println("Sản phẩm " + nameProduct + " : " + sumQuantity + " số lượng");
            } else {
                System.err.println("Không có dữ liệu theo thông tin nhập vào");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticMinReceiptFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call statistic_min_from_day_to_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, true);
            callSt.setShort(4, (short) 2);
            ResultSet rs = callSt.executeQuery();
            if (rs.next()) {
                String nameProduct = rs.getString(1);
                int sumQuantity = rs.getInt(2);
                System.out.println("Sản phẩm " + nameProduct + " : " + sumQuantity + " số lượng");
            } else {
                System.err.println("Không có dữ liệu theo thông tin nhập vào");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticMaxBillFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call statistic_max_from_day_to_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, false);
            callSt.setShort(4, (short) 2);
            ResultSet rs = callSt.executeQuery();
            if (rs.next()) {
                String nameProduct = rs.getString(1);
                int sumQuantity = rs.getInt(2);
                System.out.println("Sản phẩm " + nameProduct + " : " + sumQuantity + " số lượng");
            } else {
                System.err.println("Không có dữ liệu theo thông tin nhập vào");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

    public void statisticMinBillFromDayToDay(Scanner scanner) {
        System.out.println("Nhập khoảng thời gian bắt đầu để thống kê:");
        Date inputDate1 = ExceptionMethod.validateDate(scanner);
        System.out.println("Nhập khoảng thời gian kết thúc để thống kê:");
        Date inputDate2 = ExceptionMethod.validateDate(scanner);
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("call statistic_min_from_day_to_day(?,?,?,?)");
            java.sql.Timestamp createdTimestamp1 = new java.sql.Timestamp(inputDate1.getTime());
            callSt.setTimestamp(1, createdTimestamp1);
            java.sql.Timestamp createdTimestamp2 = new java.sql.Timestamp(inputDate2.getTime());
            callSt.setTimestamp(2, createdTimestamp2);
            callSt.setBoolean(3, false);
            callSt.setShort(4, (short) 2);
            ResultSet rs = callSt.executeQuery();
            if (rs.next()) {
                String nameProduct = rs.getString(1);
                int sumQuantity = rs.getInt(2);
                System.out.println("Sản phẩm " + nameProduct + " : " + sumQuantity + " số lượng");
            } else {
                System.err.println("Không có dữ liệu theo thông tin nhập vào");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
    }

}
