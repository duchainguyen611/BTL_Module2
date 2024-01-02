package businessImp;

import Presentation.Admin.AccountPresentation;
import bussiness.ConsoleColors;
import entity.Account;
import util.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountBusiness implements IBusiness<Account, String> {
    @Override
    public List<Account> findAll() {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        List<Account> accountList = null;
        try {
            callSt = conn.prepareCall("{call get_all_infor_account()}");
            ResultSet rs = callSt.executeQuery();
            accountList = new ArrayList<>();
            while (rs.next()) {
                Account account = new Account();
                account.setAccId(rs.getInt("acc_id"));
                account.setUserName(rs.getString("user_name"));
                account.setPassWord(rs.getString("password"));
                account.setPermission(rs.getBoolean("permission"));
                account.setEmpId(rs.getString("emp_id"));
                account.setAccStatus(rs.getBoolean("acc_status"));
                accountList.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn);
        }
        return accountList;
    }

    @Override
    public boolean create(Account account) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call add_infor_account(?,?,?)}");
            callSt.setString(1, account.getUserName());
            callSt.setString(2, account.getPassWord());
            callSt.setString(3, account.getEmpId());
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

    public int checkId(int id_check) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            callSt = conn.prepareCall("{call check_account_by_id(?,?)}");
            callSt.setInt(1, id_check);
            callSt.registerOutParameter(2, Types.INTEGER);
            callSt.execute();
            int cnt_account = callSt.getInt(2);
            ConnectionDB.closeConnection(conn);
            if (cnt_account > 0) {
                return cnt_account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    @Override
    public boolean updateInfor(Account account) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_infor_account(?,?,?,?,?)}");
            callSt.setInt(1, account.getAccId());
            callSt.setString(2, account.getUserName());
            callSt.setString(3, account.getPassWord());
            callSt.setBoolean(4, account.isPermission());
            callSt.setString(5, account.getEmpId());
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
    public boolean updateStatus(Account account) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        try {
            conn.setAutoCommit(false);
            callSt = conn.prepareCall("{call update_status_account(?,?)}");
            callSt.setInt(1, account.getAccId());
            callSt.setBoolean(2, account.isAccStatus());
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
    public List<Account> search(String inputSearch) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt1 = null;
        CallableStatement callSt2 = null;
        List<Account> accountList = null;
        try {

            callSt1 = conn.prepareCall("{call check_account_by_username_or_nameEmp(?,?)}");
            callSt1.setString(1, inputSearch);
            callSt1.registerOutParameter(2, Types.INTEGER);
            callSt1.execute();
            int cnt_account = callSt1.getInt(2);
            if (cnt_account > 0) {
                callSt2 = conn.prepareCall("{call get_account_by_username_or_nameEmp(?)}");
                callSt2.setString(1, inputSearch);
                ResultSet rs = callSt2.executeQuery();
                accountList = new ArrayList<>();
                while (rs.next()) {
                    Account account = new Account();
                    account.setAccId(rs.getInt("acc_id"));
                    account.setUserName(rs.getString("user_name"));
                    account.setPassWord(rs.getString("password"));
                    account.setPermission(rs.getBoolean("permission"));
                    account.setEmpId(rs.getString("emp_id"));
                    account.setAccStatus(rs.getBoolean("acc_status"));
                    accountList.add(account);
                }
                return accountList;
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

    public void displayAccount(List<Account> accountList) {
        accountList = findAll();
        drawAccountTable(accountList);
    }

    public void inputAccount(Scanner scanner) {
        System.out.println("Nhập số tài khoản muốn thêm:");
        int n = ExceptionMethod.validateInteger(scanner);
        for (int i = 0; i < n; i++) {
            System.out.printf("Tài khoản thứ %d:\n", i + 1);
            Account account = new Account();
            account.inputData(scanner);
            boolean resultCreate = create(account);
            System.out.println(ConsoleColors.makeColor(resultNotification(resultCreate, "Thêm"),ConsoleColors.GREEN));
        }
    }

    public void updateInforAccount(Scanner scanner) {
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callSt = null;
        System.out.println("Nhập mã cần cập nhật:");
        int id_update = ExceptionMethod.validateInteger(scanner);
        try {
            if (checkId(id_update) > 0) {
                callSt = conn.prepareCall("{call get_infor_to_update_account(?)}");
                callSt.setInt(1, id_update);
                ResultSet rs = callSt.executeQuery();
                Account account = new Account();
                account.setAccId(id_update);
                do {
                    System.out.println("MENU UPDATE ACCOUNT");
                    System.out.println("1. Tên tài khoản\n2. Mật khẩu\n3. Quyền tài khoản\n4. Mã nhân viên\n5. Thoát");
                    int choice = ExceptionMethod.validateInteger(scanner);
                    boolean resultUpdate;
                    switch (choice) {
                        case 1:
                            account.setUserName(account.inputUserName(scanner));
                            if (rs.next()) {
                                account.setPassWord(rs.getString("password"));
                                account.setPermission(rs.getBoolean("permission"));
                                account.setEmpId(rs.getString("emp_id"));
                            }
                            resultUpdate = updateInfor(account);
                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                            break;
                        case 2:
                            account.setPassWord(account.inputPassword(scanner));
                            if (rs.next()) {
                                account.setUserName(rs.getString("user_name"));
                                account.setPermission(rs.getBoolean("permission"));
                                account.setEmpId(rs.getString("emp_id"));
                            }
                            resultUpdate = updateInfor(account);
                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                            break;
                        case 3:
                            account.setPermission(account.inputPermission(scanner));
                            if (rs.next()) {
                                account.setUserName(rs.getString("user_name"));
                                account.setPassWord(rs.getString("password"));
                                account.setEmpId(rs.getString("emp_id"));
                            }
                            resultUpdate = updateInfor(account);
                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                            break;
                        case 4:
                            account.setEmpId(account.inputEmpId(scanner));
                            if (rs.next()) {
                                account.setUserName(rs.getString("user_name"));
                                account.setPassWord(rs.getString("password"));
                                account.setPermission(rs.getBoolean("permission"));
                            }
                            resultUpdate = updateInfor(account);
                            System.out.println(ConsoleColors.makeColor(resultNotification(resultUpdate, "Cập nhật"),ConsoleColors.GREEN));
                            break;
                        case 5:
                            ConnectionDB.closeConnection(conn);
                            AccountPresentation.menuAccount(scanner);
                            break;
                        default:
                            System.err.println("Mời nhập từ 1 đến 5!");
                            break;
                    }
                } while (true);
            } else {
                System.err.println("Không tồn tại mã tài khoản! Ấn 3 để nhập lại!");
                AccountPresentation.menuAccount(scanner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String resultNotification(boolean result, String types) {
        if (result) {
            return types + " thành công!";
        } else {
            return "Có lỗi trong quá trình thực hiện!";
        }
    }

    public void searchAccount(Scanner scanner, List<Account> accountList) {
        System.out.println("Nhập tên đăng nhập hoặc tên nhân viên tìm kiếm:");
        String key_word_Search = ExceptionMethod.StringNotEmpty(scanner);
        accountList = search(key_word_Search);
        if (accountList != null) {
            drawAccountTable(accountList);
            boolean isExit = false;
            do {
                System.out.println("Bạn muốn cập nhật trạng thái tài khoản không?\n1. Có\n2. Thoát");
                int choice = ExceptionMethod.validateInteger(scanner);
                switch (choice) {
                    case 1:
                        Connection conn = ConnectionDB.openConnection();
                        CallableStatement callSt = null;
                        CallableStatement callSt2 = null;
                        System.out.println("Nhập mã tài khoản bạn muốn cập nhật trạng thái:");
                        try {
                            int id_update_status = ExceptionMethod.validateInteger(scanner);
                            callSt = conn.prepareCall("{call check_account_by_and_id_username_or_nameEmp(?,?,?)}");
                            callSt.setString(1, key_word_Search);
                            callSt.setInt(2, id_update_status);
                            callSt.registerOutParameter(3, Types.INTEGER);
                            callSt.execute();
                            int check_acc = callSt.getInt(3);
                            if (check_acc > 0) {
                                int choice2;
                                do {
                                    System.out.println("Bạn muốn cập nhật trạng thái tài khoản như thế nào?\n1. Active\n2. Block");
                                    choice2 = ExceptionMethod.validateInteger(scanner);
                                    conn.setAutoCommit(false);
                                    callSt2 = conn.prepareCall("{call update_status_account_by_username_or_nameEmp(?,?,?)}");
                                    callSt2.setString(1, key_word_Search);
                                    callSt2.setInt(2, id_update_status);
                                    switch (choice2) {
                                        case 1:
                                            callSt2.setBoolean(3, true);
                                            callSt2.executeUpdate();
                                            conn.commit();
                                            ConnectionDB.closeConnection(conn);
                                            isExit = true;
                                            System.out.println(ConsoleColors.makeColor("Cập nhật thành công!",ConsoleColors.GREEN));
                                            AccountPresentation.menuAccount(scanner);
                                            break;
                                        case 2:
                                            callSt2.setBoolean(3, false);
                                            callSt2.executeUpdate();
                                            conn.commit();
                                            ConnectionDB.closeConnection(conn);
                                            isExit = true;
                                            System.out.println(ConsoleColors.makeColor("Cập nhật thành công!",ConsoleColors.GREEN));
                                            AccountPresentation.menuAccount(scanner);
                                            break;
                                        default:
                                            System.err.println("Mời nhập 1 và 2!");
                                            break;
                                    }
                                } while (choice2 == 1 || choice2 == 2);
                            }else {
                                System.err.println("Mã bạn không tồn tại theo những kết quả tìm kiếm ở trên!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        AccountPresentation.menuAccount(scanner);
                        break;
                    default:
                        System.err.println("Mời nhập 1 và 2!");
                        break;
                }
            } while (!isExit);
        } else {
            System.err.println("Thông tin về tài khoản bạn nhập không tồn tại!");
        }
    }

    public static void drawAccountTable(List<Account> accountList) {
        Connection conn = null;
        try {
            conn = ConnectionDB.openConnection();
            CallableStatement callSt = conn.prepareCall("call get_empName_in_account(?)");
            System.out.println("                                         Bảng thông tin tài khoản                         ");
            for (int i = 0; i < 102; i++) {
                System.out.print("─");
            }
            System.out.println();
            System.out.printf("|%-10s|%-20s|%-20s|%-15s|%-20s|%-10s|%n", "Mã TK", "Tên đăng nhập", "Mật khẩu", "Quyền hạn", "Tên nhân viên", "Trạng thái");
            for (int i = 0; i < 102; i++) {
                System.out.print("─");
            }
            System.out.println();

            for (Account account : accountList) {
                callSt.setString(1,account.getEmpId());
                ResultSet rs = callSt.executeQuery();
                String nameEmp = null;
                if (rs.next()){
                    nameEmp = rs.getString(1);
                }
                System.out.printf("|%-10s|%-20s|%-20s|%-15s|%-20s|%-10s|%n",
                        account.getAccId(),
                        account.getUserName(),
                        account.getPassWord(),
                        (account.isPermission() ? "User" : "Admin"),
                        nameEmp,
                        (account.isAccStatus() ? "Active" : "Block"));
                for (int i = 0; i < 102; i++) {
                    System.out.print("─");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }finally {
            ConnectionDB.closeConnection(conn);
        }
    }
}
