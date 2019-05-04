package com.lee.service;

import com.lee.model.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    public void getStudentById() {
        Student stu = studentService.getStudentById(1);
        System.out.println(stu);
    }

    @Test
    public void insertStu() {
        Student stu = new Student();
        stu.setName("Alan");
        stu.setGender(0);
        stu.setPassword("xxxx");
        stu.setGrate("grate");
        studentService.insetStu(stu);
        System.out.println(stu.getId());
    }
}