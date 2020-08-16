package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Major;
import com.xiaoshu.entity.MajorVO;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Student;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.StudentService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Controller
@RequestMapping("student")
public class StudentController {

	@Autowired
	StudentService studentService;
	@Autowired
	private OperationService operationService;
	
	@Autowired
	JedisPool jedisPool;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	ActiveMQQueue itemcat_queue;
	
	@RequestMapping("studentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		
		// 第一次，去哪？数据库查询专业，redis中只要有专业，走redis，之后，该走redis
		Jedis jedis = jedisPool.getResource();
		String majors = jedis.get("majors");
		if(majors==null){
			// 当redis中没有majors，需要先去MySQL获取，并存入到redis中
			// 专业
			List<Major> majorList =  studentService.findMajor();
			request.setAttribute("roleList", majorList);
			// 存在redis数据库中  讲集合对象，转变成json字符串
			String jsonString = JSON.toJSONString(majorList);
			System.out.println("走MYSQL查询专业");
			jedis.set("majors", jsonString);
		}else{
			System.out.println("走redis查询专业");
			// 讲json数据，转变为List集合
			List<Major> majorList = JSON.parseArray(majors, Major.class);
			request.setAttribute("roleList", majorList);
		}
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		return "student";
	}
	
	@RequestMapping(value="studentList",method=RequestMethod.POST)
	public void studentList(Student student,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Student> userList= studentService.findStudentPage(student,pageNum,pageSize);
			

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
//			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	// 新增或修改
	@RequestMapping("reserveStudent")
	public void reserveStudent(HttpServletRequest request,Student student,HttpServletResponse response){
		Integer sid = student.getSid();
		String hobby = student.getHobby();
		if(hobby==null){
			student.setHobby("");
		}
		
		JSONObject result=new JSONObject();
		try {
			if (sid != null) {   // userId不为空 说明是修改
				if(studentService.existStudentWithSname(student.getSname())==null){  // 没有重复可以添加
					studentService.updateStudent(student);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
				
			}else {   // 添加
				// 根据姓名进行验证，是否存在！
				if(studentService.existStudentWithSname(student.getSname())==null){  // 没有重复可以添加
					studentService.addStudent(student);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("deleteStudent")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				studentService.deleteStudent(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("outStudent")
	public void outStudent(HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			// 导出的代码
			// 准备需要导出的数据
			List<Student> list = studentService.findAll();
			
			// WorkBook 工作簿
			HSSFWorkbook wb = new HSSFWorkbook();
			// sheet表对象
			HSSFSheet sheet = wb.createSheet();
			// 设置一个标题 行 row
			HSSFRow row0 = sheet.createRow(0);
			String[]title = {"学生编号","学生姓名","性别","爱好","生日","专业"};
			for (int i = 0; i < title.length; i++) {
				row0.createCell(i).setCellValue(title[i]);
			}
			
			for (int i = 0; i < list.size(); i++) {
				HSSFRow row = sheet.createRow(i+1);
				Student student = list.get(i);
				row.createCell(0).setCellValue(student.getSid());
				row.createCell(1).setCellValue(student.getSname());
				row.createCell(2).setCellValue(student.getSex());
				row.createCell(3).setCellValue(student.getHobby());
				
				// 将Date类型转换成String
//				SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd");
//				String format = simpleDateFormat.format(student.getBirthday());
				
				row.createCell(4).setCellValue(TimeUtil.formatTime(student.getBirthday(), "yyyy-MM-dd"));
				row.createCell(5).setCellValue(student.getMajor().getManame());
			}
			
			OutputStream out = new FileOutputStream(new File("F://ssm-h1903b.xls"));
			wb.write(out);
			out.close();
			wb.close();
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("inStudent")
	public void inStudent(HttpServletResponse response,MultipartFile file){
		JSONObject result=new JSONObject();
		try {
			// 读取上传的文件，并生成对应的WorkBook对象
			HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
			
			// 获取期中sheet
			HSSFSheet sheet = wb.getSheetAt(0);
			// 获取最后一行的下标
			int lastRowNum = sheet.getLastRowNum();
			
			for (int i = 1; i < lastRowNum+1; i++) {
				HSSFRow row = sheet.getRow(i);
				
				Student s = new Student();
				
				String sname = row.getCell(1).getStringCellValue();
				s.setSname(sname);
				
				String sex = row.getCell(2).getStringCellValue();
				s.setSex(sex);
				
				String hobby = row.getCell(3).getStringCellValue();
				s.setHobby(hobby);
				
				String time = row.getCell(4).getStringCellValue();
				s.setBirthday(TimeUtil.ParseTime(time, "yyyy-MM-dd"));
				
				String maname = row.getCell(5).getStringCellValue();
				// 去数据库中，根据名字，查id
				Integer maid = studentService.findID(maname);
				s.setMaid(maid);
				
				
				// 放入数据库
				studentService.addStudent(s);
				
			}
			
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	/**
	 * @param major
	 */
	@RequestMapping("addMajor")
	public void addMajor(final Major major,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			studentService.addMajor(major);
			
			// 删除redis中的专业，重写从mysql在获取一边新的数据
			Jedis jedis = jedisPool.getResource();
			jedis.del("majors");
			
			// 1.队列的名称  2.消息生成工具
			jmsTemplate.send(itemcat_queue, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(""+major.getMaid());
				}
			});
			
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("zzStudent")
	public void zzStudent(HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			// 统计需要的数据
			List<MajorVO> list = studentService.findMajorVO();
			
			result.put("list", list);
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
}
