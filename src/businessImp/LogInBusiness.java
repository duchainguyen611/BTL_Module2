package businessImp;

import Presentation.Admin.AdminPresentation;
import Presentation.User.UserPresentation;
import bussiness.ConsoleColors;
import entity.Account;
import util.ConnectionDB;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class LogInBusiness implements Serializable{

    public static Account account = new Account();

    public static void accLogIn(Scanner scanner){
        try {
            account = LogInBusiness.readDataFromFile();
            if (account != null){
                if (!account.isPermission()){
                    AdminPresentation.menuAdmin(scanner);
                }else {
                    UserPresentation.menuUser(scanner);
                }
            }else {
                logInAction(scanner);
            }
        } catch (Exception ex) {
            System.err.println("⚠ Warning: File accountLogIn.txt not found!");
        }
    }

    public static void logInAction(Scanner scanner){
        Connection conn = ConnectionDB.openConnection();
        CallableStatement callst = null;
        CallableStatement callst2 = null;
        System.out.println("******************Đăng Nhập****************");
        do {
            System.out.println("Tài khoản:");
            String userName = ExceptionMethod.StringNotEmpty(scanner);
            System.out.println("Mật khẩu:");
            String passWord = ExceptionMethod.StringNotEmpty(scanner);
            try {
                callst = conn.prepareCall("{call log_in_account(?,?,?)}");
                callst.setString(1, userName);
                callst.setString(2, passWord);
                callst.setInt(3, Types.INTEGER);
                callst.execute();
                int checkAcc = callst.getInt(3);
                if (checkAcc > 0) {
                    callst2 = conn.prepareCall("{call get_infor_account(?)}");
                    callst2.setString(1, userName);
                    ResultSet rs = callst2.executeQuery();
                    account = new Account();
                    if (rs.next()) {
                        account.setAccId(rs.getInt("acc_id"));
                        account.setUserName(rs.getString("user_name"));
                        account.setPassWord(rs.getString("password"));
                        account.setPermission(rs.getBoolean("permission"));
                        account.setEmpId(rs.getString("emp_id"));
                        account.setAccStatus(rs.getBoolean("acc_status"));
                    }
                    if (account.isAccStatus()){
                        writeDataToFile(account);
                        if (!account.isPermission()) {
                            ConnectionDB.closeConnection(conn);
                            System.out.println(ConsoleColors.makeColor("Đăng nhập thành công!",ConsoleColors.GREEN));
                            AdminPresentation.menuAdmin(scanner);
                        } else {
                            ConnectionDB.closeConnection(conn);
                            System.out.println(ConsoleColors.makeColor("Đăng nhập thành công!",ConsoleColors.GREEN));
                            UserPresentation.menuUser(scanner);
                        }
                    }else {
                        System.err.println("Tài khoản bị khóa!");
                    }
                } else {
                    System.err.println("Tài khoản hoặc mật khẩu không đúng! Mời nhập lại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } while (true);
    }

    public static void writeDataToFile(Account account) {
        File file = new File("accountLogIn.txt");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(account);
            oos.flush();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static Account readDataFromFile() {
        Account accountRead = null;
        File file = new File("accountLogIn.txt");
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            accountRead = (Account) ois.readObject();
            return accountRead;
        } catch (FileNotFoundException e) {
            accountRead = new Account();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return accountRead;
    }
}
