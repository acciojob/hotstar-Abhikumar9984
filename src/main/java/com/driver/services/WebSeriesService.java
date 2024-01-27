package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        List<WebSeries> webSeriesList  = webSeriesRepository.findAll();
        Optional<WebSeries> optionalWebSeries  = Optional.ofNullable(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()));

        if(optionalWebSeries.isPresent()){
            throw new Exception("Series is already present");
        }
        WebSeries  webSeries  = optionalWebSeries.get();


        ProductionHouse productionHouse  = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        List<WebSeries> productionWebSeries   = productionHouse.getWebSeriesList();
        int count  =  productionWebSeries.size();
        double sum  = productionHouse.getRatings()*count;
        sum += webSeries.getRating();
        double newRating  = sum/(count+1);
        productionHouse.setRatings(newRating);
        productionWebSeries.add(webSeries);
        productionHouseRepository.save(productionHouse);

        webSeries  = webSeriesRepository.save(webSeries);

        return webSeries.getId();
    }

}
