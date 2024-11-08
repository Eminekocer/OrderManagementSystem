import bussiness.UserController;
import core.Helper;
import entity.User;
import view.DashboardUI;
import view.LoginUI;

public class Main {
    public static void main(String[] args) {
        Helper.setTheme();
        LoginUI loginUI = new LoginUI();
        /*UserController userController = new UserController();
        User user =userController.findByLogin("shemine221@gmail.com","234432");
        DashboardUI dashboardUI = new DashboardUI(user);
        */
    }
}