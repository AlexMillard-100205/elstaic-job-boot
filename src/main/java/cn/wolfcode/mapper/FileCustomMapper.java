package cn.wolfcode.mapper;

import cn.wolfcode.domain.FileCustom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FileCustomMapper {



    @Select("select * from t_file_custom where backedUp = 0")
    List<FileCustom> selectAll();
    @Update("update t_file_custom set backedUp = #{state} where id = #{id}")
    int changeState(@Param("id") Long id, @Param("state")int state);

    @Select("select * from t_file_custom where backedUp = 0 and type = #{type}")
    List<FileCustom> selectByType(String type);

    @Select("select * from t_file_custom where backedUp = 0 limit #{count}")
    List<FileCustom> selectLimit(int count);
}
