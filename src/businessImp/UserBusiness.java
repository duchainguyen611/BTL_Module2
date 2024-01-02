package businessImp;

import Presentation.Admin.ProductPresentation;
import Presentation.User.UserPresentation;
import bussiness.ConsoleColors;
import entity.Bill;
import entity.Bill_Detail;
import util.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserBusiness {

    BillBusiness billBusiness = new BillBusiness();
    ReceiptBusiness receiptBusiness = new ReceiptBusiness();
    Bill_DetailBusiness billDetailBusiness = new Bill_DetailBusiness();

    public List<Bill> findAll(Boolean type, String id_emp) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Bill> billList = null;
        try {
            callSt = conn.prepareCall("{call get_bill_by_id_user(?,?)}");
            callSt.setString(1, id_emp);
            callSt.setBoolean(2, type);
            ResultSet rs = callSt.executeQuery();
            billList = new ArrayList<>();
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setBillCode(rs.getString("bill_code"));
                bill.setBillType(rs.getBoolean("bill_type"));
                bill.setEmpIdCreated(rs.getString("emp_id_created"));
                bill.setCreated(rs.getDate("created"));
                bill.setEmpIdAuth(rs.getString("emp_id_auth"));
                bill.setAuthDate(rs.getDate("auth_date"));
                bill.setBillStatus(rs.getShort("bill_status"));
                billList.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return billList;
    }

    public void createByIdEmp(Scanner scanner, Boolean type, String empId) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập số phiếu muốn thêm:");
        int n = ExceptionMethod.validateInteger(scanner);
        for (int i = 0; i < n; i++) {
            System.out.printf("Phiếu nhập thứ %d:\n", i + 1);
            Bill bill = new Bill();
            bill.setBillCode(bill.inputBillCode(scanner));
            bill.setBillType(type);
            bill.setEmpIdCreated(empId);
            boolean resultCreateBill = receiptBusiness.create(bill);
            System.out.println("Nhập số sản phẩm muốn nhập:");
            int m = ExceptionMethod.validateInteger(scanner);
            for (int j = 0; j < m; j++) {
                Bill_Detail billDetail = new Bill_Detail();
                try {
                    callSt = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt.setString(1, bill.getBillCode());
                    ResultSet rs = callSt.executeQuery();
                    if (rs.next()) {
                        billDetail.setBillId(rs.getInt(1));
                    }
                    billDetail.setProductId(billDetail.inputProductId(scanner, ProductPresentation.productList));
                    billDetail.setQuantity(billDetail.inputQuantity(scanner));
                    billDetail.setPrice(billDetail.inputPrice(scanner));
                    boolean resultCreateBillDetail = billDetailBusiness.create(billDetail);
                    System.out.println(ConsoleColors.makeColor(receiptBusiness.resultNotification(resultCreateBill, resultCreateBillDetail, "Thêm"),ConsoleColors.GREEN));
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Bill> search(String inputSearch, boolean type) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        List<Bill> billList = null;
        try {
            callSt1 = conn.prepareCall("{call check_bill_by_id_bill_or_code_bill_and_idEmp(?,?,?,?)}");
            callSt1.setString(1, inputSearch);
            callSt1.setString(2, LogInBusiness.account.getEmpId());
            callSt1.setBoolean(3, type);
            callSt1.registerOutParameter(4, Types.INTEGER);
            callSt1.execute();
            int cnt_Bill = callSt1.getInt(4);
            if (cnt_Bill > 0) {
                callSt2 = conn.prepareCall("{call get_bill_by_id_bill_or_code_bill_and_idEmp(?,?,?)}");
                callSt2.setString(1, inputSearch);
                callSt2.setString(2, LogInBusiness.account.getEmpId());
                callSt2.setBoolean(3, type);
                ResultSet rs = callSt2.executeQuery();
                billList = new ArrayList<>();
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setBillCode(rs.getString("bill_code"));
                    bill.setBillType(rs.getBoolean("bill_type"));
                    bill.setEmpIdCreated(rs.getString("emp_id_created"));
                    bill.setCreated(rs.getDate("created"));
                    bill.setEmpIdAuth(rs.getString("emp_id_auth"));
                    bill.setAuthDate(rs.getDate("auth_date"));
                    bill.setBillStatus(rs.getShort("bill_status"));
                    billList.add(bill);
                }
                return billList;
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

    public void displayReceiptByIdEmp(List<Bill> billList) {
        billList = findAll(true, LogInBusiness.account.getEmpId());
        receiptBusiness.drawReceiptTable(billList);
    }

    public void createReceiptByIdEmp(Scanner scanner) {
        createByIdEmp(scanner, true, LogInBusiness.account.getEmpId());
    }

    public void displayBillByIdEmp(List<Bill> billList) {
        billList = findAll(false, LogInBusiness.account.getEmpId());
        billBusiness.drawBillTable(billList);
    }

    public void createBillByIdEmp(Scanner scanner) {
        createByIdEmp(scanner, false, LogInBusiness.account.getEmpId());
    }


    public int checkId(String input_check, Boolean type) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call check_duplicate_bill_by_id_or_code_empId(?,?,?,?)}");
            callSt.setString(1, input_check);
            callSt.setBoolean(2, type);
            callSt.setString(3, LogInBusiness.account.getEmpId());
            callSt.registerOutParameter(4, Types.INTEGER);
            callSt.execute();
            int cnt_Bill = callSt.getInt(4);
            ConnectionDB.closeConnection(conn);
            if (cnt_Bill > 0) {
                return cnt_Bill;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void updateReceiptByIdEmp(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        CallableStatement callSt3 = null;
        CallableStatement callSt4 = null;
        CallableStatement callSt5 = null;
        CallableStatement callSt6 = null;
        System.out.println("Nhập mã phiếu hoặc mã code cần cập nhật:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            try {
                if (checkId(input_check, true) > 0) {
                    callSt3 = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt3.setString(1, input_check);
                    ResultSet rs = callSt3.executeQuery();
                    int id_update = 0;
                    if (rs.next()) {
                        id_update = rs.getInt(1);
                    }
                    callSt4 = conn.prepareCall("{call get_status_bill_by_id(?)}");
                    callSt4.setInt(1, id_update);
                    ResultSet rs3 = callSt4.executeQuery();
                    short status = 0;
                    if (rs3.next()) {
                        status = rs3.getShort(1);
                    }
                    if (status != 2) {
                        callSt5 = conn.prepareCall("{call get_infor_bill_by_billId(?)}");
                        callSt5.setInt(1, id_update);
                        ResultSet rs4 = callSt5.executeQuery();
                        Bill bill2 = null;
                        while (rs4.next()) {
                            bill2 = new Bill();
                            bill2.setBillId(rs4.getInt("bill_id"));
                            bill2.setBillCode(rs4.getString("bill_code"));
                            bill2.setBillType(rs4.getBoolean("bill_type"));
                            bill2.setEmpIdCreated(rs4.getString("emp_id_created"));
                            bill2.setCreated(rs4.getDate("created"));
                            bill2.setEmpIdAuth(rs4.getString("emp_id_auth"));
                            bill2.setAuthDate(rs4.getDate("auth_date"));
                            bill2.setBillStatus(rs4.getShort("bill_status"));
                        }
                        List<Bill> billList = new ArrayList<>();
                        receiptBusiness.displayReceipt(billList);
                        List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_update);
                        billDetailBusiness.drawBillDetailTable(billDetailList);
                        callSt = conn.prepareCall("{call get_infor_to_update_bill(?)}");
                        callSt.setInt(1, id_update);
                        ResultSet rs1 = callSt.executeQuery();
                        Bill bill = new Bill();
                        bill.setBillId(id_update);
                        System.out.println("Nhập mã chi tiết phiếu để cập nhật:");
                        int idBillDetail = ExceptionMethod.validateInteger(scanner);
                        callSt6 = conn.prepareCall("{call check_infor_to_update_bill_detail(?,?,?)}");
                        callSt6.setInt(1, idBillDetail);
                        callSt6.setInt(2, id_update);
                        callSt6.registerOutParameter(3, Types.INTEGER);
                        callSt6.execute();
                        int cnt_detailBill = callSt6.getInt(3);
                        if (cnt_detailBill > 0) {
                            callSt2 = conn.prepareCall("{call get_infor_to_update_bill_detail(?,?)}");
                            callSt2.setInt(1, idBillDetail);
                            callSt2.setInt(2, id_update);
                            ResultSet rs2 = callSt2.executeQuery();
                            Bill_Detail billDetail = new Bill_Detail();
                            billDetail.setBillDetailId(idBillDetail);
                            billDetail.setBillId(id_update);
                            do {
                                System.out.println("MENU UPDATE RECEIPT");
                                System.out.println("1. Trạng thái phiếu\n2. Số lượng sản phẩm\n3. Giá sản phẩm\n4. Thoát");
                                int choice = ExceptionMethod.validateInteger(scanner);
                                boolean resultUpdateBill, resultUpdateBillDetail;
                                switch (choice) {
                                    case 1:
                                        bill.setBillStatus(bill.inputBillStatus(scanner));
                                        if (rs1.next()) {
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = receiptBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(receiptBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 2:
                                        billDetail.setQuantity(billDetail.inputQuantity(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = receiptBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(receiptBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 3:
                                        billDetail.setPrice(billDetail.inputPrice(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                        }
                                        resultUpdateBill = receiptBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(receiptBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                    case 4:
                                        ConnectionDB.closeConnection(conn);
                                        UserPresentation.menuUser(scanner);
                                        break;
                                    default:
                                        System.err.println("Mời nhập từ 1 đến 4!");
                                        break;
                                }
                            } while (true);
                        } else {
                            System.err.println("Mã chi tiết phiếu không đúng!");
                        }
                    } else {
                        System.err.println("Phiếu đã duyệt không thể cập nhật!");
                    }
                } else {
                    System.err.println("Không tồn tại mã phiếu nhập! Ấn 3 để nhập lại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 3 để nhập lại!");
        }
    }

    public void updateBillByIdEmp(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        CallableStatement callSt2 = null;
        CallableStatement callSt3 = null;
        CallableStatement callSt4 = null;
        CallableStatement callSt5 = null;
        CallableStatement callSt6 = null;
        System.out.println("Nhập mã phiếu hoặc mã code cần cập nhật:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            try {
                if (checkId(input_check, false) > 0) {
                    callSt3 = conn.prepareCall("{call get_id_to_update_by_id_or_code_empId(?)}");
                    callSt3.setString(1, input_check);
                    ResultSet rs = callSt3.executeQuery();
                    int id_update = 0;
                    if (rs.next()) {
                        id_update = rs.getInt(1);
                    }
                    callSt4 = conn.prepareCall("{call get_status_bill_by_id(?)}");
                    callSt4.setInt(1, id_update);
                    ResultSet rs3 = callSt4.executeQuery();
                    short status = 0;
                    if (rs3.next()) {
                        status = rs3.getShort(1);
                    }
                    if (status != 2) {
                        System.out.println("Thông tin phiếu:");
                        callSt5 = conn.prepareCall("{call get_infor_bill_by_billId(?)}");
                        callSt5.setInt(1, id_update);
                        ResultSet rs4 = callSt5.executeQuery();
                        Bill bill2 = null;
                        while (rs4.next()) {
                            bill2 = new Bill();
                            bill2.setBillId(rs4.getInt("bill_id"));
                            bill2.setBillCode(rs4.getString("bill_code"));
                            bill2.setBillType(rs4.getBoolean("bill_type"));
                            bill2.setEmpIdCreated(rs4.getString("emp_id_created"));
                            bill2.setCreated(rs4.getDate("created"));
                            bill2.setEmpIdAuth(rs4.getString("emp_id_auth"));
                            bill2.setAuthDate(rs4.getDate("auth_date"));
                            bill2.setBillStatus(rs4.getShort("bill_status"));
                        }
                        List<Bill> billList = new ArrayList<>();
                        billBusiness.displayBill(billList);
                        List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_update);
                        billDetailBusiness.drawBillDetailTable(billDetailList);
                        callSt = conn.prepareCall("{call get_infor_to_update_bill(?)}");
                        callSt.setInt(1, id_update);
                        ResultSet rs1 = callSt.executeQuery();
                        Bill bill = new Bill();
                        bill.setBillId(id_update);
                        System.out.println("Nhập mã chi tiết phiếu để cập nhật:");
                        int idBillDetail = ExceptionMethod.validateInteger(scanner);
                        callSt6 = conn.prepareCall("{call check_infor_to_update_bill_detail(?,?,?)}");
                        callSt6.setInt(1, idBillDetail);
                        callSt6.setInt(2, id_update);
                        callSt6.registerOutParameter(3, Types.INTEGER);
                        callSt6.execute();
                        int cnt_detailBill = callSt6.getInt(3);
                        if (cnt_detailBill > 0) {
                            callSt2 = conn.prepareCall("{call get_infor_to_update_bill_detail(?,?)}");
                            callSt2.setInt(1, idBillDetail);
                            callSt2.setInt(2, id_update);
                            ResultSet rs2 = callSt2.executeQuery();
                            Bill_Detail billDetail = new Bill_Detail();
                            billDetail.setBillDetailId(idBillDetail);
                            billDetail.setBillId(id_update);
                            do {
                                System.out.println("MENU UPDATE BILL");
                                System.out.println("1. Trạng thái phiếu\n2. Số lượng sản phẩm\n3. Giá sản phẩm\n4. Thoát");
                                int choice = ExceptionMethod.validateInteger(scanner);
                                boolean resultUpdateBill, resultUpdateBillDetail;
                                switch (choice) {
                                    case 1:
                                        bill.setBillStatus(bill.inputBillStatus(scanner));
                                        if (rs1.next()) {
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = billBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(billBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 2:
                                        billDetail.setQuantity(billDetail.inputQuantity(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setPrice(rs2.getFloat("price"));
                                        }
                                        resultUpdateBill = billBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(billBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                        break;
                                    case 3:
                                        billDetail.setPrice(billDetail.inputPrice(scanner));
                                        if (rs1.next()) {
                                            bill.setBillStatus(rs1.getShort("bill_status"));
                                            bill.setEmpIdCreated(rs1.getString("emp_id_created"));
                                        }
                                        if (rs2.next()) {
                                            billDetail.setQuantity(rs2.getInt("quantity"));
                                        }
                                        resultUpdateBill = billBusiness.updateInfor(bill);
                                        resultUpdateBillDetail = billDetailBusiness.updateInfor(billDetail);
                                        System.out.println(ConsoleColors.makeColor(billBusiness.resultNotification(resultUpdateBill, resultUpdateBillDetail, "Cập nhật"),ConsoleColors.GREEN));
                                    case 4:
                                        ConnectionDB.closeConnection(conn);
                                        UserPresentation.menuUser(scanner);
                                        break;
                                    default:
                                        System.err.println("Mời nhập từ 1 đến 4!");
                                        break;
                                }
                            } while (true);
                        }else {
                            System.err.println("Mã chi tiết phiếu không đúng!");
                        }
                    } else {
                        System.err.println("Phiếu đã duyệt không thể cập nhật!");
                    }
                } else {
                    System.err.println("Không tồn tại mã phiếu nhập! Ấn 7 để nhập lại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 7 để nhập lại!");
        }
    }

    public void searchReceiptByIdEmp(List<Bill> billList, Scanner scanner) {
        System.out.println("Nhập từ khóa mã phiếu hoặc mã code muốn xem chi tiết phiếu:");
        String input_search = ExceptionMethod.StringNotEmpty(scanner);
        if (input_search.length() <= 10) {
            billList = search(input_search, true);
            if (billList != null) {
                receiptBusiness.drawReceiptTable(billList);

            } else {
                System.err.println("Thông tin về phiếu nhập bạn nhập không tồn tại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 6 để nhập lại!");
        }
    }

    public void searchBillByIdEmp(List<Bill> billList, Scanner scanner) {
        System.out.println("Nhập từ khóa mã phiếu hoặc mã code muốn xem chi tiết phiếu:");
        String input_search = ExceptionMethod.StringNotEmpty(scanner);
        if (input_search.length() <= 10) {
            billList = search(input_search, false);
            if (billList != null) {
                billBusiness.drawBillTable(billList);
            } else {
                System.err.println("Thông tin về phiếu nhập bạn nhập không tồn tại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 6 để nhập lại!");
        }
    }

    public void detailReceipt(Scanner scanner, List<Bill> billList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        displayReceiptByIdEmp(billList);
        System.out.println("Nhập mã phiếu hoặc mã code muốn xem chi tiết phiếu:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            System.out.println("Thông tin chi tiết phiếu:");
            if (checkId(input_check,true) > 0) {
                try {
                    callSt = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt.setString(1, input_check);
                    ResultSet rs = callSt.executeQuery();
                    int id_bill = 0;
                    if (rs.next()) {
                        id_bill = rs.getInt(1);
                    }
                    List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_bill);
                    billDetailBusiness.drawBillDetailTable(billDetailList);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Mã phiếu không tại! Ấn 4 để nhập lại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 4 để nhập lại!");
        }
    }

    public void detailBill(Scanner scanner, List<Bill> billList) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        displayBillByIdEmp(billList);
        System.out.println("Nhập mã phiếu hoặc mã code muốn xem chi tiết phiếu:");
        String input_check = ExceptionMethod.StringNotEmpty(scanner);
        if (input_check.length() <= 10) {
            System.out.println("Thông tin chi tiết phiếu:");
            if (checkId(input_check,false) > 0) {
                try {
                    callSt = conn.prepareCall("{call get_id_to_update_by_id_or_code(?)}");
                    callSt.setString(1, input_check);
                    ResultSet rs = callSt.executeQuery();
                    int id_bill = 0;
                    if (rs.next()) {
                        id_bill = rs.getInt(1);
                    }
                    List<Bill_Detail> billDetailList = billDetailBusiness.findAll(id_bill);
                    billDetailBusiness.drawBillDetailTable(billDetailList);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Mã phiếu không tại! Ấn 4 để nhập lại!");
            }
        } else {
            System.err.println("Tối đa 10 ký tự! Ấn 4 để nhập lại!");
        }
    }
}
