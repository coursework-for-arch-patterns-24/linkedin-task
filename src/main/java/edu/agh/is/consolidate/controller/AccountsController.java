package edu.agh.is.consolidate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller("/accounts")

public class AccountsController {

    @GetMapping("/{id}")
    public ResponseEntity<String>  x(@PathVariable("id") String  id){
        if(id == null){
            return ResponseEntity.badRequest().body("id is missing");
        }
        return ResponseEntity.ok("success");
    }
}
