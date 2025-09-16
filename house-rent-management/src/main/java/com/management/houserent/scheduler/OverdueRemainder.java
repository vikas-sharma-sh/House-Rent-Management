package com.management.houserent.scheduler;

import com.management.houserent.model.Bill;
import com.management.houserent.repository.BillRepository;
import com.management.houserent.service.MailService;
import com.sun.jdi.event.ExceptionEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueRemainder {

    private final BillRepository billRepository ;
    private final MailService mailService ;


    public OverdueRemainder(BillRepository billRepository, MailService mailService) {
        this.billRepository = billRepository;
        this.mailService = mailService;
    }



    @Scheduled(cron = "${scheduler.overdue.cron:0 0 9 * * * }")
    public void sendOverdueRemainders(){
        LocalDate today = LocalDate.now();
        List<Bill> overdue = billRepository.findByDueDateBeforeAndStatus(today, Bill.BillStatus.PENDING);{

            for(Bill bill : overdue){
                try{
                    String to = bill.getLease().getTenant().getEmail();
                    String subject = "Overdue Reminder" ;
                    String body ="<p>Dear"+bill.getLease().getTenant().getName()+" ,</p>"+
                            "<p>Your bill #" + bill.getId() +
                            " was due on " + bill.getDueDate() +
                            ". Amount due: ₹" + bill.getAmount() + ".</p>" +
                            "<p>Please pay as soon as possible to avoid penalties.</p>";

                    mailService.sendMail(to,subject,body,null);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}
