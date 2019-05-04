package com.lee.mapper;

import com.lee.model.Student;
import com.lee.model.StudentExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentExtendMapper {

    List<Student> getAllStudent();

    List<Integer> selectNowIds();

}