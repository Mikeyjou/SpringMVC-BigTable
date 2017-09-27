/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lavidatec.template.service;

import com.lavidatec.template.entity.UsersModel;
import java.util.Optional;

/**
 *
 * @author Mikey
 */
public interface IUserService {
    
    void userInsert(final Optional<UsersModel> users)
            throws Exception;
    
    void userUpdate(final Optional<UsersModel> users)
            throws Exception;
            
    Optional<UsersModel> userFind(final Optional<UsersModel> users)
            throws Exception;
    
    void userRemove(final Optional<UsersModel> users)
            throws Exception;
    
    String userLogin(final Optional<UsersModel> users)
            throws Exception;
    
    void userLogout(final Optional<UsersModel> users)
            throws Exception;
    
    String userBook(final Optional<UsersModel> users,String no)
            throws Exception;
    
//    String userCancelOrder(final Optional<UsersModel> users,String token)
//            throws Exception;
}
