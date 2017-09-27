/**
 * UPDUser_Service
 * Author : Sian
 * UPD User method
 */
package com.lavidatec.template.service;

import com.lavidatec.template.dao.ITrainsDao;
import com.lavidatec.template.dao.IUsersDao;
import com.lavidatec.template.dao.TrainsDaoImpl;
import com.lavidatec.template.dao.UsersDaoImpl;
import com.lavidatec.template.entity.TrainsModel;
import com.lavidatec.template.entity.UsersModel;
import com.lavidatec.template.pojo.PasswordSHA3;
import com.lavidatec.template.vo.TrainsVo;
import com.lavidatec.template.vo.UsersVo;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 * 2017/03/15 - Add Users_Model.
 */
@Lazy
@Repository("IUserService")
public class UserServiceImpl implements IUserService{

    /**
     * log init.
     */
    static final Logger LOGGER
            = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private IUsersDao iUsersDao = new UsersDaoImpl();
//    private PasswordSHA3 sha = new PasswordSHA3();
    /**
     * Users Persist.
     *
     * @param users User Data
     * @throws Exception Exception
     */
    @Override
    public final void userInsert(final Optional<UsersModel> users)
            throws Exception {
        LOGGER.info("userInsertStart");
        
//        users.get().setPassword(sha.createHash(users.get().getPassword()));
        iUsersDao.usersInsert(users);
        LOGGER.info("userInsertEnd");
    }
    
    @Override
    public final void userUpdate(final Optional<UsersModel> users)
            throws Exception {
        LOGGER.info("userMergeStart");
        iUsersDao.usersUpdate(users);
        LOGGER.info("userMergeEnd");
    }
    
    @Override
    public final Optional<UsersModel> userFind(final Optional<UsersModel> users)
            throws Exception {
        LOGGER.info("userFindStart");
        UsersVo usersVo = new UsersVo();
        usersVo.setAccount(users.get().getAccount());
        if(StringUtils.isNotBlank(users.get().getPassword()))
//            usersVo.setPassword(sha.createHash(users.get().getPassword()));
            usersVo.setPassword(users.get().getPassword());
        if(StringUtils.isNotBlank(users.get().getIdentifier()))
            usersVo.setIdentifier(users.get().getIdentifier());
        
        if(users.get().getOptimisticLock() > 0){
            System.out.println("User Service optLock" + users.get().getOptimisticLock());
            usersVo.setOptimisticLock(users.get().getOptimisticLock());
        }
        LOGGER.info("userFindEnd");
        return iUsersDao.usersFind(usersVo);
    }
    
    @Override
    public final void userRemove(final Optional<UsersModel> users)
            throws Exception {
        LOGGER.info("userRemoveStart");
        UsersVo usersVo = new UsersVo();
        usersVo.setAccount(users.get().getAccount());
//        usersVo.setPassword(sha.createHash(users.get().getPassword()));
        usersVo.setPassword(users.get().getPassword());
        Optional<UsersModel> user = iUsersDao.usersFind(usersVo);
        //判斷使用者帳密
        if(user.isPresent()){
            iUsersDao.usersRemove(user);
        }
        LOGGER.info("userRemoveEnd");
    }
    
    //使用者登入
    @Override
    public final String userLogin(final Optional<UsersModel> users)
            throws Exception {
        String token = "";
        //判斷是否資料正確
        if(users.isPresent()){
            token = UUID.randomUUID().toString().replace("-", "");
            users.get().setIdentifier(token);
        }
        iUsersDao.usersUpdate(users);
        return token;
    }
    
    //使用者登出
    @Override
    public final void userLogout(final Optional<UsersModel> users)
            throws Exception {
        UsersVo usersVo = new UsersVo();
        usersVo.setAccount(users.get().getAccount());
//        usersVo.setPassword(sha.createHash(users.get().getPassword()));
        usersVo.setPassword(users.get().getPassword());
        Optional<UsersModel> user = iUsersDao.usersFind(usersVo);
        if(user.isPresent()){
            user.get().setIdentifier("");
            iUsersDao.usersUpdate(user);
        }
    }
    
    //使用者訂票
    @Override
    public final String userBook(final Optional<UsersModel> users,String no)
            throws Exception {
        String token = "";
//        UsersVo usersVo = new UsersVo();
//        PasswordSHA3 sha = new PasswordSHA3();
//        usersVo.setAccount(users.get().getAccount());
//        usersVo.setPassword(sha.createHash(users.get().getPassword()));
//        usersVo.setIdentifier(users.get().getIdentifier());
//        usersVo.setOptimisticLock(users.get().getOptimisticLock());
//        //尋找該識別碼以及帳號密碼之使用者
//        Optional<UsersModel> user = iUsersDao.usersFind(usersVo);
        
        TrainsVo trainsVo = new TrainsVo();
        trainsVo.setNo(no);
        ITrainsDao iTrainsDao = new TrainsDaoImpl();
        //尋找該車
        Optional<TrainsModel> train = iTrainsDao.trainsFind(trainsVo);
        //樂觀鎖 在根據version尋找一次
        trainsVo.setOptimisticLock(train.get().getOptimisticLock());
        train = iTrainsDao.trainsFind(trainsVo);
        
        //計算該天訂票票數
        int dayCount = 1;
        if(users.isPresent() && train.isPresent()){
            String orderList = users.get().getOrderList();
            //確保清單非空值
            if(StringUtils.isNotBlank(orderList)){
                String[] eachOrder = orderList.split(",");
                for(String order : eachOrder){
                    String[] orderInfo = order.split("_");
                    if(orderInfo[0].equals(train.get().getDate()))
                        dayCount += 1;
                }
            }
            LOGGER.info("DayCount" + Integer.toString(dayCount));
            //限制使用者該天只能訂4張票
            if(dayCount < 5){
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd");
                token = UUID.randomUUID().toString().replace("-", "");
                train.get().setTicketsLimit(train.get().getTicketsLimit() - 1);

                String order = train.get().getDate() + "_" + train.get().getNo() + "_" + token;
                if(StringUtils.isNotBlank(users.get().getOrderList()))
                    users.get().setOrderList(users.get().getOrderList() + "," + order);   
                else
                    users.get().setOrderList(order);
            }

            if(train.get().getTicketsLimit() >= 0){
                if(iTrainsDao.trainsUpdate(train) == 1)
                    iUsersDao.usersUpdate(users);
                else
                    token = "";
            }else{
                token = "";
            }
        }

        return token;
    }
//    
//    @Override
//    public final String userCancelOrder(final Optional<UsersModel> users,String token)
//            throws Exception {
//        UsersVo usersVo = new UsersVo();
//        usersVo.setAccount(users.get().getAccount());
////        usersVo.setPassword(sha.createHash(users.get().getPassword()));
//        usersVo.setPassword(users.get().getPassword());
//        usersVo.setOrderToken(token);
//        Optional<UsersModel> user = iUsersDao.usersFind(usersVo);
//        String orderList = "";
//        String[] eachOrder = new String[0];
//        //確認使用者存在
//        if(user.isPresent()){
//            orderList = user.get().getOrderList();
//            eachOrder = orderList.split(",");
//        }
//        //每筆訂單計算
//        for(String order : eachOrder){
//            System.out.println(order);
//            String[] orderInfo = order.split("_");
//            if(orderInfo.length == 3){
//                if(orderInfo[2].equals(token)){
//                    TrainsVo trainsVo = new TrainsVo();
//                    trainsVo.setNo(orderInfo[1]);
//                    ITrainsDao iTrainsDao = new TrainsDaoImpl();
//                    Optional<TrainsModel> train = iTrainsDao.trainsFind(trainsVo);
//                    train.get().setTicketsLimit(train.get().getTicketsLimit() + 1);
//                    iTrainsDao.trainsMerge(train);
//                    String newOrderList = orderList.replace(order + ",", "");
//                    newOrderList = newOrderList.replace(order, "");
//                    user.get().setOrderList(newOrderList);
//                }
//            }
//        }
//            
//        iUsersDao.usersMerge(user);
//        return token;
//    }
}