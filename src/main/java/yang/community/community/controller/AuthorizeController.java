package yang.community.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yang.community.community.dto.AccessTokenDTO;
import yang.community.community.dto.GithubUser;
import yang.community.community.mapper.UserMapper;
import yang.community.community.model.User;
import yang.community.community.provider.GithubProdiver;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProdiver githubProdiver;

    @Value("${github.client.id}")
    private String clientID;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name= "code") String code,
                           @RequestParam(name = "state") String state,
                            HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientID);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProdiver.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProdiver.getUser(accessToken);
        System.out.println(githubUser.getName());
        if (githubUser != null){
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate() );
            userMapper.insert(user);
            //登陆成功，写session和cookie
            request.getSession().setAttribute("user", githubUser);
            return "redirect:/";

        }else {
            //登录失败，重新登录。
            return "redirect:/";
        }
    }
}
