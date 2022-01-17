package codegym.controller;

import codegym.model.ClassRoom;
import codegym.model.Student;
import codegym.service.IClassZoomService;
import codegym.service.IStudentService;
import codegym.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
public class StudentController {
    @Autowired
    IStudentService studentService;

    @Autowired
    IClassZoomService classZoomService;

    @GetMapping("/students")
    public ModelAndView showAll(){
        ModelAndView modelAndView = new ModelAndView("show");
        modelAndView.addObject("students", studentService.findAll());
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreate(){
        ModelAndView modelAndView = new ModelAndView("create");
        modelAndView.addObject("student", new Student());
        modelAndView.addObject("classZooms", classZoomService.findAll());
        return modelAndView;
    }



    @PostMapping("/create")
    public String create(@ModelAttribute(value = "student") Student student, @RequestParam long idClassZoom, @RequestParam MultipartFile upImg){
        ClassRoom classRoom = new ClassRoom();
        classRoom.setId(idClassZoom);
        student.setClassRoom(classRoom);

        String nameFile = upImg.getOriginalFilename();
        try {
            FileCopyUtils.copy(upImg.getBytes(), new File("C:\\Users\\LAM\\Desktop\\Demo_Repository_JPA-master\\src\\main\\webapp\\WEB-INF/" + nameFile));
            student.setImg("/i/img/"+nameFile);
            studentService.save(student);

        } catch (IOException e) {
            student.setImg("/img/abc.jpeg");
            studentService.save(student);
            e.printStackTrace();
        }
        return "redirect:/students";
    }
    @GetMapping("/delete")
    public String delete(@RequestParam long id){
        Student student = studentService.findById(id);
        String studentDelete = student.getImg().replaceAll("/i/view/img/","");
        String file1 = "C:\\Users\\LAM\\Desktop\\Demo_Repository_JPA-master\\src\\main\\webapp\\WEB-INF\\img/" +studentDelete;
        File file = new File(file1);
        if(file.exists()){
            file.delete();
        }
        studentService.delete(id);
        return "redirect:/students";
    }
    @GetMapping("/edit")
    public ModelAndView showEdit(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("edit");
        modelAndView.addObject("students", studentService.findById(id));
        modelAndView.addObject("classZooms", classZoomService.findAll());
        return modelAndView;
    }
    @PostMapping("/edit")
    public String editProduct(@ModelAttribute  Student student,@RequestParam long id, @RequestParam MultipartFile uppImg, @RequestParam long idClassZoom) {
        ClassRoom classRoom = new ClassRoom();
        classRoom.setId(idClassZoom);
        student.setClassRoom(classRoom);
        String imgname = uppImg.getOriginalFilename();
        try {
            FileCopyUtils.copy(uppImg.getBytes(),new File("C:\\Users\\LAM\\Desktop\\Demo_Repository_JPA-master\\src\\main\\webapp\\WEB-INF\\img/" + imgname));
            student.setImg("/i/img/" +imgname);
            studentService.save(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgOld =studentService.findById(id).getImg();
        if(imgOld.equals(student.getImg()) && student.getImg().isEmpty()){
            String imgd = imgOld.replaceAll("/i/img/","");
            String file1 = "C:\\Users\\LAM\\Desktop\\Demo_Repository_JPA-master\\src\\main\\webapp\\WEB-INF\\img/" +imgd;
            File file = new File(file1);
            if(file.exists()){
                file.delete();
            }
        }
        return "redirect:/students";
    }
}
