package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import lk.jiat.calivon.entity.*;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class ContentService {
    public String loadDeliveryTypes(){
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<DeliveryTypes> deliveryTypesList = hibernateSession.createQuery("FROM DeliveryTypes dt", DeliveryTypes.class).getResultList();

        responseObject.add("deliveryTypes", AppUtil.gson.toJsonTree(deliveryTypesList));

        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadProvinceDetails() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Province> provinceList = hibernateSession.createQuery("FROM Province p", Province.class).getResultList();

        responseObject.add("provinces", AppUtil.gson.toJsonTree(provinceList));

        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }
    public String loadDistrictsDetails(int id) {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<District> districtList = hibernateSession.createQuery("FROM District d WHERE d.province.id = :id", District.class)
                .setParameter("id", id).getResultList();

        responseObject.add("districts", AppUtil.gson.toJsonTree(ContentService.district(districtList)));

        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }
    public String loadCityDetails(int id) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        /// city loading code | city loading part start
        Session HibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<City> cities = HibernateSession.createQuery("FROM City c WHERE c.district.id = :id", City.class)
                .setParameter("id", id).getResultList();

        responseObject.add("cities", AppUtil.gson.toJsonTree(ContentService.city(cities)));

        HibernateSession.close();
        /// city loading code | city loading part end

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.gson.toJson(responseObject);
    }
    private static List<JsonObject> district(List<District> districtList){
        List<JsonObject> json = new ArrayList<>();
        for(District d : districtList){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", d.getId());
            obj.addProperty("price", d.getShipping());
            obj.addProperty("name", d.getName());
            json.add(obj);
        }
        return json;
    }
    private static List<JsonObject> city(List<City> cityList){
        List<JsonObject> json = new ArrayList<>();
        for(City c : cityList){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", c.getId());
            obj.addProperty("name", c.getName());
            obj.addProperty("districtId", c.getDistrict().getId());
            obj.addProperty("districtName", c.getDistrict().getName());
            json.add(obj);
        }
        return json;
    }

    public String loadCategoryDetails() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Category> categoryList = hibernateSession.createQuery("FROM Category c", Category.class).getResultList();

        // Updated: now includes subcategories inside each category
        responseObject.add("categories", AppUtil.gson.toJsonTree(ContentService.categoriesWithSub(categoryList, hibernateSession)));

        hibernateSession.close();

        return AppUtil.gson.toJson(responseObject);
    }
    public static List<JsonObject> categoriesWithSub(List<Category> categoryList, Session session) {
        List<JsonObject> categoryJson = new ArrayList<>();

        for (Category c : categoryList) {
            JsonObject obj = new JsonObject();

            obj.addProperty("id", c.getId());
            obj.addProperty("name", c.getName());

            // Load subcategories for this category
            List<SubCategory> subCategoryList = session.createQuery(
                            "FROM SubCategory sc WHERE sc.category = :category", SubCategory.class)
                    .setParameter("category", c)
                    .getResultList();

            obj.add("subCategories", AppUtil.gson.toJsonTree(subCategories(subCategoryList)));

            categoryJson.add(obj);
        }

        return categoryJson;
    }

    public String loadSubCategoryDetails(int id){
        JsonObject responseObject = new JsonObject();

        boolean status = false;
        String message = "";

        if(id <= 0){
            message = "Please select category!";
        }else{
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            Category category = hibernateSession.find(Category.class, id);

            if(category == null){
                message = "Please provide valid category!";
            }else{
                List<SubCategory> subCategoryList = hibernateSession.createQuery("FROM SubCategory sc WHERE sc.category = :category", SubCategory.class)
                        .setParameter("category", category)
                        .getResultList();
                if(subCategoryList.isEmpty()){
                    message = "Sub-Categories not found!";
                }else{
                    responseObject.add("subCategories", AppUtil.gson.toJsonTree(ContentService.subCategories(subCategoryList)));
                    status = true;
                    message = "Sub-Category loaded successfully!";
                }
            }

            hibernateSession.close();
        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    private static List<JsonObject> subCategories(List<SubCategory> subCategoryList){
        List<JsonObject> subCategoryJson = new ArrayList<>();
        for(SubCategory sc : subCategoryList){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", sc.getId());
            obj.addProperty("name", sc.getName());
            subCategoryJson.add(obj);
        }
        return subCategoryJson;
    }

    public String loadProductSpecifications() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        List<Brand> brandList = hibernateSession.createQuery("FROM Brand b", Brand.class).getResultList();
        responseObject.add("brands", AppUtil.gson.toJsonTree(brandList));

        List<Color> colorList = hibernateSession.createQuery("FROM Color c", Color.class).getResultList();
        responseObject.add("colors", AppUtil.gson.toJsonTree(colorList));

        List<Size> sizeList = hibernateSession.createQuery("FROM Size s", Size.class).getResultList();
        responseObject.add("sizes", AppUtil.gson.toJsonTree(sizeList));

        Double minPrice = hibernateSession.createQuery("SELECT MIN(s.price) FROM Stock s WHERE s.status.id = 1 AND s.qty > 0", Double.class)
                .getSingleResult();
        Double maxPrice = hibernateSession.createQuery("SELECT MAX(s.price) FROM Stock s WHERE s.status.id = 1 AND s.qty > 0", Double.class)
                .getSingleResult();

        responseObject.addProperty("minPrice", minPrice);
        responseObject.addProperty("maxPrice", maxPrice);

        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadDiscountDetails(){
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        List<Discount> discountList =  hibernateSession.createQuery("FROM Discount d", Discount.class).getResultList();
        responseObject.add("discounts", AppUtil.gson.toJsonTree(discountList));

        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }
}
