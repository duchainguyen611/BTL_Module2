package Presentation.Login;

import businessImp.LogInBusiness;

//import java.sql.*;
import java.util.Scanner;

public class LogInPresentation {
    public static void logInMenu(Scanner scanner) {
        LogInBusiness.logInAction(scanner);
    }
}
