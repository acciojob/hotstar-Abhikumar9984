package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class  SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        SubscriptionType givenType  = subscriptionEntryDto.getSubscriptionType();

        int n  = subscriptionEntryDto.getNoOfScreensRequired();
        int amountPaid  = 0;

        if("BASIC".equals(givenType)){
            amountPaid = 500+200*n;
        }
        else if("PRO".equals(givenType)){
            amountPaid = 1000+350*n;
        }
        else{
            amountPaid = 800+250*n;
        }

        User user  = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Date newDate  = new Date();

        Subscription newSubscription  = new Subscription();
        newSubscription.setSubscriptionType(givenType);
        newSubscription.setTotalAmountPaid(amountPaid);
        newSubscription.setStartSubscriptionDate(newDate);
        newSubscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        newSubscription.setUser(user);

        user.setSubscription(newSubscription);

        subscriptionRepository.save(newSubscription);


        return amountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user  = userRepository.findById(userId).get();
        Subscription subscription  = user.getSubscription();
        SubscriptionType subscriptionType  = subscription.getSubscriptionType();
        if("ELITE".equals(subscriptionType)){
            throw new Exception("Already the best Subscription");
        }


        int oldAmount  = subscription.getTotalAmountPaid();
        int newAmount  = 0;
        int payAmount  = 0;

        if("BASIC".equals(subscriptionType)){

            newAmount = 800+250*subscription.getNoOfScreensSubscribed();
            payAmount  = newAmount-oldAmount;

            subscription.setSubscriptionType(SubscriptionType.PRO);

        }
        else{
            newAmount = 1000+350*subscription.getNoOfScreensSubscribed();
            payAmount  = newAmount-oldAmount;

            subscription.setSubscriptionType(SubscriptionType.ELITE);

        }

        subscription.setTotalAmountPaid(newAmount);
        user.setSubscription(subscription);

        subscriptionRepository.save(subscription);

        return payAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalRevenue  = 0;

        for(Subscription s : subscriptionList){
            totalRevenue += s.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
