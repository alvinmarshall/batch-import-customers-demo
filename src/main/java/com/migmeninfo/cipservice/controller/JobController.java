//package com.migmeninfo.cipservice.controller;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("jobs")
//public class JobController {
//    @Autowired
//    private JobLauncher jobLauncher;
//    @Autowired
//    private Job runJob;
//
//    @Autowired
//    private Job runDownloader;
//
//    @GetMapping
//    public void init() {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
//        try {
//            jobLauncher.run(runJob, jobParameters);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @GetMapping("zip")
//    public void downloader() {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("startAt", System.currentTimeMillis())
//                .addString("batch_url", "http://localhost:9000/data")
//                .toJobParameters();
//        try {
//            jobLauncher.run(runDownloader, jobParameters);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
