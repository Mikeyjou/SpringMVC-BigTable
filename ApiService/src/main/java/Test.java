
import com.lavidatec.template.dao.IUsersDao;
import com.lavidatec.template.dao.UsersDaoImpl;
import com.lavidatec.template.entity.TrainsModel;
import com.lavidatec.template.entity.UsersModel;
import com.lavidatec.template.vo.UsersVo;
import java.util.List;
import java.util.Optional;
import com.lavidatec.template.dao.ITrainsDao;
import com.lavidatec.template.dao.TrainsDaoImpl;
import com.lavidatec.template.entity.UserJson;
import com.lavidatec.template.service.TrainServiceImpl;
import com.lavidatec.template.vo.TrainsVo;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mikey
 */
public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello! World!");
        IUsersDao iUsersDao = new UsersDaoImpl();
        ITrainsDao iTrainsDao = new TrainsDaoImpl();
        UsersModel userModel = new UsersModel();
        userModel.setAccount("Test for it.......");
        userModel.setPassword("中文一下uuuudu");
        UsersVo usersVo = new UsersVo();
        usersVo.setAccount("mikey");
        usersVo.setPassword("123");
        Optional<UsersModel> userResult = iUsersDao.usersFind(usersVo);
        if(userResult.isPresent())
            System.out.println(userResult.get());
//        UserJson userJson = new UserJson();
//        Map<String, Boolean> concern = new HashMap<>();
//        concern.put("c67386d5-ad51-43ab-95d4-1c81aef5f7ad", Boolean.FALSE);
//        Map<String, String> nickname = new HashMap<>();
//        nickname.put("19bb5cba-567e-4937-bd20-f9ce8563b3d2", "Sandy");
//        nickname.put("c67386d5-ad51-43ab-95d4-1c81aef5f7ad", "nuck");
//        userJson.setConcern(concern);
//        userJson.setNickname(nickname);
//        userModel.setUserJson(userJson);
//        iUsersDao.usersPersist(Optional.of(userModel));
//        usersVo.setAccount("mikey");
//        usersVo.setPassword("123");
//        Optional<UsersModel> user = iUsersDao.usersFind(usersVo);
//        System.out.println(user.get().getUserJson().getNickname());
//        System.out.println(user.get().getUserJson().getConcern());

//        Optional<List<TrainsModel>> optional;
//        TrainsVo trainVo = new TrainsVo();
//        trainVo.setNo("111");
//        System.out.print(trainService.trainFindList(trainVo));
    }
}
