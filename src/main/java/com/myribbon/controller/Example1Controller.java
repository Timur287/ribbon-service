package com.myribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
public class Example1Controller {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @ResponseBody
    @GetMapping
    public String home() {

        return "<a href='testCallAbcService'>/testCallAbcService</a>";
    }

    @ResponseBody
    @GetMapping("/testCallAbcService")
    public String showFirstService() {

        String serviceId = "SIMPLE-CRUD-APP-EUREKA-CLIENT".toLowerCase();

        // (Need!!) eureka.client.fetchRegistry=true
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

        if (instances == null || instances.isEmpty()) {
            return "No instances for service: " + serviceId;
        }
        String html = "<h2>Instances for Service Id: " + serviceId + "</h2>";

        for (ServiceInstance serviceInstance : instances) {
            html += "<h3>Instance :" + serviceInstance.getUri() + "</h3>";
        }

        // Create a RestTemplate.
        RestTemplate restTemplate = new RestTemplate();

        html += "<br><h4>Call /hello of service: " + serviceId + "</h4>";

        try {
            // May be throw IllegalStateException (No instances available)
            ServiceInstance serviceInstance = this.loadBalancer.choose(serviceId);

            html += "<br>===> Load Balancer choose: " + serviceInstance.getUri();

            String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/students";

            html += "<br>Make a Call: " + url;
            html += "<br>";

           // String result = restTemplate.getForObject(url, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            List<Student> result = restTemplate.exchange(url, HttpMethod.GET,entity,new ParameterizedTypeReference<List<Student>>() {
            }).getBody();
            html += "<br>Result: " + result;
        } catch (IllegalStateException e) {
            html += "<br>loadBalancer.choose ERROR: " + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            html += "<br>Other ERROR: " + e.getMessage();
            e.printStackTrace();
        }
        return html;
    }

}