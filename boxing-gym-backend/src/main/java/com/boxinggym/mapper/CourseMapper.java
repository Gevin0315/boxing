package com.boxinggym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boxinggym.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * 课程 Mapper
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据ID批量查询课程（忽略逻辑删除）
     * 用于排课查询时，即使课程被删除也能显示课程名
     *
     * @param ids 课程ID列表
     * @return 课程列表
     */
    @Select("<script>" +
            "SELECT * FROM course WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Course> selectBatchIdsIgnoreDelete(@Param("ids") Collection<Long> ids);
}
