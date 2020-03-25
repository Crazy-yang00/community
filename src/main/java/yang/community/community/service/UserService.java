package yang.community.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yang.community.community.mapper.UserMapper;
import yang.community.community.model.User;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
         User dbUser =userMapper.findByAccountId(user.getAccountId());
         if (dbUser == null){
             user.setGmtCreate(System.currentTimeMillis());
             user.setGmtModified(user.getGmtCreate() );
             userMapper.insert(user);
         }else{
             dbUser.setGmtModified(System.currentTimeMillis());
             dbUser.setAvatarUrl(user.getAvatarUrl());
             dbUser.setName(user.getName());
             dbUser.setToken(user.getToken());
             userMapper.update(dbUser);

         }
    }
}
