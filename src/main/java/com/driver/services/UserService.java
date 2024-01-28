package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user  = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList  = webSeriesRepository.findAll();
        User user  = userRepository.findById(userId).get();
        int age  = user.getAge();
        int count  = 0;

        Subscription userSubscription = user.getSubscription();
        SubscriptionType userSubscriptionType  = userSubscription.getSubscriptionType();

        for(WebSeries w : webSeriesList) {
            int ageLimit = w.getAgeLimit();
            SubscriptionType subscriptionType  = w.getSubscriptionType();

            if("ELITE".equals(userSubscriptionType)) {
                if(age<=ageLimit)
                    count++;
            }
            else if("PRO".equals(userSubscriptionType)) {
                if ("PRO".equals(subscriptionType) || "BASIC".equals(subscriptionType)) {
                    if (age <= ageLimit)
                        count++;
                 }
              }
            else{
                if("BASIC".equals(subscriptionType)){
                    if(age<=ageLimit)
                        count++;
                }
            }
          }


        return count;
    }


}
