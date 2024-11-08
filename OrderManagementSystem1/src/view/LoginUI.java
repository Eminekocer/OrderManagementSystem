package view;

import bussiness.UserController;
import core.Helper;
import entity.User;

import javax.swing.*;
import java.awt.*;

public class LoginUI  extends JFrame{
    private JPanel container;
    private JPanel pnl_top;
    private JLabel labeltitle;
    private JPanel pnl_bottom;
    private JTextField texteposta;
    private JPasswordField passwordField1;
    private JButton girisButton;
    private JLabel parola;
    private JLabel mail;
    private UserController userController;

    public LoginUI(){
        this.userController= new UserController();
        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(400,400);

        int x= (Toolkit.getDefaultToolkit().getScreenSize().width-this.getSize().width)/2;
        int y =(Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height)/2;

        this.setLocation(x,y);
        this.setVisible(true);
        this.girisButton.addActionListener(e -> {
            JTextField[] checkList = {this.passwordField1,this.texteposta};
            if(!Helper.isEmailValid(this.texteposta.getText())){
                Helper.showMsg("Geçeri bir eposta giriniz !");
            }
            else if(Helper.isFieldListEmpty(checkList)){
                Helper.showMsg("fill");
            }
            else {
                User user = this.userController.findByLogin(this.texteposta.getText(), this.passwordField1.getText());
                if (user == null) {
                    Helper.showMsg("Girdiğiniz bilgilere göre kullanıcı bulunamadı!");
                } else {
                    this.dispose();
                    DashboardUI dashboardUI = new DashboardUI(user);
                }
            }
        });
    }
}
