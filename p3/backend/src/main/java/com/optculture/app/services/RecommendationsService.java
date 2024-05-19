package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.ereceipt.ProductsListResponseDTO;
import com.optculture.app.dto.ereceipt.RecommendationsRequestDTO;
import com.optculture.app.repositories.MbaDepartmentsRepository;
import com.optculture.app.repositories.SkuRepository;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class RecommendationsService {

    Logger logger = LoggerFactory.getLogger(RecommendationsService.class);

    @Autowired
    MbaDepartmentsRepository mbaDepartmentsRepository;

    @Autowired
    SkuRepository skuRepository;

    @Autowired
    GetLoggedInUser getLoggedInUser;
    public List<ProductsListResponseDTO> findTopRecommendations(RecommendationsRequestDTO recommendationsRequestDTO, Long userId) {

        //User user = getLoggedInUser.getLoggedInUser();
        //Long userId = 1207L;
        logger.info("departments "+recommendationsRequestDTO.getDepartments());

        //int minPrice = Collections.min(recommendationsRequestDTO.getPriceList()).intValue();
        //int maxPrice = Collections.max(recommendationsRequestDTO.getPriceList()).intValue();
        //maxPrice = (int)(maxPrice+(0.3*maxPrice));
        List<String> depList = null;
        if(recommendationsRequestDTO.getProductIds()!=null
                && !recommendationsRequestDTO.getProductIds().isEmpty()) {

            List<String> prodList = Arrays.asList(recommendationsRequestDTO.getProductIds().split(","));
            logger.info("product id list "+recommendationsRequestDTO.getProductIds());
            depList = skuRepository.findByUserIdAndUdf12(userId,prodList);
        } else if (recommendationsRequestDTO.getDepartments() != null
                && !recommendationsRequestDTO.getDepartments().isEmpty()) {
            logger.info("department list "+recommendationsRequestDTO.getDepartments());
            depList = Arrays.asList(recommendationsRequestDTO.getDepartments().split(","));
        }

        List<Object[]> listOfDepartments = mbaDepartmentsRepository.findTopScores(depList,userId, PageRequest.of(0,4));
        for(Object[] eachdep : listOfDepartments) {
            logger.info("listOf top 4 Departments "+eachdep[0]);
        }

        //List<String> finalDeptList = new ArrayList<String>();
        List<Object[]> eachSkuList = new ArrayList<>();
        for (Object[] result : listOfDepartments) {
            //finalDeptList.add((String) result[0]);
            String deptStr = (String) result[0];
            Sort sort= Sort.by(Sort.Direction.DESC,"modifiedDate");
            eachSkuList .addAll(skuRepository.findByUserIdAndDepartmentCode(userId,deptStr, PageRequest.of(0,20,sort)));
        }

        //List<Object[]> skuList = skuRepository.findByUserIdAndDepartmentCode(user.getUserId(),finalDeptList);
        logger.info("sku list size "+eachSkuList.size());

        List<Object[]> finalProductList = getDiverseCollection(eachSkuList,8);
        logger.info("final list size "+finalProductList.size());

        List<ProductsListResponseDTO> listResponseDTOS = new ArrayList<>();
        if(finalProductList.size()>=4) {
            for (Object[] result : finalProductList) {
                //if(((String) result[1]).equalsIgnoreCase("STYLE UNION")) continue;
                ProductsListResponseDTO porProductsListResponseDTO = new ProductsListResponseDTO();
                porProductsListResponseDTO.setDescription((String) result[0]);//title
                porProductsListResponseDTO.setUrl((String) result[1]);//URL
                porProductsListResponseDTO.setPrice(result[2].toString());//item price
                porProductsListResponseDTO.setImage_url((String) result[3]);//img URL
                porProductsListResponseDTO.setVariant_code((String) result[4]);//Barcode
                porProductsListResponseDTO.setDepartment((String) result[5]);//department code
                listResponseDTOS.add(porProductsListResponseDTO);
            }
        }
        return listResponseDTOS;
    }

    public List<Object[]> getDiverseCollection(List<Object[]> sortedProducts, int topN) {
        Set<String> classCodes = new HashSet<>();
        List<Object[]> diverseCollection = new ArrayList<>();

        if(sortedProducts == null) return null;

        if (sortedProducts.size() <= topN) {
            return sortedProducts;
        }

        int count = 0;
        final int MAX_ITERATIONS = sortedProducts.size();
        logger.info("MAX_ITERATIONS "+MAX_ITERATIONS);

        while (diverseCollection.size() < topN && count <= MAX_ITERATIONS) {
            for (Object product[] : sortedProducts) {
                if (diverseCollection.size() == topN) {
                    break;
                }

                if(((String) product[1]).equalsIgnoreCase("STYLE UNION")) continue;

                if (product[5]!=null & !classCodes.contains(product[5])) {
                    diverseCollection.add(product);
                    classCodes.add(product[5].toString());
                }
            }

            sortedProducts.removeIf(diverseCollection::contains);
            classCodes.clear();
            count++;
        }

        return diverseCollection;
    }

}
