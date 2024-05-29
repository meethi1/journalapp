package com.edigest.journalApp.controller;

import com.edigest.journalApp.entity.JournalEntry;
import com.edigest.journalApp.entity.User;
import com.edigest.journalApp.service.JournalEntryService;
import com.edigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.edigest.journalApp.entity.JournalEntry.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEnteriesOfUser(@PathVariable String userName){
       User user=   userService.findByUserName(userName);
       List<JournalEntry> all=user.getJournalEntries();
       if(all!=null && !all.isEmpty())
       {
           return new ResponseEntity<>(all,HttpStatus.OK);
       }
        return new ResponseEntity<>(all,HttpStatus.NOT_FOUND);
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry,@PathVariable String userName){
       try{

        myEntry.setDate(LocalDateTime.now());

     journalEntryService.saveEntry(myEntry,userName);
      return new ResponseEntity<>(myEntry,HttpStatus.CREATED);}
       catch (Exception e){
         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
    }
    @GetMapping("id/{my_id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId my_id){

Optional<JournalEntry> journalEntry= journalEntryService.findById(my_id);
if(journalEntry.isPresent()){
    return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);

}
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{userName}/{my_id}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId my_id,@PathVariable String userName){
       journalEntryService.deleteById(my_id,userName);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId id,@RequestBody JournalEntry myEntry,@PathVariable String userName){
         JournalEntry old=journalEntryService.findById(id).orElse(null);
         if(old !=null){
             old.setTitle(myEntry.getTitle()!=null&&!myEntry.getTitle().equals("")?myEntry.getTitle():old.getTitle());
             old.setContent(myEntry.getContent()!=null&&!myEntry.getContent().equals("")?myEntry.getContent(): old.getContent());

           journalEntryService.saveEntry(old);
             return new ResponseEntity<>(old,HttpStatus.OK);}
return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
