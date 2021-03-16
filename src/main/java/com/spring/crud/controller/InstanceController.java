package com.spring.crud.controller;

import com.spring.crud.model.Employee;
import com.spring.crud.model.Instance;
import com.spring.crud.model.InstanceUsage;
import com.spring.crud.repository.EmployeeRepository;
import com.spring.crud.repository.InstanceRepository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.*;

import com.spring.crud.repository.InstanceUsageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class InstanceController {

    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private InstanceUsageRepository instanceUsageRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @RequestMapping("/")
    public String getAllInstances(Model model){
        List<Instance> instances = instanceRepository.findAll();
        model.addAttribute("instances",instances);
        Instance instance = new Instance();
        model.addAttribute("instance",instance);
        return "index";

    }

    @RequestMapping("/createInstance")
    public String createNewInstance(Model model){
        Instance instance = new Instance();
        model.addAttribute("instance",instance);
        return "new_instance";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveInstance(@ModelAttribute("instance") Instance instance){
        instanceRepository.saveAndFlush(instance);
        return "redirect:/";
    }
    @RequestMapping(value = "/enter", method = RequestMethod.POST)
    public String saveInstance(@ModelAttribute("instanceUsage") InstanceUsage instanceUsage){
        instanceUsage.setTime(LocalDateTime.now());
        instanceUsageRepository.saveAndFlush(instanceUsage);
        return "redirect:/";
    }

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public String getInstanceByName(@ModelAttribute("instance") Instance instance, Model model){
        List<Instance> instances = instanceRepository.findByName(instance.getInstance_name());
        model.addAttribute("instances",instances);
        return "index";

    }

    @RequestMapping("/getInstanceUsages")
    public String getAllInstanceUsages(Model model){
        List<InstanceUsage> instancesUsages =  instanceUsageRepository.findAllInAscending();
        model.addAttribute("instancesUsages", instancesUsages);
        return "all_instances_usages";
    }

    /*@RequestMapping("/getInstanceUsageById/{id}")
    public String getInstanceUsageById(@PathVariable int id, Model model){
        List<InstanceUsage> instanceUsages = instanceUsageRepository.findAllByInstanceId(id);
        model.addAttribute("instanceUsages",instanceUsages);
        return "instance_usages";
    }*/

    @GetMapping("/getPage")
    public Page<InstanceUsage> getPage(Pageable pageable){
        return instanceUsageRepository.findAll(pageable);
    }

    @RequestMapping (value = "/getInstanceUsageById/{id}")
    public String getInstanceUsageByIdd(@RequestParam(defaultValue = "10") Integer limit, @PathVariable int id, Model model){
        List<InstanceUsage> instanceUsages =  instanceUsageRepository.findAllByInstanceIdLimit(id,limit);
        model.addAttribute("instanceUsages",instanceUsages);
        return "instance_usages";
    }

    @RequestMapping("/deleteInstance/{id}")
    public String deleteInstance(@PathVariable int id){
        instanceRepository.deleteById(id);
        return "redirect:/";
    }

    @RequestMapping("/updateInstanceState/{id}")
    public String updateInstance(@PathVariable int id, Model model){
        Instance existingInstance = instanceRepository.findById(id).orElse(null);
        Instance tempInstance = existingInstance;
        tempInstance.setInstance_state(!existingInstance.isInstance_state());
        BeanUtils.copyProperties(tempInstance,existingInstance);
        instanceRepository.saveAndFlush(existingInstance);
        InstanceUsage instanceUsage = new InstanceUsage();
        model.addAttribute("instanceUsage",instanceUsage);
        return "new_instance_usage";

    }



}
