package darts.ng.io.usersMicroservice.send_email_to_confirm_login.service;


import darts.ng.io.usersMicroservice.send_email_to_confirm_login.model.EmailToConfirmPasswordReq;
import darts.ng.io.usersMicroservice.send_email_to_confirm_login.repository.EmailToConfirmPasswordRepo;
import darts.ng.io.usersMicroservice.util.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmailToConfirmPasswordImpl {

    private EmailToConfirmPasswordRepo repository;
    private EmailValidator emailValidator;

    public EmailToConfirmPasswordImpl(EmailToConfirmPasswordRepo repository, EmailValidator emailValidator){
        this.repository = repository;
        this.emailValidator = emailValidator;
    }

    public ResponseEntity<?> sendMail(EmailToConfirmPasswordReq request) {


        return null;
    }


}
