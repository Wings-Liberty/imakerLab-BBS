package cn.imakerlab.bbs.mapper;

import cn.imakerlab.bbs.model.dto.Comment;
import cn.imakerlab.bbs.model.dto.CommentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommentDao {
    long countByExample(CommentExample example);

    int deleteByExample(CommentExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    List<Comment> selectByExample(CommentExample example);

    Comment selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Comment record, @Param("example") CommentExample example);

    int updateByExample(@Param("record") Comment record, @Param("example") CommentExample example);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);
}