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
        Subscription newSubscription  = new Subscription();
        newSubscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        newSubscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        newSubscription.setStartSubscriptionDate(new Date());
        SubscriptionType givenType  = subscriptionEntryDto.getSubscriptionType();
        if("BASIC".equals(givenType)){
            newSubscription.setTotalAmountPaid(500+200*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if("PRO".equals(givenType)){
            newSubscription.setTotalAmountPaid(800+250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else{
            newSubscription.setTotalAmountPaid(1000+350*subscriptionEntryDto.getNoOfScreensRequired());
        }

        User user  = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(newSubscription);
        userRepository.save(user);

        newSubscription.setUser(user);
        newSubscription = subscriptionRepository.save(newSubscription);

        return newSubscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user  = userRepository.findById(userId).get();
        Subscription subscription  = user.getSubscription();
        SubscriptionType subscriptionType  = subscription.getSubscriptionType();
        int oldAmount  = subscription.getTotalAmountPaid();
        int newAmount  = 0;
        int payAmount  = 0;

        if("ELITE".equals(subscriptionType)){
            throw new Exception("Already the best Subscription");
        }
        else if("BASIC".equals(subscriptionType)){

            newAmount = 800+250*subscription.getNoOfScreensSubscribed();
            payAmount  = newAmount-oldAmount;

            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(newAmount);
            user.setSubscription(subscription);

            userRepository.save(user);
            subscriptionRepository.save(subscription);
        }
        else{
            newAmount = 1000+350*subscription.getNoOfScreensSubscribed();
            payAmount  = newAmount-oldAmount;

            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(newAmount);
            user.setSubscription(subscription);

            userRepository.save(user);
            subscriptionRepository.save(subscription);
        }

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
