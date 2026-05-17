package com.aftersale.service;

import com.aftersale.entity.UserInfo;
import com.aftersale.enums.UserLevel;
import com.aftersale.store.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @PostConstruct
    public void initUsers() {
        UserInfo user1 = new UserInfo();
        user1.setUserId("U001");
        user1.setUserName("张三");
        user1.setLevel(UserLevel.NORMAL);
        user1.setBalance(new BigDecimal("100"));
        DataStore.users.put(user1.getUserId(), user1);

        UserInfo user2 = new UserInfo();
        user2.setUserId("U002");
        user2.setUserName("李四");
        user2.setLevel(UserLevel.GOLD);
        user2.setBalance(new BigDecimal("500"));
        DataStore.users.put(user2.getUserId(), user2);

        UserInfo user3 = new UserInfo();
        user3.setUserId("U003");
        user3.setUserName("王五");
        user3.setLevel(UserLevel.DIAMOND);
        user3.setBalance(new BigDecimal("1000"));
        DataStore.users.put(user3.getUserId(), user3);
    }

    public UserInfo getUserById(String userId) {
        return DataStore.users.get(userId);
    }

    public List<UserInfo> getAllUsers() {
        return new ArrayList<>(DataStore.users.values());
    }

    public void updateUserBalance(String userId, BigDecimal amount) {
        UserInfo user = DataStore.users.get(userId);
        if (user != null) {
            user.setBalance(user.getBalance().add(amount));
        }
    }
}
