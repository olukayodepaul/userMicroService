package darts.ng.io.usersMicroservice.util;


import darts.ng.io.usersMicroservice.change_password_on_login.entity.ChangePasswordOnLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    @Autowired
    private RedisTemplate<String, Object> template;

    public ChangePasswordOnLogin save(ChangePasswordOnLogin product, String HASH_KEY) {
        System.out.println((HASH_KEY +" "+product.getId()+" "+product));
        template.opsForHash().put(HASH_KEY, product.getId(), product);
        return product;
    }

    public List<ChangePasswordOnLogin> findAll(String HASH_KEY) {
        List<Object> objects = template.opsForHash().values(HASH_KEY);
        return objects.stream()
                .map(object -> (ChangePasswordOnLogin) object)
                .collect(Collectors.toList());
    }

    public ChangePasswordOnLogin findProductById(String id, String HASH_KEY) {
        return (ChangePasswordOnLogin) template.opsForHash().get(HASH_KEY, String.valueOf(id));
    }

    public String deleteProduct(String id, String HASH_KEY) {
        template.opsForHash().delete(HASH_KEY, String.valueOf(id));
        return "product removed !!";
    }

}
