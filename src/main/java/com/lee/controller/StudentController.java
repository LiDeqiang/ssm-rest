package com.lee.controller;

import com.lee.model.Student;
import com.lee.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/student")
@Api(value = "学生管理", description = "学生管理")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询注册学生数量")
    public Integer getCount() {
        Integer studentCount = (Integer) redisTemplate.opsForValue().get("student:count");
        if (Objects.nonNull(studentCount)) {
            System.out.println(" student count get from cache ... ");
        } else {
            System.out.println(" find student count from db ... ");
            studentCount = studentService.selectStudentCount();
            redisTemplate.opsForValue().set("student:count", studentCount, 1, TimeUnit.HOURS);
        }
        return studentCount;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "查询接口")
    public List<Student> getAllStudents() {
        return studentService.getAllStudent();
    }

    @RequestMapping(value = "/{stid}", method = RequestMethod.GET)
    @ApiOperation(value = "查询接口")
    public Student getStudentById(@PathVariable Integer stid) {
        return studentService.getStudentById(stid);
    }

    @RequestMapping(value = "/{stid}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除接口")
    public Result delStuById(@PathVariable Integer stid) {
        int r = studentService.delStuById(stid);
        if (r == 0)
            return new Result(403, "删除失败");
        return new Result(200, "删除操作成功");
    }

    @RequestMapping(value = "/addStu", method = RequestMethod.POST)
    @ApiOperation(value = "添加学生信息")
    public Result addAStu(Student student) {
        int result = studentService.insetStu(student);
        if (result == 0)
            return new Result(403, "添加失败");
        return new Result(200, "添加成功");
    }

    @RequestMapping(value = "/updateStu", method = RequestMethod.PUT)
    @ApiOperation(value = "更新学生信息")
    public Result updateStu(Student student) {
        System.out.println(student);
        int result = studentService.updateStu(student);
        if (result == 0)
            return new Result(403, "更新失败");
        return new Result(200, "更新成功");
    }

}
