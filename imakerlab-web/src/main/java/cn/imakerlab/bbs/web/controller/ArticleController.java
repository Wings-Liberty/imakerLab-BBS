package cn.imakerlab.bbs.web.controller;

import cn.imakerlab.bbs.constant.ErrorConstant;
import cn.imakerlab.bbs.constant.FileUploadEnum;
import cn.imakerlab.bbs.model.exception.MyException;
import cn.imakerlab.bbs.model.vo.BackContentVo;
import cn.imakerlab.bbs.model.vo.GetArticlesMsgByTypeVo;
import cn.imakerlab.bbs.model.vo.UserAndArticleAndCommentsVo;
import cn.imakerlab.bbs.security.utils.SecurityUtils;
import cn.imakerlab.bbs.service.Imp.ArticleServiceImp;
import cn.imakerlab.bbs.service.Imp.UserServiceImp;
import cn.imakerlab.bbs.utils.MyUtils;
import cn.imakerlab.bbs.utils.ResultUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ArticleController {
    @Autowired
    ArticleServiceImp articleServiceImp;

    @Autowired
    UserServiceImp userServiceImp;

    @ResponseBody
    @GetMapping("/article/{type}")
    public ResultUtils getArticlesMsgByType(@PathVariable(value = "type", required = true) String type,
                                            @RequestParam(value = "pn", required = true) Integer pn) {

        List<GetArticlesMsgByTypeVo> articlesMsgByType = articleServiceImp.getArticlesMsgByType(type);

        if (articlesMsgByType.size() > 5) {
            articlesMsgByType.subList(0, 5);
        }
        PageHelper.startPage(pn, 5);
        PageInfo<GetArticlesMsgByTypeVo> articlePageInfo = new PageInfo<>(articlesMsgByType);

        return ResultUtils.success(articlePageInfo);
    }


    @ResponseBody
    @GetMapping("/search")
    public ResultUtils searchMsgByKey(@RequestParam("key") String key) throws Exception {
        if (key != null && key != "") {
            List<BackContentVo> backContentVos = articleServiceImp.searchMsgByKey(key);
            if (backContentVos == null || backContentVos.size() == 0) {
                return ResultUtils.failure(100);
            }
            return ResultUtils.success(backContentVos);
        } else {
            throw new MyException(ErrorConstant.Article.SEARCH_CONTENT_NULL);
        }
    }

    @ResponseBody
    @GetMapping("/label")
    public ResultUtils getLabel(HttpServletRequest request) {

        Integer userId = null;

        userId = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        Map<String, List<String>> label = articleServiceImp.getLabel(3);

        return ResultUtils.success(label);
    }

    @ResponseBody
    @GetMapping("/article/msg/{id}")
    public ResultUtils getDetailMsgOfArticleByArticleId(@PathVariable("id") String id) {

        if (Integer.parseInt(id) <= 0 || id == null || id == "") {
            throw new MyException("文章id格式异常");
        }

        UserAndArticleAndCommentsVo detailMsgOfArticleByArticleId = articleServiceImp.getDetailMsgOfArticleByArticleId(Integer.parseInt(id));
        return ResultUtils.success(detailMsgOfArticleByArticleId);
    }

    @ResponseBody
    @DeleteMapping("/article")
    public ResultUtils deleteArticlesByUser(@RequestBody List<Integer> delArticle, HttpServletRequest request) {

        int userId = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        articleServiceImp.deleteArticlesByUser(delArticle, userId);
        return ResultUtils.success();
    }

    @ResponseBody
    @PutMapping("/article")
    public ResultUtils putArticlesByUser(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        Integer userId = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        String articleId = jsonObject.getString("articleId");
        String authorId = jsonObject.getString("authorId");
        String text = jsonObject.getString("text");
        String title = jsonObject.getString("title");
        String label = jsonObject.getString("label");

        if (userId != Integer.parseInt(authorId)) {
            throw new MyException("用户id与当前登录id不符");
        }

        articleServiceImp.putArticlesByUser(authorId, articleId, text, title, label);

        return ResultUtils.success();

    }

    @ResponseBody
    @PostMapping("/article")
    public ResultUtils postArticleByUser(@RequestParam("authorId") String authorId, @RequestParam("label") List<String> label,
                                         @RequestParam("text") String text, @RequestParam("title") String title,
                                         @RequestParam("summary") String summary, MultipartFile file, HttpServletRequest request) {
        Integer userId = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        if (userId != Integer.parseInt(authorId)) {
            throw new MyException("用户id与当前登录id不符");
        }
        String coverUrl = MyUtils.uplode(file, FileUploadEnum.FIGURE);
        log.info("文件路径为" + coverUrl);

        articleServiceImp.postArticleByUser(Integer.parseInt(authorId), label, text, title, summary, coverUrl);

        return ResultUtils.success();
    }

    @ResponseBody
    @PostMapping("/upload")
    public ResultUtils postImage(@RequestParam("articleId") String articleId, MultipartFile file, HttpServletRequest request) {
        int userId = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        String coverUrl = MyUtils.uplode(file, FileUploadEnum.FIGURE);

        articleServiceImp.postImage(userId, Integer.parseInt(articleId), coverUrl);
        return ResultUtils.success();
    }

    @ResponseBody
    @PutMapping("/article/likes")
    public ResultUtils postLikesByUser(@RequestParam("userId") String userId, @RequestParam("articleId") String articleId,
                                       HttpServletRequest request) {
        int userId2 = SecurityUtils.getUserIdFromAuthenticationByRequest(request);

        if (userId2 != Integer.parseInt(userId)) {
            throw new MyException("用户id与当前登录id不符");
        }

        articleServiceImp.postLikesByUser(Integer.parseInt(userId), Integer.parseInt(articleId));

        return ResultUtils.success();

    }

}