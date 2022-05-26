package com.warrenverr.ppick.service;

import com.warrenverr.ppick.DataNotFoundException;
import com.warrenverr.ppick.dto.ProjectApplyDto;
import com.warrenverr.ppick.dto.ProjectDto;
import com.warrenverr.ppick.dto.RecruitDto;
import com.warrenverr.ppick.dto.UserDto;
import com.warrenverr.ppick.form.UserCreateForm;
import com.warrenverr.ppick.model.Project;
import com.warrenverr.ppick.model.ProjectApply;
import com.warrenverr.ppick.model.Recruit;
import com.warrenverr.ppick.model.User;
import com.warrenverr.ppick.repository.ProjectApplyRepository;
import com.warrenverr.ppick.repository.ProjectRepository;
import com.warrenverr.ppick.repository.RecruitRepository;
import com.warrenverr.ppick.repository.UserRepository;
import jdk.nashorn.internal.objects.NativeUint8Array;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectApplyRepository projectApplyRepository;
    private final RecruitRepository recruitRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    private UserDto of(User user) { return this.modelMapper.map(user, UserDto.class); }
    private User of(UserDto userDto) { return this.modelMapper.map(userDto, User.class); }
    private UserDto of(UserCreateForm userCreateForm) { return this.modelMapper.map(userCreateForm, UserDto.class); }
    private ProjectApplyDto of(ProjectApply projectApply) { return this.modelMapper.map(projectApply, ProjectApplyDto.class); }
    private ProjectApply of(ProjectApplyDto projectApplyDto) { return this.modelMapper.map(projectApplyDto, ProjectApply.class); }
    private Recruit of(RecruitDto recruitDto) { return modelMapper.map(recruitDto, Recruit.class); }
    private RecruitDto of(Recruit recruit) { return modelMapper.map(recruit, RecruitDto.class); }
    private Project of(ProjectDto projectDto) { return modelMapper.map(projectDto, Project.class); }
    private ProjectDto of(Project project) { return modelMapper.map(project, ProjectDto.class); }
    public UserDto signup(UserCreateForm userCreateForm) {
        UserDto userDto = new UserDto();
        userDto=of(userCreateForm);
        User user = of(userDto);
        this.userRepository.save(user);

        return userDto;
    }

    public UserDto loginBySnsid(String snsid) {
        Optional<User> user = this.userRepository.findBySnsid(snsid);
        if(user.isPresent()) {
            return of(user.get());
        }else {
            throw new DataNotFoundException("project not found");
        }
    }

    public UserDto getUser(String snsid) {
        Optional<User> user = this.userRepository.findBySnsid(snsid);
        if(user.isPresent()) {
            return of(user.get());
        }else {
            throw new DataNotFoundException("user not found");
        }

    }

    public Specification<User> search(String keyword) {
        return new Specification<User>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("nickname"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("category"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("detail_category"), "%" + keyword + "%")
                        );
            }
        };
    }

    public Page<UserDto> getUserList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("date"));
        Pageable pageable = PageRequest.of(page, 8, Sort.by(sorts));
        Specification<User> specification = search(keyword);
        Page<User> userList = this.userRepository.findAll(specification, pageable);
        Page<UserDto> userDtoList = userList.map(user -> of(user));
        return userDtoList;
    }

    //내가 신청한 프로젝트
    public List<ProjectDto> appliedProjectList(String sns_id) {
        Optional<User> user = this.userRepository.findBySnsid(sns_id);
        UserDto userDto = of(user.get());
        List<ProjectDto> projectDtoList = new ArrayList<ProjectDto>();
        List<ProjectApply> projectApply = this.projectApplyRepository.findAllByUser_Id(userDto.getId());

        for(ProjectApply p : projectApply) {
            projectDtoList.add(of(this.projectRepository.findByApplyList_Id(p.getId()).get()));
        }
        return projectDtoList;
    }

    //진행중인 프로젝트
    public List<ProjectDto> progressProjectList(String sns_id) {
        Optional<User> user = this.userRepository.findBySnsid(sns_id);
        UserDto userDto = of(user.get());
        List<ProjectDto> projectDtoList = new ArrayList<ProjectDto>();
        List<Project> progressProjectList = this.projectRepository.findAllByProjectMember_Id(userDto.getId());

        for(Project project : progressProjectList) {
            projectDtoList.add(of(project));
        }
        return projectDtoList;
    }

    public String storeImage(MultipartFile file) {


        return "";
    }

    /*public UserDto loginByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isPresent()) {
            return of(user.get());
        }else {
            throw new DataNotFoundException("project not found");
        }
    }
*/
    public UserDto modify(UserDto userDto, UserCreateForm userCreateForm) {
        userDto.setSkill(userCreateForm.getSkill());
        userDto.setJob(userCreateForm.getJob());
        userDto.setCategory(userCreateForm.getCategory());
        userDto.setDetail_category(userCreateForm.getDetail_category());
        userDto.setAgree(userCreateForm.getAgree());
        User user = of(userDto);
        this.userRepository.save(user);
        return userDto;
    }

    public void delete(UserDto userDto) { this.userRepository.delete(of(userDto));}

    private ProjectApplyDto getProjectApplyDto(Integer id) {
        return of(this.projectApplyRepository.findById(id).get());
    }


    public void approve(ProjectDto projectDto, Integer id) {
        ProjectApplyDto projectApplyDto = getProjectApplyDto(id);
        projectApplyDto.setStatus(1);
        ProjectApply projectApply = this.projectApplyRepository.save(of(projectApplyDto));

        List<UserDto> projectMember = projectDto.getProjectMember();
        projectMember.add(of(projectApply).getUserDto());
        projectDto.setProjectMember(projectMember);

        RecruitDto recruitDto = projectDto.getRecruit();
        for(int i=0; i<recruitDto.getSubTask().size(); i++) {
            if(recruitDto.getSubTask().get(i).equals(projectApplyDto.getField())) {
                Integer recruitment = recruitDto.getRecruitment().get(i);
                recruitDto.getRecruitment().set(i,recruitment-1);
            }
        }
        Recruit recruit = this.recruitRepository.save(of(recruitDto));

        projectDto.setRecruit(of(recruit));

        this.projectRepository.save(of(projectDto));


    }



}
