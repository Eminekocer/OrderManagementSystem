package bussiness;

import core.Helper;
import dao.UserDao;
import entity.User;

public class UserController {
    private final UserDao userDao = new UserDao();

    public User findByLogin(String mail,String password){
        if(!Helper.isEmailValid(mail)) return null;
        return this.userDao.findByLogin(mail,password);
    }
}
